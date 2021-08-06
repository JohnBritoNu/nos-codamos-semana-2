(ns clobank.logic
  (:require [clobank.db :as cb.db]
            [java-time :as jt]
            [schema.core :as s]))

(s/set-compile-fn-validation! true)

(s/def Transaction
  {:guid     s/Str,
   ;:date     java.time.LocalDateTime,
   :date     s/Str,
   :amount   s/Num,
   :merchant s/Str,
   :category s/Str,
   })

(s/def CreditCard
  {:guid         s/Str,
   :number       s/Str,
   :cvv          s/Int,
   :valid        s/Str,
   :limit        s/Int,
   :transactions [Transaction]})

(s/def CreditCardList  [CreditCard])

(s/def Client
  {:guid         s/Str,
   :name         s/Str,
   :cpf          s/Str,
   :email        s/Str,
   :credit-cards CreditCardList})

(s/def TotalCategory  {s/Keyword s/Num})

(s/def GroupTotalCategory  [TotalCategory])

(defn get-total [value]
  (->> value
       (map :amount)
       (reduce +)))

(s/defn transform-group-with-sum
  [[k, v] :- TotalCategory]
  {k (get-total v)})

(s/defn group-total-by-category :- GroupTotalCategory
  [credit-cards :- CreditCardList]
  (->> credit-cards
       (map :transactions)
       (reduce into [])
       (group-by :category)
       (map transform-group-with-sum)))

(defn get-all-credit-cards
  [customer]
  (->> customer
       :credit-cards
       first))

(defn transactions-by-category [data]
  (->> data
       (map get-all-credit-cards)
       (group-total-by-category)
       println))

(transactions-by-category (cb.db/clobank-database))

(s/defn format-transaction
  [transaction :- Transaction]
  (-> transaction
      (update :date #(str (jt/format "dd/MM/yyyy hh:mm" %)))
      (update :amount #(str "R$ " %))))

(s/defn show-formatted-transactions
  [transaction :- Transaction]
  (println "\nData:" (:date transaction)
           "\nValor:" (:amount transaction)
           "\nEstabelecimento:" (:merchant transaction)
           "\nCategoria:" (:category transaction)))

(s/defn list-transactions
  [data]
  (->> data
       (map get-all-credit-cards)
       (map :transactions)
       (reduce into [])
       (map format-transaction)
       (mapv show-formatted-transactions)))

;(list-transactions (cb.db/clobank-database))
;
;(let [transaction {:guid     "38146102-16c7-4a21-ace8-c17bccecc546",
;                   ;:date     (java-time/local-date-time 2021 07 26 8 34),
;                   :date     "02/12"
;                   :amount   15.00,
;                   :merchant "Bar do Seu Zé",
;                   :category "Alimentação",
;                   }
;      credit-card {:guid         "16f476bc-a4aa-4254-8552-c3b6b99e98d0",
;                     :number       "1111222233334444",
;                     :cvv          123,
;                     :valid        "04/2022",
;                     :limit        5234,
;                     :transactions [transaction]}
;      credit-card-list [credit-card, credit-card]
;      client {:guid         "3fe728fc-216b-402d-9927-d5c2ac0cefa7",
;              :name         "João Clojure",
;              :cpf          "12312312312",
;              :email        "joaoclojure@gmail.com",
;              :credit-cards credit-card-list}]
;
;  (println "\nTeste schema transaction:" (s/validate Transaction transaction))
;  (println "Teste schema Credit Card:" (s/validate CreditCard credit-card))
;  (println "Teste schema Credit Card List:" (s/validate CreditCardList credit-card-list))
;  (println "Teste schema Client:" (s/validate Client client)))
