(ns learning-big.builder
  (:require [learning-big.jisho.core :as jc]
            [hiccup.core :as hc]))

(defn- builder 
  "wait-ms - waits before every recur
   pred - applied to every word
   words - should be string obviously"
  [wait-ms pred & words]
  (loop [w words
         ret {}]
    (if-not (seq w)
      ret
      (let [word (-> w first str)]
        (println "Working on => " word)
        (Thread/sleep wait-ms)
        (recur 
         (rest w)
         (assoc ret (keyword word) (pred word)))))))

(defn- builder->html-menu [builder-results]
  (loop [k (-> builder-results keys sort)
         menu []]
    (if-not (seq k)
      (hc/html
       [:ul {:class :menu}
        (apply str menu)])
      (let [menu-name (-> k first name)
            menu-url [:li [:a {:href (str "#" menu-name)} menu-name]]]
        (recur
         (rest k)
         (conj menu (hc/html menu-url)))))))

(defn- builder->html [builder-results]
  (jc/html-utf8
   [:table 
    [:tr
     [:td 
      (loop [b builder-results
             ret []]
        (if-not (seq b)
          (apply str ret)
          (let [[k v] (first b)
                n (name k)
                header [:h1 (.. n toUpperCase)]]
            (recur 
             (rest b)
             (conj ret
                   (hc/html 
                    [:div 
                     [:br]
                     [:a {:name n :id n} header]
                     [:br]
                     [:p v]]))))))]
     [:td (builder->html-menu builder-results)]]]))


(defn eng->jp [& words]
  (let [b (apply builder 3000 jc/eng->html words)]
    (builder->html b)))
