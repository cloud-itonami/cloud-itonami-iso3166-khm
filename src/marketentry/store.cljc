(ns marketentry.store
  "SSoT for the KHM market-entry compliance actor, behind a `Store`
  protocol so the backend is a swap, not a rewrite -- the same seam
  every prior cloud-itonami actor in this fleet uses.

    - `MemStore`     -- atom of EDN. The deterministic default for
                        dev/tests/demo (no deps).
    - `DatomicStore` -- backed by `langchain.db`, a Datomic-API-compatible
                        EAV store.

  Both implement the same protocol and pass the same contract
  (test/marketentry/store_contract_test.clj).

  The primary entity here is an `engagement` -- filing-draft and
  filing-submit actuation events apply SEQUENTIALLY to the SAME
  engagement record (draft first, submit later). Dedicated
  double-actuation-guard booleans (`:drafted?`/`:submitted?`, never a
  `:status` value).

  The ledger stays append-only on every backend."
  (:require #?(:clj  [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])
            [marketentry.registry :as registry]
            [langchain.db :as d]))

(defprotocol Store
  (engagement [s id])
  (all-engagements [s])
  (assessment-of [s engagement-id] "committed jurisdiction assessment, or nil")
  (ledger [s])
  (draft-history [s] "the append-only filing-draft history")
  (submit-history [s] "the append-only filing-submit history")
  (next-draft-sequence [s jurisdiction])
  (next-submit-sequence [s jurisdiction])
  (engagement-already-drafted? [s engagement-id])
  (engagement-already-submitted? [s engagement-id])
  (commit-record! [s record] "apply a committed op's record to the SSoT")
  (append-ledger! [s fact]   "append one immutable decision fact")
  (with-engagements [s engagements] "replace/seed the engagement directory"))

;; ----------------------------- demo data -----------------------------

(defn demo-data
  "A small, self-contained engagement set covering both actuation
  lifecycles (draft, submit) plus the governor's own new checks:
  eng-1 clean (Option 1, Group 2 -> 6yr, patent tax verified), eng-2
  a foreign-jurisdiction test fixture (no spec-basis), eng-3 fee
  mismatch, eng-4 QIP double-election (both options claimed), eng-5
  QIP tier-duration mismatch (Group 3 claims 9yr instead of 3yr), eng-6
  patent-tax unverified, eng-7 a second clean engagement (Option 2,
  Group 1 -> 9yr)."
  []
  {:engagements
   {"eng-1" {:id "eng-1" :operator "Angkor Bridge Systems" :portal "qip.cdc.gov.kh"
             :base-fee 500000 :monthly-rate 30000 :monitoring-months 12
             :claimed-fee 860000.0
             :qip-group 2 :qip-incentive-option :option-1
             :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false
             :qip-claimed-exemption-years 6
             :requires-patent-tax? true :patent-tax-verified? true
             :drafted? false :submitted? false
             :jurisdiction "KHM" :status :intake}
    "eng-2" {:id "eng-2" :operator "Atlantis LLC" :portal "qip.cdc.gov.kh"
             :base-fee 500000 :monthly-rate 30000 :monitoring-months 12
             :claimed-fee 860000.0
             :qip-group 2 :qip-incentive-option :option-1
             :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false
             :qip-claimed-exemption-years 6
             :requires-patent-tax? true :patent-tax-verified? true
             :drafted? false :submitted? false
             :jurisdiction "ATL" :status :intake}
    "eng-3" {:id "eng-3" :operator "Tonle Sap Trading" :portal "qip.cdc.gov.kh"
             :base-fee 500000 :monthly-rate 30000 :monitoring-months 12
             :claimed-fee 999000.0
             :qip-group 2 :qip-incentive-option :option-1
             :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false
             :qip-claimed-exemption-years 6
             :requires-patent-tax? true :patent-tax-verified? true
             :drafted? false :submitted? false
             :jurisdiction "KHM" :status :intake}
    "eng-4" {:id "eng-4" :operator "Mekong Export Partners" :portal "qip.cdc.gov.kh"
             :base-fee 500000 :monthly-rate 30000 :monitoring-months 12
             :claimed-fee 860000.0
             :qip-group 2 :qip-incentive-option :option-1
             :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? true
             :qip-claimed-exemption-years 6
             :requires-patent-tax? true :patent-tax-verified? true
             :drafted? false :submitted? false
             :jurisdiction "KHM" :status :intake}
    "eng-5" {:id "eng-5" :operator "Kampot Agro Processing" :portal "qip.cdc.gov.kh"
             :base-fee 500000 :monthly-rate 30000 :monitoring-months 12
             :claimed-fee 860000.0
             :qip-group 3 :qip-incentive-option :option-1
             :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false
             :qip-claimed-exemption-years 9
             :requires-patent-tax? true :patent-tax-verified? true
             :drafted? false :submitted? false
             :jurisdiction "KHM" :status :intake}
    "eng-6" {:id "eng-6" :operator "Sihanoukville Logistics" :portal "qip.cdc.gov.kh"
             :base-fee 500000 :monthly-rate 30000 :monitoring-months 12
             :claimed-fee 860000.0
             :qip-group 2 :qip-incentive-option :option-1
             :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false
             :qip-claimed-exemption-years 6
             :requires-patent-tax? true :patent-tax-verified? false
             :drafted? false :submitted? false
             :jurisdiction "KHM" :status :intake}
    "eng-7" {:id "eng-7" :operator "Battambang Digital Works" :portal "qip.cdc.gov.kh"
             :base-fee 400000 :monthly-rate 25000 :monitoring-months 6
             :claimed-fee 550000.0
             :qip-group 1 :qip-incentive-option :option-2
             :qip-claims-option-1-benefit? false :qip-claims-option-2-benefit? true
             :qip-claimed-exemption-years 9
             :requires-patent-tax? true :patent-tax-verified? true
             :drafted? false :submitted? false
             :jurisdiction "KHM" :status :intake}}})

;; ----------------------------- shared commit logic -----------------------------

(defn- draft-filing!
  [s engagement-id]
  (let [e (engagement s engagement-id)
        seq-n (next-draft-sequence s (:jurisdiction e))
        result (registry/register-draft engagement-id (:jurisdiction e) seq-n)]
    {:result result
     :engagement-patch {:drafted? true
                         :draft-number (get result "draft_number")}}))

(defn- submit-filing!
  [s engagement-id]
  (let [e (engagement s engagement-id)
        seq-n (next-submit-sequence s (:jurisdiction e))
        result (registry/register-submit engagement-id (:jurisdiction e) seq-n)]
    {:result result
     :engagement-patch {:submitted? true
                         :submit-number (get result "submit_number")}}))

;; ----------------------------- MemStore (default) -----------------------------

(defrecord MemStore [a]
  Store
  (engagement [_ id] (get-in @a [:engagements id]))
  (all-engagements [_] (sort-by :id (vals (:engagements @a))))
  (assessment-of [_ engagement-id] (get-in @a [:assessments engagement-id]))
  (ledger [_] (:ledger @a))
  (draft-history [_] (:draft-records @a))
  (submit-history [_] (:submit-records @a))
  (next-draft-sequence [_ jurisdiction] (get-in @a [:draft-sequences jurisdiction] 0))
  (next-submit-sequence [_ jurisdiction] (get-in @a [:submit-sequences jurisdiction] 0))
  (engagement-already-drafted? [_ engagement-id] (boolean (get-in @a [:engagements engagement-id :drafted?])))
  (engagement-already-submitted? [_ engagement-id] (boolean (get-in @a [:engagements engagement-id :submitted?])))
  (commit-record! [s {:keys [effect path value payload]}]
    (case effect
      :engagement/upsert
      (swap! a update-in [:engagements (:id value)] merge value)

      :assessment/set
      (swap! a assoc-in [:assessments (first path)] payload)

      :engagement/mark-drafted
      (let [engagement-id (first path)
            {:keys [result engagement-patch]} (draft-filing! s engagement-id)
            jurisdiction (:jurisdiction (engagement s engagement-id))]
        (swap! a (fn [state]
                   (-> state
                       (update-in [:draft-sequences jurisdiction] (fnil inc 0))
                       (update-in [:engagements engagement-id] merge engagement-patch)
                       (update :draft-records registry/append result))))
        result)

      :engagement/mark-submitted
      (let [engagement-id (first path)
            {:keys [result engagement-patch]} (submit-filing! s engagement-id)
            jurisdiction (:jurisdiction (engagement s engagement-id))]
        (swap! a (fn [state]
                   (-> state
                       (update-in [:submit-sequences jurisdiction] (fnil inc 0))
                       (update-in [:engagements engagement-id] merge engagement-patch)
                       (update :submit-records registry/append result))))
        result)
      nil)
    s)
  (append-ledger! [_ fact] (swap! a update :ledger conj fact) fact)
  (with-engagements [s engagements] (when (seq engagements) (swap! a assoc :engagements engagements)) s))

(defn seed-db
  "A MemStore seeded with the demo engagement set."
  []
  (->MemStore (atom (assoc (demo-data)
                            :assessments {}
                            :ledger [] :draft-sequences {} :draft-records []
                            :submit-sequences {} :submit-records []))))

;; ----------------------------- DatomicStore (langchain.db) -----------------------------

(def ^:private schema
  {:engagement/id                   {:db/unique :db.unique/identity}
   :assessment/engagement-id        {:db/unique :db.unique/identity}
   :ledger/seq                      {:db/unique :db.unique/identity}
   :draft-record/seq                {:db/unique :db.unique/identity}
   :submit-record/seq               {:db/unique :db.unique/identity}
   :draft-sequence/jurisdiction     {:db/unique :db.unique/identity}
   :submit-sequence/jurisdiction    {:db/unique :db.unique/identity}})

(defn- enc [v] (pr-str v))
(defn- dec* [s] (when s (edn/read-string s)))

(defn- engagement->tx [{:keys [id operator portal base-fee monthly-rate monitoring-months claimed-fee
                                qip-group qip-incentive-option
                                qip-claims-option-1-benefit? qip-claims-option-2-benefit?
                                qip-claimed-exemption-years
                                requires-patent-tax? patent-tax-verified?
                                drafted? submitted?
                                jurisdiction status draft-number submit-number]}]
  (cond-> {:engagement/id id}
    operator                                (assoc :engagement/operator operator)
    portal                                  (assoc :engagement/portal portal)
    base-fee                                (assoc :engagement/base-fee base-fee)
    monthly-rate                            (assoc :engagement/monthly-rate monthly-rate)
    monitoring-months                       (assoc :engagement/monitoring-months monitoring-months)
    claimed-fee                             (assoc :engagement/claimed-fee claimed-fee)
    qip-group                               (assoc :engagement/qip-group qip-group)
    qip-incentive-option                    (assoc :engagement/qip-incentive-option qip-incentive-option)
    (some? qip-claims-option-1-benefit?)    (assoc :engagement/qip-claims-option-1-benefit? qip-claims-option-1-benefit?)
    (some? qip-claims-option-2-benefit?)    (assoc :engagement/qip-claims-option-2-benefit? qip-claims-option-2-benefit?)
    qip-claimed-exemption-years             (assoc :engagement/qip-claimed-exemption-years qip-claimed-exemption-years)
    (some? requires-patent-tax?)            (assoc :engagement/requires-patent-tax? requires-patent-tax?)
    (some? patent-tax-verified?)            (assoc :engagement/patent-tax-verified? patent-tax-verified?)
    (some? drafted?)                        (assoc :engagement/drafted? drafted?)
    (some? submitted?)                      (assoc :engagement/submitted? submitted?)
    jurisdiction                            (assoc :engagement/jurisdiction jurisdiction)
    status                                  (assoc :engagement/status status)
    draft-number                            (assoc :engagement/draft-number draft-number)
    submit-number                           (assoc :engagement/submit-number submit-number)))

(def ^:private engagement-pull
  [:engagement/id :engagement/operator :engagement/portal :engagement/base-fee :engagement/monthly-rate
   :engagement/monitoring-months :engagement/claimed-fee
   :engagement/qip-group :engagement/qip-incentive-option
   :engagement/qip-claims-option-1-benefit? :engagement/qip-claims-option-2-benefit?
   :engagement/qip-claimed-exemption-years
   :engagement/requires-patent-tax? :engagement/patent-tax-verified?
   :engagement/drafted? :engagement/submitted?
   :engagement/jurisdiction :engagement/status :engagement/draft-number :engagement/submit-number])

(defn- pull->engagement [m]
  (when (:engagement/id m)
    {:id (:engagement/id m) :operator (:engagement/operator m) :portal (:engagement/portal m)
     :base-fee (:engagement/base-fee m) :monthly-rate (:engagement/monthly-rate m)
     :monitoring-months (:engagement/monitoring-months m) :claimed-fee (:engagement/claimed-fee m)
     :qip-group (:engagement/qip-group m) :qip-incentive-option (:engagement/qip-incentive-option m)
     :qip-claims-option-1-benefit? (boolean (:engagement/qip-claims-option-1-benefit? m))
     :qip-claims-option-2-benefit? (boolean (:engagement/qip-claims-option-2-benefit? m))
     :qip-claimed-exemption-years (:engagement/qip-claimed-exemption-years m)
     :requires-patent-tax? (boolean (:engagement/requires-patent-tax? m))
     :patent-tax-verified? (boolean (:engagement/patent-tax-verified? m))
     :drafted? (boolean (:engagement/drafted? m)) :submitted? (boolean (:engagement/submitted? m))
     :jurisdiction (:engagement/jurisdiction m) :status (:engagement/status m)
     :draft-number (:engagement/draft-number m) :submit-number (:engagement/submit-number m)}))

(defrecord DatomicStore [conn]
  Store
  (engagement [_ id]
    (pull->engagement (d/pull (d/db conn) engagement-pull [:engagement/id id])))
  (all-engagements [_]
    (->> (d/q '[:find [?id ...] :where [?e :engagement/id ?id]] (d/db conn))
         (map #(pull->engagement (d/pull (d/db conn) engagement-pull [:engagement/id %])))
         (sort-by :id)))
  (assessment-of [_ engagement-id]
    (dec* (d/q '[:find ?p . :in $ ?eid
                 :where [?a :assessment/engagement-id ?eid] [?a :assessment/payload ?p]]
               (d/db conn) engagement-id)))
  (ledger [_]
    (->> (d/q '[:find ?s ?f :where [?e :ledger/seq ?s] [?e :ledger/fact ?f]] (d/db conn))
         (sort-by first)
         (mapv (comp dec* second))))
  (draft-history [_]
    (->> (d/q '[:find ?s ?r :where [?e :draft-record/seq ?s] [?e :draft-record/record ?r]] (d/db conn))
         (sort-by first)
         (mapv (comp dec* second))))
  (submit-history [_]
    (->> (d/q '[:find ?s ?r :where [?e :submit-record/seq ?s] [?e :submit-record/record ?r]] (d/db conn))
         (sort-by first)
         (mapv (comp dec* second))))
  (next-draft-sequence [_ jurisdiction]
    (or (d/q '[:find ?n . :in $ ?j
               :where [?e :draft-sequence/jurisdiction ?j] [?e :draft-sequence/next ?n]]
             (d/db conn) jurisdiction)
        0))
  (next-submit-sequence [_ jurisdiction]
    (or (d/q '[:find ?n . :in $ ?j
               :where [?e :submit-sequence/jurisdiction ?j] [?e :submit-sequence/next ?n]]
             (d/db conn) jurisdiction)
        0))
  (engagement-already-drafted? [s engagement-id]
    (boolean (:drafted? (engagement s engagement-id))))
  (engagement-already-submitted? [s engagement-id]
    (boolean (:submitted? (engagement s engagement-id))))
  (commit-record! [s {:keys [effect path value payload]}]
    (case effect
      :engagement/upsert
      (d/transact! conn [(engagement->tx value)])

      :assessment/set
      (d/transact! conn [{:assessment/engagement-id (first path) :assessment/payload (enc payload)}])

      :engagement/mark-drafted
      (let [engagement-id (first path)
            {:keys [result engagement-patch]} (draft-filing! s engagement-id)
            jurisdiction (:jurisdiction (engagement s engagement-id))
            next-n (inc (next-draft-sequence s jurisdiction))]
        (d/transact! conn
                     [(engagement->tx (assoc engagement-patch :id engagement-id))
                      {:draft-sequence/jurisdiction jurisdiction :draft-sequence/next next-n}
                      {:draft-record/seq (count (draft-history s)) :draft-record/record (enc (get result "record"))}])
        result)

      :engagement/mark-submitted
      (let [engagement-id (first path)
            {:keys [result engagement-patch]} (submit-filing! s engagement-id)
            jurisdiction (:jurisdiction (engagement s engagement-id))
            next-n (inc (next-submit-sequence s jurisdiction))]
        (d/transact! conn
                     [(engagement->tx (assoc engagement-patch :id engagement-id))
                      {:submit-sequence/jurisdiction jurisdiction :submit-sequence/next next-n}
                      {:submit-record/seq (count (submit-history s)) :submit-record/record (enc (get result "record"))}])
        result)
      nil)
    s)
  (append-ledger! [s fact]
    (d/transact! conn [{:ledger/seq (count (ledger s)) :ledger/fact (enc fact)}])
    fact)
  (with-engagements [s engagements]
    (when (seq engagements) (d/transact! conn (mapv engagement->tx (vals engagements)))) s))

(defn datomic-store
  ([] (datomic-store {}))
  ([{:keys [engagements]}]
   (let [s (->DatomicStore (d/create-conn schema))]
     (with-engagements s engagements))))

(defn datomic-seed-db
  []
  (datomic-store (demo-data)))
