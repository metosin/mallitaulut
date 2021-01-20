(ns mallitaulut.core-test
  (:require [clojure.test :refer :all]
            [mallitaulut.embedded-postgres :refer [with-db *db*]]
            [next.jdbc :as jdbc]))

(use-fixtures :each with-db)

(deftest fixture-ran
  (is (= [{:count 2}] (jdbc/execute! *db* ["select count(id) from app.user"]))))
