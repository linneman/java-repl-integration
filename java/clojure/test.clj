(ns repl.test)

(import '(oata TestA))


(defn interop-test! [a b]
  (let [myTestA (TestA/getInstance)]
    (println "before setter, got for a:" (.geta myTestA))
    (println "before setter, got for b:" (.getb myTestA))
    (println "set a to:" a)
    (println "set b to:" b)
    (.seta myTestA a)
    (.setb myTestA b)))

(interop-test! 88 99)
