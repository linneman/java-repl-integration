(defproject repl-client "1.0.0-SNAPSHOT"
  :description "client for remote repl alternatively to Emacs/cider"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.nrepl "0.2.7"]]
  :plugins [[cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  :main repl-client.main
  :aot [repl-client.main])
