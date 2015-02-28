(defproject cljrepl "1.0.0-SNAPSHOT"
  :description "Solver for the Game of Solitaire"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.nrepl "0.2.7"]]
  :plugins [[cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  :main cljrepl.main
  :aot [cljrepl.main])
