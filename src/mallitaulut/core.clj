(ns mallitaulut.core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :refer [datafiable-result-set]])
  (:import [java.sql Types]))

;;;; # Schemas for SQL Types

(defmulti column-schema :DATA_TYPE)

;;;; ## Integral Types

(defmethod column-schema Types/BIGINT [_] :int)

(defmethod column-schema Types/INTEGER [_]
  [:int {:min Integer/MIN_VALUE, :max Integer/MAX_VALUE}])

(defmethod column-schema Types/SMALLINT [_]
  [:int {:min Short/MIN_VALUE, :max Short/MAX_VALUE}])

;;;; ## Floating Point

(defmethod column-schema Types/DOUBLE [_] :double)

(defmethod column-schema Types/REAL [_] float?)

;;;; ## Booleans

(defmethod column-schema Types/BOOLEAN [_] :boolean)

;; TODO: Is this really correct? What about other BIT string lengths?
(defmethod column-schema Types/BIT [{:keys [COLUMN_SIZE]}]
  (assert (= COLUMN_SIZE 1))
  :boolean)

;;;; ## Strings

(defmethod column-schema Types/CHAR [{:keys [COLUMN_SIZE]}]
  [:string {:min COLUMN_SIZE, :max COLUMN_SIZE}])

(defmethod column-schema Types/VARCHAR [{:keys [COLUMN_SIZE]}]
  [:string {:max COLUMN_SIZE}])

;;;; # Read Metadata

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

;;;; # API

(defn tables [ds sql-schema-name]
  (let [sql-schema-name (name sql-schema-name)]
    (with-open [conn (jdbc/get-connection ds)]
      (->> (.. conn (getMetaData) (getColumns nil sql-schema-name nil nil))
           datafiable-result-set
           (table-schemas sql-schema-name)))))
