(ns learning-big.core
  (:gen-class)
  (:require [learning-big.builder :as b]))

(defn -main [file-path & words]
  (spit file-path (apply b/eng->jp words)))
