(ns learning-big.core
  (:gen-class)
  (:require [learning-big.builder :as b]))

(defn -main
  "source is either jisho, tangorin, tatoeba"
  [source file-path & words]
  (let [words (distinct words)] 
    (condp = (keyword source)
      :jisho (spit file-path (apply b/eng->jp words))
      :tangorin (spit file-path (apply b/tangorin-eng->jp words))
      :tatoeba (spit file-path (apply b/tatoeba-eng->jp words))
      (println "Invalid source. The first argument must be jisho or tangorin"))))
    
