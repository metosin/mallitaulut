(ns mallitaulut.core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :refer [datafiable-result-set]])
  (:import [java.sql Types]))

(defmulti column-schema :DATA_TYPE)

(defmethod column-schema Types/INTEGER [_] :int)

(defmethod column-schema Types/VARCHAR [{:keys [COLUMN_SIZE]}]
  [:string {:max COLUMN_SIZE}])

(defn- table-schema [cols]
  (into [:map]
        (map (fn [{:keys [TABLE_NAME COLUMN_NAME] :as col}]
               [(keyword TABLE_NAME COLUMN_NAME) (column-schema col)]))
        cols))

(defn- table-schemas [sql-schema-name cols]
  (->> cols
       (group-by :TABLE_NAME)
       (into {} (map (fn [[table-name cols]]
                       [(keyword sql-schema-name table-name) (table-schema cols)])))))

(defn tables [ds sql-schema-name]
  (let [sql-schema-name (name sql-schema-name)]
    (with-open [conn (jdbc/get-connection ds)]
      (->> (.. conn (getMetaData) (getColumns nil sql-schema-name nil nil))
           datafiable-result-set
           (table-schemas sql-schema-name)))))
