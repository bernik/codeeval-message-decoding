(ns message-decoding.core
  (:gen-class))

(defn bin-str->int [^String s]
  (Integer/parseInt s 2))

(defn int->bin-str [^Integer n ^Integer length]
  (let [s (->> n
             Integer/toBinaryString
             (format (str "%" length "s")))]
    (.replace s " " "0")))

(defn next-key-code [^String current-code]
  (let [next-code (-> current-code
                      bin-str->int
                      inc
                      (int->bin-str (count current-code)))]
    (if (every? #{\1} (seq next-code))
      (apply str (-> current-code
                     .length
                     inc
                     (repeat "0")))
      next-code)))

(defn generate-keymap [char-seq]
  (zipmap (iterate next-key-code "0") char-seq))

(defn split-line
  "split text line into keymap and message"
  [line]
  (let [[head mess] (split-with (complement #{\0 \1}) line)]
    {:keymap (generate-keymap head)
     :message (apply str mess)}))

(defn read-key-length
  "return segment key length and message from input"
  [message]
  (let [[bin-length message] (split-at 3 message)]
    {:length (->> bin-length (apply str) bin-str->int)
     :message (apply str message)}))

(def segment-end? (partial every? #{\1}))

(defn message->keycodes
  "read split message by segments and extract keys from segments"
  [message]
  (let [{length :length mess :message} (read-key-length message)]
    (loop [mess mess
           codes []
           key-length length]
      (if (zero? key-length)
        codes
        (let [[k mess] (split-at key-length mess)]
          (if (segment-end? k)
            (let [{length :length mess :message} (read-key-length (apply str mess))]
              (recur mess codes length))
            (recur mess (conj codes (apply str k)) key-length)))))))

(defn keycodes->string
  "generate string by given keycode map and codes"
  [kcs kcmap]
  (->> kcs
       (map #(get kcmap %))
       (apply str)))

(defn decode [line]
  (let [{keymap :keymap message :message} (split-line line)]
    (keycodes->string (message->keycodes message) keymap)))

(defn -main [& args]
  (with-open [rdr (clojure.java.io/reader (first *command-line-args*))]
    ; Read each line ignoring empty ones
    (doseq [line (remove empty? (line-seq rdr))]
      (println (decode line)))))

