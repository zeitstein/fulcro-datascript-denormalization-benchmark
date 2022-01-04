(ns app.generators)

(defn grow-id [base id]
  (str base "-" id))

(defn simple-node
  "For testing purposes."
  ([id]
   {:id id :open false :text (str "node " id)})
  ([id num-kida]
   (if (= 0 num-kida)
     (simple-node id)
     {:id id :open false :text (str "node " id)
      :kida (mapv #(assoc {} :id (grow-id id %)) (range num-kida))})))

(defn simple-tree
  "For testing purposes – ugly but works. Tree of [[simple-node]]s *for tx*."
  [starting-id num-kida max-tree-depth]
  (letfn [(simple-tree*
            ([id depth]
             (if (= depth max-tree-depth)
               [(simple-node id)]
               (vec (concat [(simple-node id num-kida)]
                            (flatten (mapv #(simple-tree* (grow-id id %) (inc depth))
                                           (range num-kida))))))))]
    (simple-tree* starting-id 0)))
