(ns reverie.batteries.meta.module
  (:require [ez-database.core :as db]
            [reverie.core :refer [defmodule]]
            [vlad.core :as vlad]))

(defn get-sets [{db :database}]
  (->> {:select [:id :name]
        :from [:reverie_meta_set]
        :order-by [:name]}
       (db/query db)
       (map (juxt :name :id))
       (into [["" ""]])))

(defn get-meta-data [{db :database}]
  (->> {:select [:id :name]
        :from [:reverie_meta_data]
        :order-by [:name]}
       (db/query db)
       (map (juxt :name :id))
       (into [["" ""]])))

(defn get-page-serials [{db :database}]
  (->> {:select [:serial :name]
        :from [:reverie_page]
        :where [:= :version 0]
        :order-by [:name]}
       (db/query db)
       (map (juxt :name :serial))))

(defn display-page [{db :database {:keys [page_serial]} :value}]
  (let [{:keys [name set_name meta_name]}
        (->> {:select [:p.name [:ms.name :set_name] [:md.name :meta_name]]
              :from [[:reverie_page :p]]
              :join [[:reverie_meta_page :mp] [:= :p.serial :mp.page_serial]]
              :left-join [[:reverie_meta_set :ms] [:= :ms.id :mp.meta_set_id]
                          [:reverie_meta_data :md] [:= :md.id :mp.meta_data_id]]
              :where [:and
                      [:= :p.serial page_serial]
                      [:= :p.version 0]]}
             (db/query db)
             first)]
    (format "%s [%s] {%s}" name set_name meta_name)))

(defmodule reverie/meta
  {:name "Meta"
   :interface? true
   :migration {:path "src/reverie/batteries/meta/migrations/meta"
               :automatic? true
               :table "migrations_module_reverie_meta"}
   :actions #{:view :edit}
   :required-roles {:view #{:admin :staff}
                    :edit #{:admin :staff}}
   :template :admin/main
   :entities
   {:data {:name "Meta data"
           :table :reverie_meta_data
           :interface {:display {:name {:name "Name"
                                        :link? true
                                        :sort :n
                                        :sort-name :id}}
                       :default-order :id}
           :fields {:name {:name "Name"
                           :type :text
                           :validation (vlad/attr [:name] (vlad/present))}
                    :data {:name "Data"
                           :type :textarea
                           :validation (vlad/attr [:data] (vlad/present))
                           :help "This is the data that will actually show up onthe page."}
                    :type {:name "Type"
                           :type :dropdown
                           :options [["Meta" "meta"]
                                     ["Link (javascript)" "link-js"]
                                     ["Link (CSS)" "link-css"]
                                     ["Javascript (raw)" "js"]
                                     ["CSS (raw)" "css"]]}
                    :active_p {:name "Active?"
                               :type :boolean
                               :default false}}
           :sections [{:fields [:name :type :data]}]}

    :set {:name "Set"
          :table :reverie_meta_set
          :interface {:display {:name {:name "Name"
                                       :link? true
                                       :sort :n
                                       :sort-name :id}
                                :all_pages_p {:name "All pages?"
                                              :sort :a}}
                      :default-order :id}
          :fields {:name {:name "Name"
                          :type :text
                          :validation (vlad/attr [:name] (vlad/present))}
                   :all_pages_p {:name "All pages?"
                                 :type :boolean
                                 :default false}
                   :meta {:name "Meta data"
                          :type :m2m
                          :cast :int
                          :table :reverie_meta_data
                          :options [:id :name]
                          :order :name
                          :m2m {:table :reverie_meta_set_meta_data
                                :joining [:meta_set_id :meta_data_id]}}}
          :sections [{:fields [:name :meta :all_pages_p]}]}
    :page {:name "Page"
           :table :reverie_meta_page
           :interface {:display {:page_serial {:name "Page"
                                               :fn display-page
                                               :link? true
                                               :sort :n}}
                       :default-order :page_serial}
           :fields {:page_serial {:name "Page"
                                  :type :dropdown
                                  :cast :int
                                  :options get-page-serials}
                    :meta_data_id {:name "Meta data"
                                   :type :dropdown
                                   :cast :int
                                   :options get-meta-data}
                    :meta_set_id {:name "Meta set"
                                  :type :dropdown
                                  :cast :int
                                  :options get-sets}}
           :sections [{:fields [:page_serial :meta_data_id :meta_set_id]}]}}}
  [])
