(ns mallitaulut.core-test
  (:require [clojure.test :refer :all]
            [mallitaulut.core :as taulut]
            [mallitaulut.embedded-postgres :refer [with-db *db*]]
            [next.jdbc :as jdbc]))

(def User
  [:map
   [:user/id [:int {:min Integer/MIN_VALUE, :max Integer/MAX_VALUE}]]
   [:user/name [:string {:max 20}]]
   [:user/initials [:string {:min 3, :max 3}]]
   [:user/admin :boolean]
   [:user/description [:string {:max 2147483647}]]
   [:user/reputation :int]
   [:user/veracity float?]
   [:user/height :double]
   [:user/hats [:int {:min Short/MIN_VALUE, :max Short/MAX_VALUE}]]])

(use-fixtures :each with-db)

(deftest fixture-ran
  (is (= [{:count 2}] (jdbc/execute! *db* ["select count(id) from app.user"]))))

(deftest user
  (is (= User (:app/user (taulut/tables *db* :app)))))
