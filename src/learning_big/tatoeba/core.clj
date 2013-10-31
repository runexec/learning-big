(ns learning-big.tatoeba.core
  (:require [net.cgrand.enlive-html :as html]))

(defn search-url [words]
  (format "http://tatoeba.org/eng/sentences/search?query=%s&from=eng&to=jpn"
          (java.net.URLEncoder/encode words)))

(defn url->enlive-map [url]
  (let [data (html/html-resource (java.net.URL. url))
        data-text (->> (html/select data [:div.sentences_set 
                                          :>
                                          :div.mainSentence])
                       (map html/text))
        data-furigana (->> (html/select data [:div.translations])
                           (map (fn [x]
                                  (let [c (:content x)
                                        xs (filter coll? c)
                                        xs (drop-while #(-> % :content not) xs)
                                        coll-only #(filter coll? %)]

                                    ;; drill down to the furigana

                                    (->> xs 
                                         first 
                                         :content 
                                         coll-only
                                         last 
                                         last
                                         coll-only
                                         first
                                         coll-only
                                         last))))
                           (map html/text)
                           (map (comp 
                                 clojure.string/trim
                                 clojure.string/trimr
                                 clojure.string/trim-newline)))]
    {:jp data-furigana
     :eng data-text}))


(defn search-english [s]
  (url->enlive-map
   (search-url s)))

(defn search->hiccup
  [search-results]
  (let [{:keys [jp eng]} search-results
        combine (fn [learning trans]
                  (list
                   [:div {:class :learning} learning]
                   trans))]
    (for [[e j] (partition-all 2 (interleave eng jp))]
      (combine j e))))

(defn eng->html [word]
  (let [data (search-english word)
        to-display (search->hiccup data)]
    (apply str 
           (for [td to-display]
             (hiccup.core/html td)))))
