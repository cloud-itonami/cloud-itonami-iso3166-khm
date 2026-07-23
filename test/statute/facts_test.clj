(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest khm-has-spec-basis
  (let [sb (facts/spec-basis "KHM")]
    (is (= 3 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "http") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["KHM" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["khm.law-on-public-procurement-2023"]
         (mapv :statute/id (facts/by-topic "KHM" :public-procurement))))
  (is (= ["khm.law-on-investment-2021"]
         (mapv :statute/id (facts/by-topic "KHM" :foreign-investment))))
  (is (empty? (facts/by-topic "KHM" :labor)))
  (is (empty? (facts/by-topic "ATL" :public-procurement))))
