(ns com.giftcf.webservice
  (:use net.cgrand.moustache)
  (:require [com.giftcf.wishlist :as wishlist]
	    [clojure.contrib.json :as json]
	    [appengine-magic.core :as ae]))

(def giftcf-app-handler
     (app ["wishlist" "email" email-address]
	  {:get (fn [req]
		  {:status 200
		   :headers {"Content-Type" "application/json"}
		   :body (if-let [wishlist-data (wishlist/wishlist-items-by-email email-address "gifterate-20")]
			   (json/json-str wishlist-data)
			   (json/json-str {:error "Unable to retrieve wishlist data."}))})}
	  ["wishlist" "email" email-address affiliate-id]
	  {:get (fn [req]
		  {:status 200
		   :headers {"Content-Type" "application/json"}
		   :body (if-let [wishlist-data (wishlist/wishlist-items-by-email email-address affiliate-id)]
			   (json/json-str wishlist-data)
			   (json/json-str {:error "Unable to retrieve wishlist data."}))})}
	  ["wishlist" wishlist-id]
	  {:get (fn [req]
		  {:status 200
		   :headers {"Content-Type" "application/json"}
		   :body (if-let [wishlist-data (wishlist/wishlist-items wishlist-id "gifterate-20")]
			   (json/json-str wishlist-data)
			   (json/json-str {:error "Unable to retrieve wishlist data."}))})}
	  ["wishlist" wishlist-id affiliate-id]
	  {:get (fn [req]
		  {:status 200
		   :headers {"Content-Type" "application/json"}
		   :body (if-let [wishlist-data (wishlist/wishlist-items wishlist-id affiliate-id)]
			   (json/json-str wishlist-data)
			   (json/json-str {:error "Unable to retrieve wishlist data."}))})}))

(ae/def-appengine-app giftcf-app #'giftcf-app-handler)