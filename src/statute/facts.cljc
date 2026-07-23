(ns statute.facts
  "General-law compliance catalog for Cambodia (KHM) -- extends this
  repo's existing `marketentry.facts` (narrow public-procurement/QIP
  scope) with a second, orthogonal catalog of statutes a company
  generally must track for compliance. Mirrors
  cloud-itonami-iso3166-jpn/-usa/-vnm/-tha/-lao/-... `statute.facts`
  (ADR-2607141700, cloud-itonami-compliance-fact-federation).

  All three entries fetched and read directly from official Cambodian
  sources this session (2026-07-23):

  - **Law on Investment of the Kingdom of Cambodia** -- fetched as a
    real, machine-readable 12-page PDF directly from `cdc.gov.kh` (the
    Council for the Development of Cambodia's own site, own text marked
    'Unofficial translation by the CDC'), read in full via
    `pdftotext -layout`. Promulgation date (15 October 2021) cited from
    `cdc.gov.kh`'s own Step-by-Step-Guide page (own text, read
    directly) -- the PDF's own extracted text does not display its own
    Royal Kram number (only the superseded 1994/2003 predecessors'
    numbers appear in the transitional Art.39), an honestly-disclosed
    gap.
  - **Law on Commercial Enterprises** -- fetched as a real,
    machine-readable 63-page PDF directly from `cdc.gov.kh`, read in
    full via `pdftotext -layout`. Its own closing Art.304 + signature
    block (own text, read directly): 'This law is adopted by the
    National Assembly of the Kingdom of Cambodia in Phnom Penh on
    May 17th, 2005 during the 2nd Plenary session of the Third
    Legislature.'
  - **Law on Public Procurement** -- this session's live fetch of
    `gdpp.gov.kh` (General Department of Public Procurement, Ministry
    of Economy and Finance) hit a genuine Cloudflare bot-detection
    challenge and was NOT bypassed (per this session's hard safety
    rule); the citation instead comes from a 2025-03-20 Internet
    Archive Wayback Machine snapshot of `gdpp.gov.kh`'s own 'ច្បាប់'
    (Laws) listing page (own text, read directly, fetched via a direct
    HTTP client since the web-fetch tool available to this iteration
    could not reach `web.archive.org` itself): 'ព្រះរាជក្រមលេខ
    នស/រកម/០៥២៣/០០៥ ... ប្រកាសឲ្យប្រើច្បាប់ស្ដីពីលទ្ធកម្មសាធារណៈ' --
    Royal Kram No. NS/RKM/0523/005, dated 16 May 2023, promulgating the
    Law on Public Procurement. This iteration did NOT independently
    fetch/read the Law's own substantive PDF text this session (only
    the listing-page title/number/date) -- its existence, title,
    Royal-Kram number and date are HIGH confidence (read directly from
    the administering authority's own official listing), its
    substantive articles are NOT read this session (honest gap, the
    same discipline other iso3166 siblings use for un-fetched
    implementing instruments).

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries."
  {"KHM"
   [{:statute/id "khm.law-on-investment-2021"
     :statute/title "Law on Investment of the Kingdom of Cambodia"
     :statute/jurisdiction "KHM"
     :statute/kind :law
     :statute/law-number "Unofficial translation, CDC (Royal Kram number not shown in the fetched English text -- see facts.cljc docstring honest gap)"
     :statute/url "https://cdc.gov.kh/wp-content/uploads/2022/04/LOI_English-Updated-13Dec21.pdf"
     :statute/url-provenance :cdc-official-translation
     :statute/enacted-date "2021-10-15"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:foreign-investment :corporate-governance :tax-incentives}}
    {:statute/id "khm.law-on-commercial-enterprises-2005"
     :statute/title "Law on Commercial Enterprises"
     :statute/jurisdiction "KHM"
     :statute/kind :law
     :statute/law-number "Adopted by the National Assembly, 2nd Plenary Session of the Third Legislature (own text, no separate promulgation number extracted)"
     :statute/url "https://cdc.gov.kh/wp-content/uploads/2022/04/Law-on-Commercial-Enterprises_English_050517.pdf"
     :statute/url-provenance :cdc-official-translation
     :statute/enacted-date "2005-05-17"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "khm.law-on-public-procurement-2023"
     :statute/title "Law on Public Procurement"
     :statute/jurisdiction "KHM"
     :statute/kind :law
     :statute/law-number "Royal Kram No. NS/RKM/0523/005"
     :statute/url "http://web.archive.org/web/20250320023351/https://gdpp.gov.kh/download/laws"
     :statute/url-provenance :gdpp-official-listing-via-wayback-machine
     :statute/enacted-date "2023-05-16"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:public-procurement}}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-khm statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "KHM")) " KHM statutes seeded with "
                 "cdc.gov.kh / gdpp.gov.kh (via Wayback Machine) citations. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
