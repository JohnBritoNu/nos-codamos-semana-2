(ns clobank.alternative
  (:require [clobank.db :as cb.db]
            [java-time :as jt]))

(defn get-total [v]
  (->> v
       (map #(get % :amount))
       (reduce +)))

(defn transform-group-with-sum
  [[k, v]]
  {
   k (get-total v)})

(defn group-total-by-category
  [ccs]
  (->> ccs
       (map :transactions)
       (reduce into [])
       (group-by :category)
       (map transform-group-with-sum)))

(defn group-total-by-merchant
  [ccs]
  (->> ccs
       (map :transactions)
       (reduce into [])
       (group-by :merchant)))

(defn monthly-charge
  [css]
  (->> css
       (map :transactions)
       (reduce into [])
       (when (= (jt/local-date :month-of-year)))))

(defn format-transaction
  [transaction]
  (-> transaction
      (update :date #(str (jt/format "dd/MM/yyyy hh:mm" %)))
      (update :amount #(str "R$ " %))))

(defn show-formatted-transactions
  [transaction]
  (println transaction)
  (println "\nData:" (:date transaction)
           "\nValor:" (:amount transaction)
           "\nEstabelecimento:" (:merchant transaction)
           "\nCategoria:" (:category transaction)))

(defn get-all-credit-cards
  [customer]
  (->> customer
       :credit-cards
       first))

(defn transactions-by-category []
  (->> (cb.db/clobank-database)
       (map get-all-credit-cards)
       (group-total-by-category)
       println))

(defn transactions-by-merchant []
  (->> (cb.db/clobank-database)
       (map get-all-credit-cards)
       (group-total-by-merchant)
       println))

(defn list-transactions []
  (->> (cb.db/clobank-database)
       (map get-all-credit-cards)
       (map :transactions)
       (reduce into [])
       (map format-transaction)
       println
       ;(map show-formatted-transactions)
       ))
