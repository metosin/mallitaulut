(ns mallitaulut.core-test
  (:require [clojure.test :refer :all]
            [mallitaulut.core :as taulut]
            [mallitaulut.embedded-postgres :refer [with-db *db*]]
            [next.jdbc :as jdbc]))

(use-fixtures :each with-db)

(deftest fixture-ran
  (is (= [{:count 2}] (jdbc/execute! *db* ["select count(id) from app.user"]))))

(deftest user
  (is (= [:map [:user/id :int] [:user/name [:string {:max 2147483647}]]]
         (:app/user (taulut/tables *db* :app)))))
