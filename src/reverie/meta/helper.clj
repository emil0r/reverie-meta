(ns reverie.meta.helper
  (:require [ez-database.core :as db]
            [reverie.page :as page]
            [yesql.core :refer [defqueries]])
  (import [reverie.page Page AppPage]))

(defqueries "queries/meta/helper.sql")


(def get-page-meta nil)
(defmulti get-page-meta (fn [db serial] (type serial)))

(defmethod get-page-meta Page [db serial]
  (get-page-meta db (page/serial serial)))

(defmethod get-page-meta AppPage [db serial]
  (get-page-meta db (page/serial serial)))

(defmethod get-page-meta java.lang.Integer [db serial]
  (get-page-meta db (long serial)))

(defmethod get-page-meta java.lang.Long [db serial]
  (let [meta-data (db/query db sql-get-meta {:serial serial})]
    (distinct meta-data)))

(defmethod get-page-meta :default [_ _] nil)
