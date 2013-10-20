(ns learning-big.jisho.ui
  (:require [garden.core :refer [css]]))

(def style 
  (css [[:table {:padding-left :200px}]
        [:.menu {:position :absolute
                 :top :80px
                 :left :20px}]
        [:div {:padding :10px :margin :10px}]
        [:span {:margin :10px :padding :10px}]
        [:.match {:background-color :#FADDDD}]
        [:.learning {:font-size :30px
                     :background-color :#FFFAAA}]
        [:ruby {:display :inline-table
                :text-align :center
                :white-space :nowrap
                :text-indent :0
                :margin :0
                :vertical-align :1.06em}]
        ["ruby>rb,ruby>rbc" {:display :table-row-group
                             :line-height :130%}]
        ["ruby>rt,ruby>rbc+rtc" {:display :table-header-group
                                 :font-size :70%
                                 :line-height :80%
                                 :letter-spacing :0}]
        [:rp {:display :none}]]))

