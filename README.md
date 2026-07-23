# cloud-itonami-iso3166-khm

Open ISO 3166 Blueprint for **KHM**: Cambodia.

**`:implemented`** for **KHM**. Flagship `qip-incentive-election-mismatch`, tax `patent-tax-unverified`.

```
clojure -M:dev:test
```

This repository designs a forkable OSS business for an independent
public-sector market-entry consultant: an already-incorporated operator
(e.g. a `cloud-itonami-cofog-{code}`, `cloud-itonami-isco-{code}`,
`cloud-itonami-unspsc-{segment}` or `cloud-itonami-{ISIC}` blueprint
fork) gets a Compliance Advisor + independent **Market-Entry Compliance
Governor** to navigate public-procurement registration, local business/
tax registration, and local-content rules in Cambodia, so the operator
can win and service a government contract without hiring a full in-house
compliance department.

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) — the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set — it always requires human sign-off (mirrors
`cloud-itonami-M6910`'s `filing-submit-never-auto-at-any-phase`
invariant).

## What this is NOT

- **Not the government of Cambodia.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary with
  `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto` (sovereign
  statecraft), `com-etzhayyim-toritsugi` (individual citizen concierge),
  `legal-entity.etzhayyim.com` (read-only data aggregation), and
  `cloud-itonami-M6910` (company incorporation — a different regulatory
  phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Cambodian-licensed counsel
  or a registered agent where the law requires licensed representation.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `KHM`). Required capabilities:

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as `cloud-itonami-iso3166-vnm`/`-tha`/`-lao`, grounded in Cambodia's real
investment/procurement regime, researched directly from official sources
(verified 2026-07-23, see the namespace docstrings for the full research
trail, including facts this iteration could NOT verify):

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the Law on
  Investment of the Kingdom of Cambodia (promulgated 15 October 2021 per
  `cdc.gov.kh`'s own text), fetched and read as a real 12-page
  machine-readable PDF from `cdc.gov.kh` (the Council for the
  Development of Cambodia's own site). `governor.cljc`'s flagship check
  (`qip-incentive-election-mismatch`) independently recomputes two
  things about a Qualified Investment Project (QIP) filing's own
  declared incentive package under Art.26: (1) that it elects a SINGLE
  valid option -- Option 1 (income-tax exemption) or Option 2 (special
  depreciation) -- never both at once ('entitled to choose basic
  incentives under the following two options'), and (2) that its
  claimed exemption-year count matches the independently recomputed
  Investment Activity Category Group tier (Group 1 -> 9 years, Group 2
  -> 6 years, Group 3 -> 3 years, per `cdc.gov.kh`'s own
  Incentives-and-Schemes page). This is a MUTUALLY-EXCLUSIVE
  INCENTIVE-OPTION ELECTION check compounded with a Group-tier DURATION
  recompute -- grep-verified absent fleet-wide, a genuinely different
  shape from every other iso3166 sibling's turnover formula / flat
  threshold / registry-boolean / 3-tier value classification / bid-price
  adjustment / sector-exclusion allow-list / ordered-tier preference /
  workforce-composition / authority-jurisdiction-routing checks.
- `src/statute/facts.cljc` -- general-law catalog: the Law on Investment
  itself, the Law on Commercial Enterprises (adopted by the National
  Assembly 17 May 2005, fetched as a real 63-page PDF from `cdc.gov.kh`
  and read directly), and the Law on Public Procurement (Royal Kram
  No. NS/RKM/0523/005, 16 May 2023).

Honest gaps this iteration disclosed rather than silently resolved:
`gdpp.gov.kh` (General Department of Public Procurement) returned a
genuine Cloudflare bot-detection challenge to a live fetch this session
-- per this session's hard safety rule this was NOT bypassed; the Law on
Public Procurement's title/number/date were instead confirmed from a
2025-03-20 Internet Archive Wayback Machine snapshot of GDPP's own laws
listing (its substantive articles were not independently read this
session). `businessregistration.moc.gov.kh` (Ministry of Commerce)
returned a genuine 'Site Under Maintenance' page this session -- a real
technical-unavailability gap, not a bot-block, disclosed rather than
worked around. Sub-Decree No.139 (26 June 2023), which implements the
Law on Investment's Annex 2 Investment Activity Category groups, is
Khmer-only and this iteration's PDF text extraction was unreliable for
it -- the concrete Group 1/2/3 = 9/6/3-year figures are instead cited
from CDC's own English-language Incentives-and-Schemes webpage (a
high-confidence secondary citation from the implementing authority
itself, not the primary Khmer legal text).

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Cambodia:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
