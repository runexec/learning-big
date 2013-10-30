(ns learning-big.tatoeba.core
  (:require [net.cgrand.enlive-html :as html]))

(defn search-url [words]
  (format "http://tatoeba.org/eng/sentences/search?query=%s&from=eng&to=jpn"
          (java.net.URLEncoder/encode words)))

(defn url->enlive-map [url]
  (let [data (html/html-resource (java.net.URL. url))
        extract (fn [k] 
                  (->> [k]
                       (html/select data)
                       (map html/text)
                       (map #(.. % (replace "\n" "")))))]
    {:jp (extract :div.furigana)
     :eng (->> (extract :a.text)
               (partition-all 2)
               (map first)
               flatten
               ;; UTF-8 english only
               (filter (fn [x]
                         (if (every? #(<= (int %) 255) x)
                           x))))}))


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
