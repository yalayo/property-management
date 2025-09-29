(ns elevator)

(def texto "roblox")


texto





(do
  (print "a")
  (print "b"))

(do
  (println "toilet")
  (println "skibidi"))

 (println "skibidi" texto)

(def cinco 5)
cinco

(def mil 1000)
mil

(def ex 6)

ex

(+ cinco mil ex)

(- cinco mil ex)

(- mil cinco  ex)
 
(* cinco mil)
 
(def lista [1 2 3 4 0])

lista

(last lista)
(first lista)

(nth lista 0)


(def elevator 
  {
   :floor 0
   :moving false
   :door_status :close})

elevator

(defn move [floor]
  (assoc elevator :floor floor))

(move 3)

