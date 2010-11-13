(ns com.giftcf.urlutil
  (:require [net.cgrand.enlive-html :as html]
	    [clojure.contrib.string :as string])
  (:import (java.net HttpURLConnection URL URLEncoder)
	   (java.io BufferedReader InputStreamReader OutputStreamWriter)))

(defn get-url [url]
  (try
    (html/html-resource (URL. url))
    (catch Exception e nil)))

(defn- encode-message [message]
  (apply str (interpose "&" (map #(str
				   (URLEncoder/encode (string/as-str (first %)) "UTF-8") "="
				   (URLEncoder/encode (string/as-str (second %)) "UTF-8")) message))))

(defn post-url [url message]
  (try
    (let [connection (doto (.openConnection (URL. url))
		       (.setDoOutput true)
		       (.setRequestMethod "POST"))]
      (with-open [writer (OutputStreamWriter. (.getOutputStream connection))]
	(.write writer (encode-message message))
	(.flush writer)
	(with-open [reader (BufferedReader. (InputStreamReader. (.getInputStream connection)))]
	  (let [response (html/html-resource reader)
		final-url (.getURL connection)]
	    (.disconnect connection)
	    {:response response :final-url (.toString final-url)}))))
    (catch Exception e nil)))