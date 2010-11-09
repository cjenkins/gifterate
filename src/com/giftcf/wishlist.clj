(ns com.giftcf.wishlist
  (:require [net.cgrand.enlive-html :as html]))

(def *wishlist-base-url* "http://www.amazon.com/gp/registry/wishlist/")
(def *options* "?reveal=unpurchased&filter=all&layout=standard")
(def *item-text-max-length* 32)

(def *item-selector*
     [[:tbody.itemWrapper]])

(def *product-title-selector*
     [[:span.productTitle] [:strong] [:a]])

(def *price-selector*
     [[:span.wlPriceBold] [:strong]])

(def *date-added-selector*
     [[:span.commentBlock] [:nobr]])

(def *num-pages-selector*
     [[:span.sortbarText]])

(defn- fetch-url [url]
  (try
    (html/html-resource (java.net.URL. url))
    (catch java.io.FileNotFoundException fnfe nil)))

(defn- wishlist-pages
  "Takes a wishlist ID and returns the number of pages."
  [content]
  (try
    (Integer/parseInt (last (.split (first (:content (first (html/select content *num-pages-selector*)))) " ")))
    (catch Exception e 0)))

(defn- get-wishlist
  "Takes an Amazon wishlist ID and returns all pages of the normal view of it."
  [wishlist-id]
  (let [wishlist-url (str *wishlist-base-url* wishlist-id *options*)
	wishlist-content (fetch-url wishlist-url)
	num-pages (wishlist-pages wishlist-content)]
    (if (> num-pages 1)
      (flatten (concat wishlist-content
		       (for [page (range 2 (inc num-pages))] (fetch-url (str wishlist-url "&page=" page)))))
      wishlist-content)))

(defn- add-wishlist-id
  "Adds the wishlist id to links that start with http://www.amazon.com"
  [link wishlist-id]
  (if (.startsWith link "http://www.amazon.com")
    (str link "?colid=" wishlist-id)
    link))

(defn- add-affiliate-tag
  "Adds an affiliate tag links that start with http://www.amazon.com"
  [wish-item affiliate-id]
  (if (.startsWith (:link wish-item) "http://www.amazon.com")
    (update-in wish-item [:link] str "&tag=" affiliate-id)
    wish-item))

(defn- replace-nil
  "Replaces values in a map that are nil with n/a."
  [wishlist-data]
  (map #(zipmap (keys %) (replace {nil "n/a"} (vals %))) wishlist-data))

(defn- format-date
  "Removes the 'Added ' from the added on date string."
  [date-string]
  (.substring date-string 6))

(defn- limit-length
  [string max-length]
  (if (> (.length string) max-length)
    (str (.substring string 0 max-length) "...")
    string))

(defn wishlist-items
  "Retrieves the item, link, price and date added for all items on an Amazon wishlist.  If affiliate ID is
   included, appends it to all links for items sold on Amazon."
  ([wishlist-id affiliate-id]
     (when-let [items (wishlist-items wishlist-id)]
       (map #(add-affiliate-tag % affiliate-id) items)))
  ([wishlist-id]
     (when-let [content (get-wishlist wishlist-id)]
       (let [items (html/select content *item-selector*)]
	 (replace-nil
	  (for [item items]
	    (let [product (html/select item *product-title-selector*)
		  price (html/select item *price-selector*)
		  date (html/select item *date-added-selector*)]
	      {:item (limit-length (first (:content (first product))) *item-text-max-length*)
	       :link (add-wishlist-id (:href (:attrs (first product))) wishlist-id)
	       :price (first (:content (first price)))
	       :date-added (format-date (first (:content (first date))))})))))))