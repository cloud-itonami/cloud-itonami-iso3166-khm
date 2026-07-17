(ns culture.facts
  "Country-level regional-culture catalog for Cambodia (KHM) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"KHM"
   [{:culture/id "khm.dish.amok"
     :culture/name "Amok (steamed curry)"
     :culture/name-local "អាម៉ុក"
     :culture/country "KHM"
     :culture/kind :dish
     :culture/summary "Southeast Asian curry steamed in banana leaves; in Khmer cuisine it is known as amok -- chopped meat, chicken or fish mixed with spices and coconut juice, placed in leaves and steamed."
     :culture/url "https://en.wikipedia.org/wiki/Steamed_curry"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.dish.kuyteav"
     :culture/name "Kuyteav"
     :culture/country "KHM"
     :culture/kind :dish
     :culture/summary "Cambodian noodle soup of rice noodles with pork stock and toppings, a popular breakfast dish across all of Cambodia."
     :culture/url "https://en.wikipedia.org/wiki/Kuyteav"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.dish.num-banhchok"
     :culture/name "Num banhchok"
     :culture/country "KHM"
     :culture/kind :dish
     :culture/summary "Lightly fermented Cambodian rice noodles and a traditional breakfast noodle dish of Cambodia."
     :culture/url "https://en.wikipedia.org/wiki/Num_banhchok"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.product.kampot-pepper"
     :culture/name "Kampot pepper"
     :culture/country "KHM"
     :culture/kind :product
     :culture/summary "Black pepper cultivar grown in Kampot and Kep provinces of southwestern Cambodia; a certified geographical indication product in Cambodia since 2010 and in the European Union since 2016."
     :culture/url "https://en.wikipedia.org/wiki/Kampot_pepper"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.product.prahok"
     :culture/name "Prahok"
     :culture/country "KHM"
     :culture/kind :product
     :culture/summary "Salted and fermented fish paste (usually of mudfish) used in Cambodian cuisine as a seasoning or condiment, originating in Cambodia as a preservation method."
     :culture/url "https://en.wikipedia.org/wiki/Prahok"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.craft.krama"
     :culture/name "Krama"
     :culture/country "KHM"
     :culture/kind :craft
     :culture/summary "Sturdy traditional Cambodian woven cloth worn by men, women and children, used as scarf, bandanna, face covering or child's hammock; a Cambodian national symbol."
     :culture/url "https://en.wikipedia.org/wiki/Krama"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.festival.bon-om-touk"
     :culture/name "Bon Om Touk"
     :culture/country "KHM"
     :culture/kind :festival
     :culture/summary "Cambodian water festival held in late October or early November, marking the end of the monsoon season and the reversal of the Tonle Sap river's flow, celebrated with dragon-boat races and illuminated floats."
     :culture/url "https://en.wikipedia.org/wiki/Bon_Om_Touk"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.festival.cambodian-new-year"
     :culture/name "Cambodian New Year"
     :culture/country "KHM"
     :culture/kind :festival
     :culture/summary "Traditional Khmer solar new year celebration, a three-day public holiday in Cambodia usually falling on 13 or 14 April, marking the end of the harvesting season."
     :culture/url "https://en.wikipedia.org/wiki/Cambodian_New_Year"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "khm.heritage.angkor"
     :culture/name "Angkor"
     :culture/country "KHM"
     :culture/kind :heritage
     :culture/summary "Former capital of the Khmer Empire near Siem Reap in present-day Cambodia, declared a UNESCO World Heritage Site in 1992."
     :culture/url "https://en.wikipedia.org/wiki/Angkor"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

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
      :note (str "cloud-itonami-iso3166-khm culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "KHM"))
                 " KHM entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
