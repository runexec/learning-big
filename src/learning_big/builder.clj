(ns learning-big.builder
  (:require [learning-big.jisho.core :as jc]
            [learning-big.tangorin.core :as tc]
            [learning-big.ui :as lbui]
            [hiccup.core :as hc]))

(defn- builder 
  "wait-ms - waits before every recur
   pred - applied to every word
   words - should be string obviously"
  [wait-ms pred & words]
  (loop [w (sort words)
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
  (lbui/html-utf8
   [:table 
    [:tr
     [:td 
      (loop [b (sort builder-results)
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

(defn eng->jp 
  "Jisho.org"
  [& words]
  (let [b (apply builder 3000 jc/eng->html words)]
    (builder->html b)))

(defn tangorin-eng->jp
  "Tangorin.com"
  [& words]
  (let [b (apply builder 3000 tc/eng->html words)]
    (builder->html b)))  
