# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Cambodia

## Classification

- Repository: `cloud-itonami-iso3166-khm`
- ISO 3166: `KHM` (Cambodia)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator
- Social impact: [:cambodian-sme-market-access :public-spend-transparency :cross-border-friction-reduction]

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Cambodian
  public contract
- a foreign SME or civic-tech vendor entering the public sector in
  Cambodia for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation and
  now needs public-sector market access

## Offer

- Qualified Investment Project (QIP) registration walkthrough via the
  Council for the Development of Cambodia (CDC) or the
  Municipal-Provincial Investment Sub-Committee (MPIS) One-stop Service
  (online at qip.cdc.gov.kh), under the Law on Investment of the
  Kingdom of Cambodia (promulgated 15 October 2021) -- Registration
  Certificate issuance within 20 working days per CDC's own published
  process
- QIP incentive-election advisory: helping the client choose and
  correctly document ONE of the Law on Investment's two mutually
  exclusive Art.26 basic-incentive options (Option 1 income-tax
  exemption / Option 2 special depreciation + up to 200% expense
  deduction) at the correct Investment Activity Category Group-tier
  duration (Group 1 = 9 years, Group 2 = 6 years, Group 3 = 3 years,
  per CDC's own Incentives-and-Schemes page) -- this is exactly what
  this repo's Market-Entry Compliance Governor flagship check
  (`qip-incentive-election-mismatch`) independently verifies before any
  filing is drafted or submitted
- registration walkthrough for public-procurement bidder/contractor
  registration under the Law on Public Procurement (Royal Kram
  No. NS/RKM/0523/005, 16 May 2023), administered by the General
  Department of Public Procurement (GDPP), Ministry of Economy and
  Finance
- business/tax registration checklist: Ministry of Commerce (MOC)
  business registration (businessregistration.moc.gov.kh) + General
  Department of Taxation (GDT, tax.gov.kh) taxpayer registration and
  Patent Tax certificate (Cambodia's de-facto annual
  business-operation tax registration, per GDT's own Tax Types
  listing) -- HONEST GAP: this session could not independently verify
  a specific integration mechanism (e.g. a CamDX-style single window)
  between MOC and GDT; that earlier claim in this document has been
  removed rather than repeated unverified
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone — it must be corrected
  against a cited official source first
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Cambodian-licensed counsel or a registered agent
- every requirement cites the official portal or regulation, never
  invented

## Boundary with adjacent actors (read before forking)

- **`com-etzhayyim-ooyake`** (etzhayyim/root): read-only civic-wayfinding
  mirror of government structure, non-commercial, barred from acting as
  or for the government (G3 impersonation ban). This blueprint is
  commercial and never claims to be an official channel.
- **`matsurigoto`** (etzhayyim/root): sovereign e-government statecraft —
  literally the government, for etzhayyim's own covenant or an adopting
  nation-state. This blueprint is an independent operator the government
  contracts with or that bids into its procurement — never the
  government.
- **`com-etzhayyim-toritsugi`** (etzhayyim/root): guides a consenting
  INDIVIDUAL citizen through their OWN procedure, non-profit,
  donation-only. This blueprint's client is a business operator, not an
  individual citizen, and it is commercial.
- **`legal-entity.etzhayyim.com`**: read-only aggregated company-registry
  data, no execution. This blueprint executes (gated) registrations.
- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) — a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis — the two compose (fork a COFOG-function
  blueprint AND this one to operate in Cambodia).
