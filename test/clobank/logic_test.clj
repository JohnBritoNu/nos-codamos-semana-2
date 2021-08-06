(ns clobank.logic-test
  (:require [clojure.test :refer :all]
            [clobank.logic :refer :all]
            [schema.core :as s]
            [clojure.test.check.generators :as gen]
            [schema-generators.generators :as g]))

(s/set-compile-fn-validation! true)

(defn gerar-amostras-get-total
  ([number]
   (gerar-amostras-get-total number []))
  ([number list]
   (if (> number 0)
     (let [sample (gen/sample  (gen/not-empty (g/generator Transaction)))
           new-list (conj list sample)]
       (gerar-amostras-get-total (dec number) new-list))
     list)))

(deftest test-get-total
  (testing "Uma sequencia vazia"
    (let [map-seq []]
      (is (= 0 (get-total map-seq)))))

  (testing "Uma sequencia vazia"
    (let [map-seq [nil]]
      (is (nil? (get-total map-seq)))))

  (testing "Uma mapa de dados sem o campo AMOUNT"
    (let [map-seq [{:not-amount 1}, {:not-amount 9}, {:not-amount 7}, {:not-amount 3}, {:not-amount 2}, {:not-amount 8}]]
      (is (thrown? java.lang.NullPointerException
                   (get-total map-seq)))))

  (testing "Uma mapa de dados sem alguns contendo o campo AMOUNT"
    (let [map-seq [{:amount 1}, {:amount 9}, {:not-amount 7}, {:not-amount 3}, {:amount 2}, {:amount 8}]]
      (is (thrown? java.lang.NullPointerException
                   (get-total map-seq)))))

  (testing "Uma sequencia de numeros interios positivos"
    (let [map-seq-int [{:amount 1}, {:amount 9}, {:amount 7}, {:amount 3}, {:amount 2}, {:amount 8}]]
      (is (= 30 (get-total map-seq-int)))))

  (testing "Uma sequencia de numeros interios negativos"
    (let [map-seq-int [{:amount -1}, {:amount -9}, {:amount -7}, {:amount -3}, {:amount -2}, {:amount -8}]]
      (is (= -30 (get-total map-seq-int)))))

  (testing "Uma sequencia de numeros decimais positivos"
    (let [map-seq-int [{:amount 0.1}, {:amount 0.9}, {:amount 0.7}, {:amount 0.3}, {:amount 0.2}, {:amount 0.8}]]
      (is (= 3.0 (get-total map-seq-int)))))

  (testing "Uma sequencia de numeros decimais negativos"
    (let [map-seq-int [{:amount -0.1}, {:amount -0.9}, {:amount -0.7}, {:amount -0.3}, {:amount -0.2}, {:amount -0.8}]]
      (is (= -3.0 (get-total map-seq-int)))))

  (testing "Muitas sequencias de numeros aleatorios"
    (doseq [map-seq-int  (gerar-amostras-get-total 100)]
      (is (number? (get-total map-seq-int))))))

(deftest test-transform-group-with-sum
  (testing "Uma mapa vazio"
    (let [data []]
      (is (= {nil 0}
            (transform-group-with-sum data)))))

  (testing "Uma mapa com chave com valores nulos"
    (let [data [:test [nil, nil]]]
      (is (thrown? java.lang.NullPointerException
                   (transform-group-with-sum data)))))

  (testing "Uma mapa com chave com valores nulos"
    (let [data [:test [{:amount 10}, {:amount 100}]]]
      (is (= {:test 110}
             (transform-group-with-sum data)))))
)

(deftest test-group-total-by-category
  (testing "Testando entrada vazia"
    (let [entrada nil]
      (is (= []
           (group-total-by-category entrada)))))

  (testing "Testando entrada valida"
    (let [entrada [{:transactions [
                                   {:amount   100, :category "Educacao"}
                                   {:amount   10,  :category "Alimentacao"}
                                   {:amount   1,   :category "Saude"}]}]]
      (is (= [{"Educacao" 100} {"Alimentacao" 10} {"Saude" 1}]
             (group-total-by-category entrada)))))
)













