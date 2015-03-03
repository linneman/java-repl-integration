;; Clojure Repl Server
;;
;; invoke e.g. as
;;
;; lein run
;;
;; and enter the following expressions
;;
;;  (take 5 (iterate inc 1))
;;  (time (reduce + (range 1e6)))
;;
;; main class for generation of stand-alone app
;;
;; by Otto Linnemann
;; (C) 2015, GNU General Public Licence

(ns repl-client.main
  (:gen-class)
  (:require [clojure.tools.nrepl :as repl]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log])
  (:use [server.socket])
  (:import [java.io BufferedReader InputStreamReader OutputStreamWriter]))


(def quit-cmd "quit")
(def exit-request (atom false))


(defn transform-repl-resp
  "transforms the repl output data structure
  to some useful output, for more details refer to:
  https://github.com/clojure/tools.nrepl"
  [resp]
  (if-let [err (first (filter :err resp))]
    (:err err)
    (apply str
           (filter not-empty (map #(str (:out %) (:value %)) resp)))))


(def repl-conn (atom nil))
(defn remote-eval!
  "evaluates expression on remote side, manages internally socket
   connection to nrepl host"
  [error-resp host port exp]
  (when-not @repl-conn
    (do
      (reset! repl-conn (repl/connect :host host :port port))
      (log/info "connected to host" host ", port: " port)
      ))
  (let [resp (try
               (-> (repl/client @repl-conn 1000) ; message receive timeout required
                   (repl/message {:op "eval" :code exp})
                   doall)
               (catch java.net.SocketException e
                 (do
                   (log/error "connection to socket server interrupted!")
                   (reset! repl-conn nil)
                   error-resp)))]
    ; (log/info "-> " resp)
    (transform-repl-resp resp)))


(defn remote-repl
  "reads lines from given InputStream in, evaluates the given
   expression on specified nrepl server and writes the result
   to OutputStream out."
  [host port in out]
  (binding [*in* (BufferedReader. (InputStreamReader. in))
            *out* (OutputStreamWriter. out)]
    (loop []
      (let [exp (read-line)]
        (if (= exp quit-cmd) (reset! exit-request true))
        (if (empty? exp) (Thread/sleep 100)
            (let [req-agent (agent "no connection to nrepl server error!")
                  resp (send-off req-agent remote-eval! host port exp)]
              (await-for 3000 req-agent)
              (println @req-agent)))
        (when-not @exit-request (recur))))))


(defn create-stdio-server
  "Connects the remote-repl function instead to an tcp server to
   standard input and standard output."
  [in out handler]
  (loop []
    (try (handler in out) (catch java.net.SocketException e (println "lost connection!")))
    (when-not @exit-request (recur))))



(def cli-options ; command line interface (leiningen)
  [["-h" "--host host" "nrepl host to connect to"
    :default "localhost"]
   ["-p" "--port port" "nrepl service port"
    :default "7888"
    :validate [#(let [p (Integer/parseInt %)] (and (> p 1) (< p 65535))) "must be valid port"]]
   ["-l" "--local-port port" "socket server port"
    :default ""
    :validate [#(let [p (Integer/parseInt %)] (and (> p 1) (< p 65535))) "must be valid port"]]
   ["-?" "--help" "this help string"]])


(defn -main
  "execute script and start repl (cider)"
  [& args]
  (let [opts (parse-opts args cli-options)
        options (:options opts)
        arguments (:arguments opts)
        summary (:summary opts)
        errors (:errors opts)
        host (:host options)
        port (:port options)
        local-port (:local-port options)
        invalid-opts (not-empty errors)
        title-str "repl-client: connects standard I/O or given socket service to nepl server\n"]
    (println title-str)
    (when (or (:help options) invalid-opts)
      (when invalid-opts (println errors))
      (println "  Invocation:\n")
      (println summary)
      (System/exit -1))
    (let [port (Integer/parseInt port)
          repl (partial remote-repl host port)]
      (if-let [local-port (when (not-empty local-port) (Integer/parseInt local-port))]
        (do
          (println "starting proxy server on port " local-port "... ")
          (let [server (create-server local-port repl)]
            (loop [] (Thread/sleep 1000) (when-not @exit-request (recur)))
            (close-server server)))
        (do
          (println "enter your clojure expressions below!")
          (create-stdio-server System/in System/out repl)))
      (println "quit repl server!")
      (System/exit 0))))
