(ns com.giftcf.urlutil
  (:require [net.cgrand.enlive-html :as html]
	    [clojure.contrib.string :as string]
	    [appengine-magic.services.url-fetch :as url-fetch])
  (:import (java.net URLEncoder)
	   (java.io InputStreamReader ByteArrayInputStream)))

(defn get-url [url]
  (html/html-resource
   (InputStreamReader. (ByteArrayInputStream. (:content (url-fetch/fetch url :deadline 10.0))))))

(defn- encode-message [message]
  (apply str (interpose "&" (map #(str
				   (URLEncoder/encode (string/as-str (first %)) "UTF-8") "="
				   (URLEncoder/encode (string/as-str (second %)) "UTF-8")) message))))

(defn post-url [url message]
  (let [encoded-message (encode-message message)
	byte-message (.getBytes encoded-message)]
    (let [response (url-fetch/fetch url :method :post :payload byte-message :deadline 10.0)]
      {:response (html/html-resource
		  (InputStreamReader. (ByteArrayInputStream. (:content response))))
       :final-url (:final-url response)})))