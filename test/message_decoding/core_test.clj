(ns message-decoding.core-test
  (:require [clojure.test :refer :all]
            [message-decoding.core :refer :all]))

(deftest bin->int-test
  (are [b n] (= (bin-str->int b) n)
    "000" 0
    "001" 1
    "010" 2
    "100" 4
    "101" 5))


(deftest next-code-test
  (are [curr next] (= next (next-key-code curr))
    "0" "00"
    "00" "01"
    "1110" "00000"
    "10110" "10111"
    "00000" "00001"))

(deftest key-map-test
  (let [codes ["0" "00" "01" "10" "000" "001" "010" "011" "100"]]
    (are [s] (= (zipmap codes s) (generate-keymap s))
      "q"
      "qw"
      "qwe"
      "qwer"
      "qwerty"
      "qwertyu"
      "qwertyui"
      "qwertyuio")))

(deftest split-line-test
  (are [line header message] (= {:keymap (generate-keymap header)
                                 :message message}
                                (split-line line))
    "#$qwe000" "#$qwe" "000"
    "asdasd111" "asdasd" "111"
    "##$$**010101" "##$$**" "010101"
    "*^$101010" "*^$" "101010"))

(deftest read-key-length-test
  (are [lm res] (= res (read-key-length lm))
    "01000010111" {:length 2 :message "00010111"}
    "10000000101111001111111" {:length 4 :message "00000101111001111111"}
    "000" {:length 0 :message ""}))

(deftest message->keycodes-test
  (are [message codes] (= (message->keycodes message) codes)
    "000" []
    "0010001000" ["0" "0" "0"]
    "01001100011000" ["01" "10" "00"]
    "011110000010000011111000" ["110" "000" "010" "000" "011"]
    "10000000101111001111111000" ["0000" "0101" "1110" "0111"]))

(deftest keycodes->string-test
  (let [m (generate-keymap "abcdefg")]
    (are [kcs s] (= s (keycodes->string kcs m))
      ["0" "00" "01" "10"] "abcd"
      ["00" "00" "00"] "bbb"
      ["10" "000" "01"] "dec")))

(deftest decode-test
  (is (= (decode "$#**\\0100000101101100011100101000")
         "##*\\$")))



