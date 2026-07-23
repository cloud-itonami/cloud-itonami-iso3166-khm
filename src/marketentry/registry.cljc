(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `qip-incentive-election-mismatch?` (+ `qip-double-election?` /
  `qip-tier-duration-mismatch?` / `qip-tier-years`) is this vertical's
  FLAGSHIP check -- see `marketentry.facts` docstring for the Law on
  Investment Art.26 grounding. It independently recomputes TWO things
  about a QIP filing's own declared incentive package:

    1. Election EXCLUSIVITY -- Art.26 frames Option 1 (income-tax
       exemption) and Option 2 (special depreciation) as an either/or
       choice ('entitled to choose basic incentives under the following
       two options'). A filing that itemizes benefits from BOTH options
       at once is not a real entitlement under the Law -- this is a
       DOUBLE-ELECTION violation, never a value/threshold comparison.
    2. Group-tier DURATION recompute -- the filing's own
       `:qip-claimed-exemption-years` must equal the INDEPENDENTLY
       recomputed duration for its own declared `:qip-group`
       (Group 1 -> 9 years, Group 2 -> 6 years, Group 3 -> 3 years, per
       cdc.gov.kh's own Incentives-and-Schemes page).

  This is a genuinely different check SHAPE from a plain tiered-VALUE
  threshold (e.g. a registered-capital-percentage-by-investment-size
  formula) because the 'tier' here does not classify a monetary amount
  at all -- it classifies a fixed Investment Activity Category the QIP
  itself was assigned, and the check's primary failure mode is claiming
  two MUTUALLY EXCLUSIVE INCENTIVE MECHANISMS at once, not miscounting a
  threshold.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement or investment portal. It builds the
  RECORD an operator would keep, not the act of submitting a portal
  registration itself (that is `marketentry.operation`'s
  `:filing/submit`, always human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

;; ----------------------- QIP flagship check -----------------------

(def qip-group-years
  "Investment Activity Category Group -> income-tax-exemption /
  prepayment-tax-exemption duration in years, per cdc.gov.kh's own
  Incentives-and-Schemes page (Law on Investment Art.26 x Sub-Decree
  No.139 Annex 2, Khmer-only primary text not independently machine-read
  this session -- see `marketentry.facts` docstring honest gap)."
  {1 9, 2 6, 3 3})

(defn qip-tier-years
  "The INDEPENDENTLY recomputed statutory duration (years) for `group`
  (1, 2 or 3), or nil for an unrecognized group -- never guess a
  duration for a group this catalog does not carry."
  [group]
  (get qip-group-years group))

(defn qip-double-election?
  "Does `engagement` itemize benefits from BOTH Option 1 (income-tax
  exemption) AND Option 2 (special depreciation) at once? Art.26 frames
  these as a mutually exclusive EITHER/OR choice -- claiming both is
  never a real entitlement."
  [{:keys [qip-claims-option-1-benefit? qip-claims-option-2-benefit?]}]
  (and (true? qip-claims-option-1-benefit?) (true? qip-claims-option-2-benefit?)))

(defn qip-tier-duration-mismatch?
  "Does `engagement`'s own `:qip-claimed-exemption-years` differ from
  the INDEPENDENTLY recomputed Group-tier duration for its own declared
  `:qip-group`? An unrecognized/missing group is treated as a mismatch
  (no basis to accept any claimed duration)."
  [{:keys [qip-group qip-claimed-exemption-years]}]
  (not= qip-claimed-exemption-years (qip-tier-years qip-group)))

(defn qip-incentive-election-mismatch?
  "The FLAGSHIP check: true when `engagement` either double-elects
  (claims both Option 1 and Option 2 benefits) or its claimed exemption
  duration does not match the independently recomputed Group tier."
  [engagement]
  (or (qip-double-election? engagement)
      (qip-tier-duration-mismatch? engagement)))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement or
  investment portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                 "kind" "filing-draft"
                 "engagement_id" engagement-id
                 "jurisdiction" jurisdiction
                 "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                 "kind" "filing-submit"
                 "engagement_id" engagement-id
                 "jurisdiction" jurisdiction
                 "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
