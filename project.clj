(defproject message-decoding "0.1.0"
  :description "solution for codeeval 'message-decoding' challenge"
  :url "https://www.codeeval.com/open_challenges/36/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :main ^:skip-aot message-decoding.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
