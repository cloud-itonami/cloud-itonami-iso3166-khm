(ns marketentry.facts
  "Per-jurisdiction public-sector market-entry regulatory catalog -- the
  G2-style spec-basis table the Market-Entry Compliance Governor checks
  every `:jurisdiction/assess` proposal against ('did the advisor cite an
  OFFICIAL public source for this jurisdiction's requirements, or did it
  invent one?').

  This iteration researched Cambodia's (KHM) real investment / business-
  registration / public-procurement regime directly from official
  Cambodian government sources fetched and read this session (curl +
  pdftotext where PDF, 2026-07-23):

  - **Investment / QIP incentives -- the primary source is the Law on
    Investment of the Kingdom of Cambodia**, fetched as a real,
    machine-readable 12-page PDF directly from `cdc.gov.kh` (the Council
    for the Development of Cambodia's own site, marked on its own cover
    'Unofficial translation by the CDC') and read in full via
    `pdftotext -layout`. Its own Art.2: 'This Law applies to all
    Qualified Investment Projects, Expanded Qualified Investment
    Projects and Guaranteed Investment Projects registered with the
    Council for the Development of Cambodia or Municipal-Provincial
    Investment Sub-Committees.' `cdc.gov.kh`'s own Step-by-Step-Guide
    page (own text, read directly) states the Law was 'promulgated on
    15 October 2021' and that online QIP registration runs at
    `qip.cdc.gov.kh`, with a Registration Certificate 'issued by the CDC
    or MPIS within 20 (twenty) working days' once the application is
    complete and the sector is not on the Negative List (Art.25). HONEST
    GAP: the fetched PDF text itself does not display its own Royal Kram
    promulgation number (only the SUPERSEDED 1994/2003 laws' numbers
    appear, in the transitional Art.39) -- this catalog cites the
    15 October 2021 promulgation date from `cdc.gov.kh`'s own
    step-by-step-guide page rather than from the PDF's own header.
  - **QIP basic incentives are a genuine two-option, mutually exclusive
    election** -- Art.26 (own text, read directly): 'Investment
    activities registered as QIP are entitled to choose basic incentives
    under the following two options: 1. Option 1: Income Tax exemption
    for 3 to 9 years ... 2. Option 2: Deduction of capital expenditure
    through special depreciation ... Eligibility of deducting up to 200
    percent of specific expenses ...'. The concrete duration-by-sector
    mapping is delegated by the Law itself ('shall be determined in the
    law on financial management and/or the Sub-Decree') to Sub-Decree
    No.139 ANK/BK dated 26 June 2023 ('on LoI', linked from `cdc.gov.kh`'s
    own Laws-and-Regulations page). This iteration DID fetch that
    Sub-Decree PDF directly from `cdc.gov.kh`, but it is Khmer-only and
    `pdftotext` produced unreliable/garbled text from its font encoding
    -- this iteration did NOT independently machine-read its own Annex 2
    Investment Activity Category text. Instead, the concrete Group
    1/2/3 = 9/6/3-year figures below are cited from `cdc.gov.kh`'s OWN
    English-language 'Incentives and Schemes' page (own text, read
    directly): '9 (nine) years for Group 1; 6 (six) years for Group 2;
    3 (three) years for Group 3', restated identically for both Option 1
    (income-tax exemption + prepayment-tax exemption) and Option 2
    (200%-expense-deduction + prepayment-tax exemption) periods -- a
    HIGH-confidence secondary citation (the implementing authority's own
    plain-English summary of its own Sub-Decree), not the primary Khmer
    legal text itself (honest gap, the same discipline
    cloud-itonami-iso3166-lao used for the Lao-language original of its
    own primary law).
  - **This vertical's FLAGSHIP check is grounded here** (see
    `marketentry.governor` / `marketentry.registry`): Art.26's own
    'choose ... under the following two options' framing means Option 1
    and Option 2 are MUTUALLY EXCLUSIVE -- a QIP cannot simultaneously
    claim both the income-tax-exemption bundle AND the
    special-depreciation bundle. Combined with the Group-tier duration
    (9/6/3 years), this is a genuinely different check SHAPE from every
    other iso3166 sibling this session found (grep-verified across the
    local `orgs/cloud-itonami/` checkout's `governor.cljc` files at
    build time): not a turnover formula, not a flat threshold, not a
    3-tier VALUE classification (unlike a monetary tier), not a
    bid-evaluation price adjustment, not a categorical sector-exclusion
    allow-list, not an ordered-tier eligibility preference, not a
    multi-criterion workforce-composition eligibility check, and not an
    authority-JURISDICTION-routing check between two government/
    legislative bodies (cloud-itonami-iso3166-cub's Consejo de
    Estado/Consejo de Ministros shape, reused by
    cloud-itonami-iso3166-lao's National Assembly/Provincial People's
    Assembly shape) -- it is a MUTUALLY-EXCLUSIVE INCENTIVE-OPTION
    ELECTION check (does the filing claim benefits from BOTH options at
    once?) COMPOUNDED with a Group-tier DURATION recompute (does the
    filing's own claimed exemption-year count match the independently
    recomputed Group tier?).
  - **Public procurement -- General Department of Public Procurement
    (GDPP), Ministry of Economy and Finance (MEF).** `gdpp.gov.kh`
    returned a genuine Cloudflare bot-detection challenge ('Attention
    Required! | Cloudflare', HTTP 403) to this session's live fetch --
    per this session's hard safety rule, this was NOT bypassed by any
    browser-automation or challenge-solving technique. Instead this
    iteration used the Internet Archive Wayback Machine as the
    prescribed fallback (fetched via a direct HTTP client, since the
    web-fetch tool available to this iteration could not reach
    `web.archive.org` at all -- a tool-level limitation, not a
    bot-detection bypass): a 2025-03-20 snapshot of `gdpp.gov.kh`'s own
    'ច្បាប់' (Laws) listing page (own text, read directly, Khmer) shows
    two entries titled identically 'ប្រកាសឲ្យប្រើច្បាប់ស្ដីពីលទ្ធកម្ម
    សាធារណៈ' ('promulgating the Law on Public Procurement'): one
    promulgated by Royal Kram No. NS/RKM/0523/005 dated 16 May 2023 (the
    CURRENT law this catalog cites), and an earlier one by Royal Kram
    No. NS/RKM/0112/004 dated 14 January 2012. HONEST GAP: this
    iteration did not reconcile why GDPP's own listing labels the 2012
    instrument 'ច្បាប់' (Law) when some general secondary sources
    describe Cambodia's pre-2023 procurement regime as governed by a
    Sub-Decree (No.74 ANKR.BK) rather than a National-Assembly Law --
    this catalog cites GDPP's own official listing verbatim rather than
    silently picking one description, and in any case models only the
    CURRENT (2023) law, which is unambiguous. `gdpp.gov.kh`'s own
    contact line confirms `gdpp@mef.gov.kh` -- GDPP sits inside the
    Ministry of Economy and Finance, corroborated independently by
    `tax.gov.kh` (GDT, also MEF) and `registrationservices.gov.kh`'s own
    whole-of-government service directory, which lists GDPP's bidder/
    contractor-registration system at `bidder.gdpp.gov.kh`.
  - **Business registration -- Ministry of Commerce (MOC),
    `businessregistration.moc.gov.kh`.** This session's live fetch of
    that exact URL returned HTTP 403 with a genuine 'Site Under
    Maintenance' HTML page (own served content, not a bot-challenge) --
    an honest, disclosed technical-unavailability gap, not a fabricated
    citation. The portal's real existence and MOC's role are
    independently corroborated by `registrationservices.gov.kh`'s own
    directory (own text, read directly, listing an MOC 'ប្រព័ន្ធស្វ័យ
    ប្រវត្តិកម្ម ក្នុងការស្នើសុំលិខិតអនុញ្ញាត ឬ អាជ្ញាបណ្ណពាណិជ្ជកម្ម'
    -- 'automated system for applying for commercial permits/licenses')
    and by `tax.gov.kh`'s own Quick-Links list, which includes
    'Ministry of Commerce' alongside GDT/GDCE/National Treasury. NOTE:
    `www.cambodiainvestment.gov.kh` (a domain named directly in this
    session's own task brief) did NOT resolve (DNS ENOTFOUND) this
    session -- `cdc.gov.kh`'s own Facebook link text still references
    'cambodiainvestment.gov.kh' as CDC's identity, but the live working
    domain today is `cdc.gov.kh` (also reachable via `cdc-crdb.gov.kh`,
    which itself links onward to `cdc.gov.kh` as 'Cambodian Investment
    Board (CIB)').
  - **Tax registration -- General Department of Taxation (GDT),
    `tax.gov.kh`, Ministry of Economy and Finance.** Fetched and read
    directly (English site, `/en` path -- note `/en/index.php?lang=en`
    DID trigger a Cloudflare challenge for this session and was
    abandoned in favor of the working `/en` path, not bypassed).
    Taxpayers are classified 'General / Small / Medium / Large' (own
    text, read directly). GDT's own 'Tax Types Briefly' page (own text,
    read directly, `/en/tax-types-briefly`) lists 'Patent Tax' (item 10)
    and 'Value Added Tax' (item 12) as of-record Cambodian tax types --
    grounding this catalog's `:patent-tax-*` fields (Cambodia's Patent
    Tax certificate functions as the de-facto annual business-operation
    tax registration).
  - **Companies law -- Law on Commercial Enterprises**, fetched as a
    real, machine-readable 63-page PDF directly from `cdc.gov.kh` and
    read in full via `pdftotext -layout`. Its own closing Art.304 +
    signature block (own text, read directly): 'This law is adopted by
    the National Assembly of the Kingdom of Cambodia in Phnom Penh on
    May 17th, 2005 during the 2nd Plenary session of the Third
    Legislature.' (also catalogued in `statute.facts`).
  - Coverage is reported HONESTLY (see `coverage`): a jurisdiction not
    in this table has NO spec-basis, full stop -- the advisor must not
    fabricate one, and the governor holds if it tries.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  intake/portal-registration/filing evidence set; `:legal-basis` /
  `:owner-authority` / `:provenance` are the G2 citation the governor
  requires before any `:jurisdiction/assess` proposal can commit. KHM
  deliberately carries NO `:rep-owner-authority` -- this vertical's
  flagship check is the QIP incentive-option-election/tier-duration
  gate, not a resident-representative gate (unlike
  cloud-itonami-iso3166-vnm/-tha's `-entity-missing` shape).
  `:patent-tax-*` grounds the secondary registry-boolean check
  (`patent-tax-unverified` in `marketentry.governor`). `:qip-tier-*`
  grounds this vertical's FLAGSHIP check
  (`qip-incentive-election-mismatch` in `marketentry.governor` /
  `marketentry.registry`)."
  {"KHM" {:name "Cambodia"
          :owner-authority "Council for the Development of Cambodia (CDC) / Municipal-Provincial Investment Sub-Committee (MPIS) -- Qualified Investment Project (QIP) registration and incentives; General Department of Public Procurement (GDPP), Ministry of Economy and Finance -- public procurement"
          :legal-basis "Law on Investment of the Kingdom of Cambodia (promulgated 15 October 2021, per cdc.gov.kh's own Step-by-Step-Guide page); Law on Public Procurement (Royal Kram No. NS/RKM/0523/005, 16 May 2023, per gdpp.gov.kh's own Laws listing)"
          :national-spec "QIP online registration (qip.cdc.gov.kh) via CDC/MPIS One-stop Service (Registration Certificate within 20 working days, Art.25-26) + General Department of Taxation (GDT, tax.gov.kh) tax/Patent-Tax registration + Ministry of Commerce (MOC, businessregistration.moc.gov.kh) business registration"
          :provenance "https://cdc.gov.kh/law-on-investment-2021/ ; https://cdc.gov.kh/step-by-step-guide/ ; https://cdc.gov.kh/incentives-and-schemes/ ; https://gdpp.gov.kh/download/laws (live Cloudflare-blocked this session, fetched via Wayback Machine: http://web.archive.org/web/20250320023351/https://gdpp.gov.kh/download/laws)"
          :required-evidence ["QIP Registration Certificate record (CDC or MPIS, via qip.cdc.gov.kh One-stop Service)"
                               "Ministry of Commerce (MOC) business registration record"
                               "General Department of Taxation (GDT) Patent Tax certificate record"
                               "Authorized-representative record"]
          :corporate-number-owner-authority "General Department of Taxation (GDT), Ministry of Economy and Finance"
          :corporate-number-legal-basis "Taxpayer registration (General/Small/Medium/Large classification, per tax.gov.kh's own Taxpayers page)"
          :corporate-number-provenance "https://www.tax.gov.kh/en/tax-payer"
          :patent-tax-owner-authority "General Department of Taxation (GDT), Ministry of Economy and Finance"
          :patent-tax-legal-basis "Patent Tax -- statutory Cambodian tax type per GDT's own 'Tax Types Briefly' listing (item 10, alongside Value Added Tax item 12)"
          :patent-tax-provenance "https://www.tax.gov.kh/en/tax-types-briefly"
          :qip-tier-owner-authority "Council for the Development of Cambodia (CDC) -- Law on Investment Art.24/25/26; Sub-Decree No.139 ANK/BK dated 26 June 2023 Annex 2 Investment Activity Category (Khmer-only PDF, not independently machine-read this session -- see facts.cljc docstring honest gap)"
          :qip-tier-legal-basis "Law on Investment Art.26: QIP 'entitled to choose basic incentives under the following two options' (Option 1 income-tax exemption / Option 2 special depreciation + up to 200% expense deduction), duration by Investment Activity Category Group per cdc.gov.kh's own Incentives-and-Schemes page: '9 (nine) years for Group 1; 6 (six) years for Group 2; 3 (three) years for Group 3'"
          :qip-tier-criteria {:group-1-years 9 :group-2-years 6 :group-3-years 3}
          :qip-tier-provenance "https://cdc.gov.kh/incentives-and-schemes/ ; https://cdc.gov.kh/wp-content/uploads/2022/04/LOI_English-Updated-13Dec21.pdf"}
   "USA" {:name "United States"
          :owner-authority "U.S. General Services Administration (GSA) / SAM.gov"
          :legal-basis "Federal Acquisition Regulation (FAR); System for Award Management"
          :national-spec "SAM.gov entity registration + NAICS self-certification"
          :provenance "https://sam.gov/"
          :required-evidence ["EIN record"
                               "SAM.gov registration record"
                               "State business registration record"
                               "Authorized-representative record"]}
   "SGP" {:name "Singapore" :owner-authority "GeBIZ" :legal-basis "GFR" :national-spec "GeBIZ" :provenance "https://www.gebiz.gov.sg/"
          :required-evidence ["UEN record" "GeBIZ registration" "GST record" "Authorized-representative record"]}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO spec-basis,
  and the governor must hold any proposal that tries to assess or file
  on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions actually
  have a spec-basis entry. Never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-khm R0: " (count catalog)
                 " jurisdictions seeded with an official spec-basis. "
                 "This is a starting catalog for market-entry navigation, "
                 "not a survey of all ~194 jurisdictions -- extend "
                 "`marketentry.facts/catalog`, never fabricate a "
                 "jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings) satisfy
  every evidence item listed for `iso3`? Missing spec-basis -> never
  satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))

(defn corporate-number-spec-basis
  "The jurisdiction's corporate-number / tax-id regime, or nil."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority
                        :corporate-number-legal-basis
                        :corporate-number-provenance]))))

(defn patent-tax-spec-basis
  "The jurisdiction's Patent Tax / business-operation-tax regime, or nil.
  For KHM this is real and current -- grounds the secondary
  `patent-tax-unverified` governor check."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:patent-tax-owner-authority sb)
      (select-keys sb [:patent-tax-owner-authority
                        :patent-tax-legal-basis
                        :patent-tax-provenance]))))

(defn qip-tier-spec-basis
  "The jurisdiction's QIP incentive-option/Group-tier regime, or nil. For
  KHM this is real and current -- the FLAGSHIP check this vertical adds
  is grounded here (Art.26 two-option election x Group 1/2/3 = 9/6/3-year
  duration)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:qip-tier-owner-authority sb)
      (select-keys sb [:qip-tier-owner-authority
                        :qip-tier-legal-basis
                        :qip-tier-criteria
                        :qip-tier-provenance]))))
