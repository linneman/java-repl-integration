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
  [error-resp host port exp timeout]
  (when-not @repl-conn
    (do
      (reset! repl-conn (repl/connect :host host :port port))
      (log/info "connected to host" host ", port: " port)
      ))
  (let [resp (try
               (-> (repl/client @repl-conn timeout) ; message receive timeout required
                   (repl/message {:op "eval" :code exp})
                   doall)
               (catch java.net.SocketException e
                 (do
                   (log/error "connection to remote nrepl server lost!")
                   (reset! repl-conn nil)
                   error-resp)))]
    ; (log/info "remote-eval!-> " resp)
    (if resp
      (if (not-empty resp)
        (transform-repl-resp resp)
        "error: nrepl server timed out, processing result lost!")
      "error: connection to remote nrepl server lost!")))


(defn remote-repl
  "reads lines from given InputStream in, evaluates the given
  expression on specified nrepl server and writes the result
  to OutputStream out."
  [& {:keys [in out host port timeout] :or {host "localhost" port 7888 timeout 3000}}]
  (binding [*in* (BufferedReader. (InputStreamReader. in))
            *out* (OutputStreamWriter. out)]
    (loop []
      (let [exp (read-line)]
        (if (= exp quit-cmd)
          (reset! exit-request true)
          (do
            (if (empty? exp) (Thread/sleep 100)
                (let [req-agent (agent nil)
                      resp (send-off req-agent remote-eval! host port exp timeout)]
                  (await-for (+ 100 timeout) req-agent)
                  (if @req-agent
                    (println @req-agent)
                    (println "error: no connection to remote nrepl server!"))))
            (recur)))))))


(defn create-stdio-server
  "Connects the remote-repl function instead to an tcp server to
   standard input and standard output."
  [in out handler]
  (loop []
    (try (handler in out) (catch java.net.SocketException e (println "lost connection!")))
    (when-not @exit-request (recur))))



(def cli-options ; command line interface (leiningen)
  [["-h" "--host <host>" "nrepl host to connect to"
    :default "localhost"]
   ["-p" "--port <port>" "nrepl service port"
    :default "7888"
    :validate [#(let [p (Integer/parseInt %)] (and (> p 0) (< p 65536))) "must be valid port"]]
   ["-l" "--local-port <port>" "socket server port"
    :default ""
    :validate [#(let [p (Integer/parseInt %)] (and (> p 0) (< p 65536))) "must be valid port"]]
   ["-t" "--timeout <timeout>" "timeout value in ms"
    :default "3000"
    :validate [#(let [t (Integer/parseInt %)] (and (>= t 100) (<= t (* 3600 1000)))) "must be between 100 and 3600000"]]
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
        timeout (:timeout options)
        invalid-opts (not-empty errors)
        title-str "repl-client: connects standard I/O or given socket service to nepl server\n"]
    (println title-str)
    (when (or (:help options) invalid-opts)
      (when invalid-opts (println errors))
      (println "  Invocation:\n")
      (println summary)
      (System/exit -1))
    (let [port (Integer/parseInt port)
          timeout (Integer/parseInt timeout)
          repl (fn [in out] (remote-repl :in in :out out :host host :port port :timeout timeout))]
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
