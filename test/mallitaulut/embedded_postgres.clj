(ns mallitaulut.embedded-postgres
  "Mostly snarfed from specql:

  MIT License

  Copyright (c) 2017 Tatu Tarvainen

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the \"Software\"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE."
  (:require [next.jdbc :as jdbc]
            [clojure.string :as str])
  (:import [com.opentable.db.postgres.embedded PreparedDbProvider DatabasePreparer]))

(def ^:dynamic *db* nil)

(defn- run-statements [db resource split]
  (doseq [statement (remove str/blank?
                            (str/split (slurp resource) split))]
    (println "SQL: " statement)
    (jdbc/execute! db [statement])))

(defn- create-test-database [db]
  (run-statements db "test/database.sql" #";"))

(defn- provider []
  (PreparedDbProvider/forPreparer
    (reify DatabasePreparer
      (prepare [_ ds]
        (create-test-database ds)))))

(defonce db-provider (provider))

(defn reset-db [] (alter-var-root #'db-provider (fn [_] (provider))))

(defn datasource [] (.createDataSource db-provider))

(defn with-db [fn]
  (binding [*db* (datasource)]
    (fn)))
