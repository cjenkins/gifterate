(ns com.giftcf.webservice
  (:use net.cgrand.moustache)
  (:require [com.giftcf.wishlist :as wishlist]
	    [clojure.contrib.json :as json]
	    [appengine-magic.core :as ae]
	    [ring.middleware.params :as params]))

(def giftcf-app-handler
     (app params/wrap-params
	  ["wishlist" "email" email-address]
	  {:get (fn [req]
		  (if-let [country (get (:params req) "country")]
		    (if (= country "uk")
		      (binding [wishlist/*amazon-site* "http://www.amazon.co.uk"
				wishlist/*wishlist-base-url* "http://www.amazon.co.uk/gp/registry/wishlist/"
				wishlist/*wishlist-search-url* "http://www.amazon.co.uk/gp/registry/search.html?type=wishlist"]
			{:status 200
			 :headers {"Content-Type" "application/json"}
			 :body (if-let [wishlist-data (wishlist/wishlist-items-by-email
						       email-address
						       "gifterate-20")]
				 (json/json-str wishlist-data)
				 (json/json-str {:error "Unable to retrieve wishlist data."}))}))
		    {:status 200
		     :headers {"Content-Type" "application/json"}
		     :body (if-let [wishlist-data (wishlist/wishlist-items-by-email
						   email-address
						   "gifterate-20")]
			     (json/json-str wishlist-data)
			     (json/json-str {:error "Unable to retrieve wishlist data."}))}))}

	  ["wishlist" "email" email-address affiliate-id]
	  {:get (fn [req]
		  (if-let [country (get (:params req) "country")]
		    (if (= country "uk")
		      (binding [wishlist/*amazon-site* "http://www.amazon.co.uk"
				wishlist/*wishlist-base-url* "http://www.amazon.co.uk/gp/registry/wishlist/"
				wishlist/*wishlist-search-url* "http://www.amazon.co.uk/gp/registry/search.html?type=wishlist"]
			{:status 200
			 :headers {"Content-Type" "application/json"}
			 :body (if-let [wishlist-data (wishlist/wishlist-items-by-email
						       email-address
						       affiliate-id)]
				 (json/json-str wishlist-data)
				 (json/json-str {:error "Unable to retrieve wishlist data."}))}))
		    {:status 200
		     :headers {"Content-Type" "application/json"}
		     :body (if-let [wishlist-data (wishlist/wishlist-items-by-email
						   email-address
						   affiliate-id)]
			     (json/json-str wishlist-data)
			     (json/json-str {:error "Unable to retrieve wishlist data."}))}))}

	  ["wishlist" wishlist-id]
	  {:get (fn [req]
		  (if-let [country (get (:params req) "country")]
		    (if (= country "uk")
		      (binding [wishlist/*amazon-site* "http://www.amazon.co.uk"
				wishlist/*wishlist-base-url* "http://www.amazon.co.uk/gp/registry/wishlist/"
				wishlist/*wishlist-search-url* "http://www.amazon.co.uk/gp/registry/search.html?type=wishlist"]
			{:status 200
			 :headers {"Content-Type" "application/json"}
			 :body (if-let [wishlist-data (wishlist/wishlist-items
						       wishlist-id
						       "gifterate-20")]
				 (json/json-str wishlist-data)
				 (json/json-str {:error "Unable to retrieve wishlist data."}))}))
		    {:status 200
		     :headers {"Content-Type" "application/json"}
		     :body (if-let [wishlist-data (wishlist/wishlist-items
						   wishlist-id
						   "gifterate-20")]
			     (json/json-str wishlist-data)
			     (json/json-str {:error "Unable to retrieve wishlist data."}))}))}

	  ["wishlist" wishlist-id affiliate-id]
	  {:get (fn [req]
		  (if-let [country (get (:params req) "country")]
		    (if (= country "uk")
		      (binding [wishlist/*amazon-site* "http://www.amazon.co.uk"
				wishlist/*wishlist-base-url* "http://www.amazon.co.uk/gp/registry/wishlist/"
				wishlist/*wishlist-search-url* "http://www.amazon.co.uk/gp/registry/search.html?type=wishlist"]
			{:status 200
			 :headers {"Content-Type" "application/json"}
			 :body (if-let [wishlist-data (wishlist/wishlist-items
						       wishlist-id
						       affiliate-id)]
				 (json/json-str wishlist-data)
				 (json/json-str {:error "Unable to retrieve wishlist data."}))}))
		    {:status 200
		     :headers {"Content-Type" "application/json"}
		     :body (if-let [wishlist-data (wishlist/wishlist-items
						   wishlist-id
						   affiliate-id)]
			     (json/json-str wishlist-data)
			     (json/json-str {:error "Unable to retrieve wishlist data."}))}))}))

(ae/def-appengine-app giftcf-app #'giftcf-app-handler)