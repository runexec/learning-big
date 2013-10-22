(ns learning-big.jisho.core
  (:require [net.cgrand.enlive-html :as html]
            [hiccup.core :as hc]))

(def search-urls
  (letfn [(base [s] (str "http://trans.hiragana.jp/ruby/" s))]
    {:eng->jp (base "http://www.jisho.org/sentences?eng=")
     :jp->eng (base "http://www.jisho.org/sentences?jp=")}))

(defn search-types [] (keys search-urls))

(defn- url* [url words]
  (str url (java.net.URLEncoder/encode words)))

(defn eng->jp-url [word] 
  (-> search-urls
      :eng->jp
      (url* word)))

(defn jp->eng-url [word] 
  (-> search-urls
      :jp->eng
      (url* word)))

(defn url->enlive-map [url]
  (let [data (html/html-resource (java.net.URL. url))
        extract (fn [k] 
                  (map (fn [x] (apply str x))
                       (map #(html/emit* [%])
                            (html/select data [k]))))]
    {:jp (extract :td.japanese)
     :eng (extract :td.english)}))

(defn search-english [word]
  (url->enlive-map
   (eng->jp-url word)))

(defn search-japanese [word]
  (url->enlive-map
   (jp->eng-url word)))

(defn search->hiccup
  "lang-first is either :jp or :en and this will decide which
  language will be bold." 
  [lang-first search-results]
  (let [{:keys [jp eng]} search-results
        combine (fn [learning trans]
                  (let [r! #(-> %
                                (.replace "<td" "<p")
                                (.replace "</td" "</p")
                                (.replace "<a" "<span")
                                (.replace "</a>" "</span>")
                                (.replace "50%" ""))]
                    (list
                     [:div {:class :learning} (r! learning)]
                     (r! trans))))]
    (for [[e j] (partition-all 2 (interleave eng jp))]
       (if (= lang-first :eng)
         (combine e j)
         (combine j e)))))

(defn eng->html [word]
  (let [data (search-english word)
        to-display (search->hiccup :jp data)]
    (apply str 
           (for [td to-display]
             (hiccup.core/html td)))))

(defn jp->html [word]
  (let [data (search-english word)
        to-display (search->hiccup :eng data)]
    (apply str 
           (for [td to-display]
             (hiccup.core/html td)))))



