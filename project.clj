(defproject evil-overlord "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [aleph "0.4.0-beta1"]
                 [org.clojure/data.json "0.2.5"][manifold "0.1.0-beta8"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cheshire "5.4.0"]]
  :main ^:skip-aot evil-overlord.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
