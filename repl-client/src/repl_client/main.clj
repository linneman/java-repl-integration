; Clojure Repl Server
;
; main class for generation of stand-alone app
;
; by Otto Linnemann
; (C) 2015, GNU General Public Licence

(ns repl-client.main
  (:gen-class)
  (:require [clojure.tools.nrepl :as repl]))


(defn read-non-empty-line [] (let [l (read-line)] (if (empty? l) (recur) l)))

(defn remote-repl
  [host port]
  (with-open [conn (repl/connect :host host :port port)]
    (println "connected to host"  host ", port: " port)
    (loop []
      (let [exp (read-non-empty-line)
            resp (-> (repl/client conn 1000)    ; message receive timeout required
                     (repl/message {:op "eval" :code exp})
                     doall)
            outstr (apply str
                          (filter not-empty (map #(str (:out %) (:value %)) resp)))]
        (println outstr))
      (recur))))


(defn keep-alive-repl
  [host port]
  (loop []
    (let [keep-running (try
                         (remote-repl host port)
                         (catch java.net.SocketException e (do (println "no host connection!") (Thread/sleep 3000) true))
                         (catch java.net.UnknownHostException e (do (println "host is unknown, exit!") false)))]
      (when keep-running (recur)))))


(defn -main
  "execute script and start repl (cider)"
  [& args]
  (let [host (if (> (count args) 0) (nth args 0) "localhost")
        port (if (> (count args) 1) (Integer/parseInt (nth args 1)) 7888)]
    (keep-alive-repl host port)))


(comment
  ;; try out
  (take 5 (iterate inc 1))
  (time (reduce + (range 1e6)))
  )
