(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "KHM" 0)
        s (registry/register-submit "eng-1" "KHM" 0)]
    (is (= "KHM-DFT-000000" (get d "draft_number")))
    (is (= "KHM-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "KHM" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest qip-tier-years-table
  (is (= 9 (registry/qip-tier-years 1)))
  (is (= 6 (registry/qip-tier-years 2)))
  (is (= 3 (registry/qip-tier-years 3)))
  (is (nil? (registry/qip-tier-years 4))))

(deftest qip-double-election-detection
  (is (true? (registry/qip-double-election?
              {:qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? true})))
  (is (false? (registry/qip-double-election?
               {:qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false})))
  (is (false? (registry/qip-double-election?
               {:qip-claims-option-1-benefit? false :qip-claims-option-2-benefit? false}))))

(deftest qip-tier-duration-mismatch-detection
  (is (false? (registry/qip-tier-duration-mismatch? {:qip-group 2 :qip-claimed-exemption-years 6})))
  (is (true? (registry/qip-tier-duration-mismatch? {:qip-group 3 :qip-claimed-exemption-years 9})))
  (is (true? (registry/qip-tier-duration-mismatch? {:qip-group nil :qip-claimed-exemption-years 6}))))

(deftest qip-incentive-election-mismatch-combines-both
  (testing "clean single-option, correct-tier engagement -> no mismatch"
    (is (false? (registry/qip-incentive-election-mismatch?
                 {:qip-group 1 :qip-claimed-exemption-years 9
                  :qip-claims-option-1-benefit? false :qip-claims-option-2-benefit? true}))))
  (testing "double-election alone triggers mismatch even with correct duration"
    (is (true? (registry/qip-incentive-election-mismatch?
                {:qip-group 2 :qip-claimed-exemption-years 6
                 :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? true}))))
  (testing "tier-duration mismatch alone triggers mismatch even with single option"
    (is (true? (registry/qip-incentive-election-mismatch?
                {:qip-group 3 :qip-claimed-exemption-years 6
                 :qip-claims-option-1-benefit? true :qip-claims-option-2-benefit? false})))))
