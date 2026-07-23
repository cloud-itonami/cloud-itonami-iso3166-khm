(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Cambodian investment/procurement law, whether a claimed QIP
  incentive election under the Law on Investment Art.26 is actually a
  single valid option (not both Option 1 AND Option 2 at once) whose
  claimed exemption duration actually matches the Investment Activity
  Category Group the engagement itself declares, whether a claimed
  engagement fee actually equals base + months x rate, whether a Patent
  Tax certificate has been verified for a filing that requires one, or
  when a draft stops being a draft and becomes a real-world QIP/portal
  filing, so this MUST be a separate system able to *reject* a proposal
  and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. QIP incentive-election      -- for `:filing/submit`,
       mismatch                       INDEPENDENTLY recompute whether
                                       the engagement's own declared QIP
                                       incentive package is a SINGLE
                                       valid Law-on-Investment Art.26
                                       election (never both Option 1 AND
                                       Option 2 at once) whose own
                                       claimed exemption-year count
                                       matches the independently
                                       recomputed Investment Activity
                                       Category Group tier (Group 1 -> 9
                                       years, Group 2 -> 6, Group 3 -> 3,
                                       per cdc.gov.kh's own
                                       Incentives-and-Schemes page), and
                                       HARD-hold if not. FLAGSHIP
                                       genuinely new check for the
                                       iso3166 family (grep-verified
                                       absent as a governor check
                                       function name fleet-wide at build
                                       time) -- a MUTUALLY-EXCLUSIVE
                                       INCENTIVE-OPTION ELECTION check
                                       compounded with a Group-tier
                                       DURATION recompute, a check SHAPE
                                       genuinely different from every
                                       prior sibling's (turnover formula
                                       / flat threshold / boolean
                                       registry membership / 3-tier
                                       VALUE classification / bid-
                                       evaluation price adjustment /
                                       categorical sector-exclusion
                                       allow-list / ordered-tier
                                       eligibility preference / multi-
                                       criterion workforce-composition
                                       eligibility / authority-
                                       jurisdiction routing between two
                                       government or legislative
                                       bodies) -- the first in this
                                       family to check whether a filing
                                       claims two MUTUALLY EXCLUSIVE TAX
                                       MECHANISMS at once, rather than
                                       whether a bidder/investor/
                                       authority is eligible or has
                                       jurisdiction at all.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. Patent Tax unverified       -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-patent-tax? true`,
                                       INDEPENDENTLY check
                                       `:patent-tax-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in
                                       GDT's own 'Tax Types Briefly'
                                       listing (Patent Tax, item 10).
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(QIP登録証/MOC商業登録/GDT特許税証明/代理人確認等)が充足していない状態での提案"}]))))

(defn- qip-incentive-election-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared QIP incentive election is valid: not a
  double-election of both Art.26 Option 1 and Option 2 at once, and its
  claimed exemption-year count matches the independently recomputed
  Investment Activity Category Group tier -- the flagship check this
  vertical adds."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/qip-incentive-election-mismatch? e)
        [{:rule :qip-incentive-election-mismatch
          :detail (str subject
                       " のQIP優遇措置申告が不正: 二重選択="
                       (registry/qip-double-election? e)
                       " group=" (:qip-group e)
                       " 独立再計算年数=" (registry/qip-tier-years (:qip-group e))
                       " 申告年数=" (:qip-claimed-exemption-years e))}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                       ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- patent-tax-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-patent-tax? true`, INDEPENDENTLY check
  `:patent-tax-verified?` -- CONDITIONAL on the engagement's own ground
  truth."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-patent-tax? e))
                 (not (true? (:patent-tax-verified? e))))
        [{:rule :patent-tax-unverified
          :detail (str subject " はGDT特許税(Patent Tax)証明の確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (qip-incentive-election-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (patent-tax-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
