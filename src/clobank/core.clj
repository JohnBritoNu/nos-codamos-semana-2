(ns clobank.core
  (:require [clobank.logic :as cb.logic]
            [clobank.db :as cb.db]
            [schema.core :as s]))

(s/set-compile-fn-validation! true)

(println "\nTransações por categoria: ")
(cb.logic/transactions-by-category (cb.db/clobank-database))

(println "\nTransações: ")
(cb.logic/list-transactions (cb.db/clobank-database))