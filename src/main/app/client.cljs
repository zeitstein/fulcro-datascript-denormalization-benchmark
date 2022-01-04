(ns app.client
  (:require
   [app.generators :as gen]
   [datascript.core :as d]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro.algorithms.denormalize :as de]))

(defsc Node [_ _]
  {:query [:id :text :open {:kida 10}]
   :ident :id})

(defsc UI [_ _]
  {:query [{:root (comp/get-query Node)}]}
  (dom/div "..."))

(defonce APP (app/fulcro-app))

;;* DS
(def root-id "0")

(def schema
  {:id {:db/unique :db.unique/identity}
   :kida {:db/valueType :db.type/ref
          :db/cardinality :db.cardinality/many
          :db/isComponent true}})

(def conn (d/create-conn schema))
(def data (gen/simple-tree root-id 5 5))
(d/transact! conn data)

(def ds-tree (d/pull @conn (comp/get-query Node) [:id root-id]))


(defn ^:export init []
  (app/mount! APP UI "app")
  (merge/merge-component! APP Node ds-tree :replace [:root]))


(comment
  (def fulcro-tree (de/db->tree (comp/get-query Node)
                                [:id root-id]
                                (app/current-state APP)))
  (= ds-tree fulcro-tree)

  ;; I find DS is faster by a factor of 2-3
  (simple-benchmark []
                    (d/pull @conn (comp/get-query Node) [:id root-id])
                    100)

  (simple-benchmark []
                    (de/db->tree (comp/get-query Node)
                                 [:id root-id]
                                 (app/current-state APP))
                    100)
  ;
  )
