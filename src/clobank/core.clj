(ns clobank.core
  (:require [clobank.logic :as cb.logic]))

(println "\nTransações por categoria: ")
(cb.logic/transactions-by-category)

(println "\nTransações: ")
(cb.logic/list-transactions)