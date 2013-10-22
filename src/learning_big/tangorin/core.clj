(ns learning-big.tangorin.core
  (:require [net.cgrand.enlive-html :as html]))

(defn search-url [words]
  (str "http://tangorin.com/dict.php?scope=results&dict=examples&s="
       (java.net.URLEncoder/encode words)))

(defn url->enlive-map [url]
  (let [data (html/html-resource (java.net.URL. url))
        extract (fn [k] 
                  (map (fn [x] (apply str x))
                       (map #(html/emit* [%])
                            (html/select data [k]))))]
    {:jp (extract :dt.ex-dt)
     :eng (extract :dd.ex-dd.ex-en)}))

(defn search-english [s]
  (url->enlive-map
   (search-url s)))

(defn search->hiccup
  [search-results]
  (let [{:keys [jp eng]} search-results
        combine (fn [learning trans]
                  (let [r! #(-> %
                                (.replace "<a" "<span")
                                (.replace "</a>" "</span>"))]
                    (list
                     [:div {:class :learning} (r! learning)]
                     (r! trans))))]
    (for [[e j] (partition-all 2 (interleave eng jp))]
      (combine j e))))

(defn eng->html [word]
   (let [data (search-english word)
         to-display (search->hiccup data)]
     (apply str 
            (for [td to-display]
              (hiccup.core/html td)))))

;; (-> "bears" search-english search->hiccup first)
