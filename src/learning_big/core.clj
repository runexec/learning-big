(ns learning-big.core
  (:gen-class)
  (:require [learning-big.builder :as b]))

(defn -main
  "source is either jisho or tangorin"
  [source file-path & words]
  
  (condp = (keyword source)
    :jisho (spit file-path (apply b/eng->jp words))
    :tangorin (spit file-path (apply b/tangorin-eng->jp words))
    (println "Invalid source. The first argument must be jisho or tangorin")))
    
