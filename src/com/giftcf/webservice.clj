(ns com.giftcf.webservice
  (:use net.cgrand.moustache)
  (:require [com.giftcf.wishlist :as wishlist]
	    [clojure.contrib.json :as json]
	    [appengine-magic.core :as ae]
	    [ring.middleware.params :as params]))

(def international-bindings
     {"uk" {:site "http://www.amazon.co.uk"
	    :base-url "http://www.amazon.co.uk/gp/registry/wishlist/"
	    :search-url "http://www.amazon.co.uk/gp/registry/search.html?type=wishlist"}})

(defn wishlist-handler
  "Creates a function that handles a Ring request.

   wishlist-fn is a function that takes two parameters and returns a list of wishlist data

   wishlist-id is used by wishlist-fn to identify which list to return

   affiliate-code is the affiliate code to insert into the Amazon item links returned in the
   list of data"
  [wishlist-fn wishlist-id affiliate-code]
  (fn [req]
    (if-let [country (get (:params req) "country")]
      (if-let [country-params (get international-bindings country)]
	;If we have a valid country code rebind the URLs we use
	(binding [wishlist/*amazon-site* (:site country-params)
		  wishlist/*wishlist-base-url* (:base-url country-params)
		  wishlist/*wishlist-search-url* (:search-url country-params)]
	  {:status 200
	   :headers {"Content-Type" "application/json"}
	   :body (if-let [wishlist-data (wishlist-fn
					 wishlist-id
					 affiliate-code)]
		   (json/json-str wishlist-data)
		   (json/json-str {:error "Unable to retrieve wishlist data."}))})
	;Otherwise this is an error
	{:status 200
	 :headers {"Content-Type" "application/json"}
	 :body (json/json-str {:error "Invalid country code."})})
      ;Use the default URL for amazon.com defined in wishlist.clj
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (if-let [wishlist-data (wishlist-fn
				     wishlist-id
				     affiliate-code)]
	       (json/json-str wishlist-data)
	       (json/json-str {:error "Unable to retrieve wishlist data."}))})))

(def giftcf-app-handler
     (app params/wrap-params
	  ["wishlist" "email" email-address]
	  {:get (wishlist-handler wishlist/wishlist-items-by-email
				  email-address
				  "gifterate-20")}
	  
	  ["wishlist" "email" email-address affiliate-id]
	  {:get (wishlist-handler wishlist/wishlist-items-by-email
				  email-address
				  affiliate-id)}

	  ["wishlist" wishlist-id]
	  {:get (wishlist-handler wishlist/wishlist-items
				  wishlist-id
				  "gifterate-20")}

	  ["wishlist" wishlist-id affiliate-id]
	  {:get (wishlist-handler wishlist/wishlist-items
				  wishlist-id
				  affiliate-id)}))

(ae/def-appengine-app giftcf-app #'giftcf-app-handler)