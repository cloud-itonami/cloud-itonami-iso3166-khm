(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest khm-has-spec-basis
  (let [sb (facts/spec-basis "KHM")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "KHM")))
    (is (some? (facts/patent-tax-spec-basis "KHM")))
    (is (some? (facts/qip-tier-spec-basis "KHM")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "KHM")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "KHM" all)))
    (is (not (facts/required-evidence-satisfied? "KHM" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["KHM" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest qip-tier-spec-basis-has-criteria
  (let [tb (facts/qip-tier-spec-basis "KHM")]
    (is (= {:group-1-years 9 :group-2-years 6 :group-3-years 3}
           (:qip-tier-criteria tb)))
    (is (nil? (facts/qip-tier-spec-basis "USA")))))
