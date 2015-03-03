(defproject repl-client "1.0.0-SNAPSHOT"
  :description "client for remote repl alternatively to Emacs/cider"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.nrepl "0.2.7"]
                 [server-socket "1.0.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]]
  :plugins [[cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  :main repl-client.main
  :aot [repl-client.main])
