(ns com.giftcf.webservice
  (:use net.cgrand.moustache)
  (:require [com.giftcf.wishlist :as wishlist] [com.giftcf.render :as render]
	    [appengine-magic.core :as ae]))

(def giftcf-app-handler
     (app ["wishlist" wishlist-id]
	  {:get (fn [req]
		  {:status 200
		   :headers {"Content-Type" "text/html"}
		   :body (if-let [wishlist-data (wishlist/wishlist-items wishlist-id)]
			   (render/wishlist-page wishlist-data)
			   "Unable to retrieve wishlist data.")})}
	  ["wishlist" wishlist-id affiliate-id]
	  {:get (fn [req]
		  {:status 200
		   :headers {"Content-Type" "text/html"}
		   :body (if-let [wishlist-data (wishlist/wishlist-items wishlist-id affiliate-id)]
			   (render/wishlist-page wishlist-data)
			   "Unable to retrieve wishlist data.")})}))

(ae/def-appengine-app giftcf-app #'giftcf-app-handler)