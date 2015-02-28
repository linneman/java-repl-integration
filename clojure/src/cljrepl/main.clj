; Clojure Repl Server
;
; main class for generation of stand-alone app
;
; by Otto Linnemann
; (C) 2015, GNU General Public Licence

(ns cljrepl.main
  (:gen-class)
  (:require [cider.nrepl :refer (cider-nrepl-handler)]
            [clojure.tools.nrepl.server :as nrepl-server]))


(defn exec-script-when-existing
  "executes the given clojure file when existing"
  [filename]
  (when (.exists (clojure.java.io/as-file filename))
    (load-file filename)))


(defn start-repl
  "starts cider repl server at given port"
  [port]
  (println "staring clojure repl ...")
  (nrepl-server/start-server :port port :handler cider-nrepl-handler)
  (println "clojure repl server cider is running")
  (println "connect to hostname:"
           (.getCanonicalHostName (java.net.InetAddress/getLocalHost))
           ", port:" port))


(defn -main
  "execute script and start repl (cider)"
  [& args]
  (do
    (when-let  [filename (first args)]
      (println "executing clojure file " filename " ...")
      (exec-script-when-existing filename))
    (start-repl 7888)))
