(ns com.giftcf.render
  (:use net.cgrand.enlive-html))

(def *wishitem-model* [[:.wishlist (nth-of-type 1)] :> first-child])

(defsnippet wishitem-model "template/wishlist.html" *wishitem-model*
  [{:keys [item link]}]
  [:a] (do-> (content item) (set-attr :href link)))

(def *wishitem-section* [[:.wishlist (nth-of-type 1)]])

(defsnippet wishitem-section "template/wishlist.html" *wishitem-section*
  [data model]
  [:.wishlist] (content (map model data)))

(deftemplate wishlist-page "template/wishlist.html"
  [data]
  [:body] (content (wishitem-section data wishitem-model)))