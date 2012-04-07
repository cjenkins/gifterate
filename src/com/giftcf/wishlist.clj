(ns com.giftcf.wishlist
  (:use com.giftcf.urlutil)
  (:require [net.cgrand.enlive-html :as html]
	    [clojure.contrib.string :as string]))

(def *amazon-site* "http://www.amazon.com")
(def *wishlist-base-url* "http://www.amazon.com/gp/registry/wishlist/")
(def *options* "?reveal=unpurchased&filter=all&layout=standard")

(def *wishlist-search-url* "http://www.amazon.com/gp/registry/search.html?type=wishlist")

;Data selectors

(def *wishlist-title-selector*
     [[:span#listTitleTxt]])

(def *item-selector*
     [[:tbody.itemWrapper]])

(def *product-title-selector*
     [[:span.productTitle] [:strong] [:a]])

(def *price-selector*
     [[:span.wlPriceBold] [:strong]])

(def *date-added-selector*
     [[:span.commentBlock] [:nobr]])

(def *priority-selector*
     [[:span.priorityValueText]])

(def *rating-selector*
     [[:span.asinReviewsSummary] [:a] [:span] [:span]])

(def *num-ratings-selector*
     [[:span.crAvgStars] [:a (html/nth-child 2)]])

(def *comment-selector*
     [[:span.commentValueText]])

(def *picture-selector*
     [[:td.productImage] [:a] [:img]])

;Utility selectors

(def *num-pages-selector*
     [[:span.sortbarText]])

;Wishlist Fetching

(defn- is-wishlist?
  "Verifies if this chunk of HTML is a wishlist page."
  [html-content]
  (not (empty? (html/select html-content *wishlist-title-selector*))))

(defn- wishlist-pages
  "Takes a wishlist ID and returns the number of pages."
  [content]
  (try
    (Integer/parseInt (last (.split (first (:content (first (html/select content *num-pages-selector*)))) " ")))
    (catch Exception e 0)))

(defn- get-wishlist-pages
  [wishlist-url wishlist-html]
  (let [num-pages (wishlist-pages wishlist-html)]
    (if (> num-pages 1)
      (flatten (concat wishlist-html
		       (for [page (range 2 (inc num-pages))] (get-url (str wishlist-url "&page=" page)))))
      wishlist-html)))

(defn- get-wishlist
  "Takes an Amazon wishlist ID and returns all pages of the normal view of it."
  [wishlist-id]
  (let [wishlist-url (str *wishlist-base-url* wishlist-id *options*)
	wishlist-html (get-url wishlist-url)]
    (when (is-wishlist? wishlist-html) (get-wishlist-pages wishlist-url wishlist-html))))

(defn- get-wishlist-by-email
  [email-address]
  (let [search-response (post-url *wishlist-search-url* {:field-name email-address})]
    (when (is-wishlist? (:response search-response)) (get-wishlist-pages (:final-url search-response) (:response search-response)))))

;Wishlist Altering

(defn- add-wishlist-id
  "Adds the wishlist id to links that start with http://www.amazon.com"
  [link wishlist-id]
  (if (.startsWith link *amazon-site*)
    (str link "?colid=" wishlist-id)
    link))

(defn- random-affiliate
  "If threshold is greater then a random number between 0 and 1, use gifterate-20 affiliate code.  Otherwise
   use the passed in affiliate-id."
  [threshold affiliate-id]
  (if (> threshold (rand))
    "gifterate-20"
    affiliate-id))

(defn- add-affiliate-tag
  "Adds an affiliate tag to links that start with http://www.amazon.com"
  [wish-item affiliate-id]
  (if (.startsWith (:link wish-item) *amazon-site*)
    (update-in wish-item [:link] str "&tag=" (random-affiliate 0.1 affiliate-id))
    wish-item))

(defn- map-affiliate-tag
  [items affiliate-id]
  (if (= affiliate-id "")
    (map #(add-affiliate-tag % "gifterate-20") items)
    (map #(add-affiliate-tag % affiliate-id) items)))

(defn- replace-nil
  "Replaces values in a map that are nil with n/a."
  [wishlist-data]
  (map #(zipmap (keys %) (replace {nil "n/a"} (vals %))) wishlist-data))

(defn- filter-results
  "Filter out all results that don't have a date added.  Amazon seems to have some
   superfluous records in their returned HTML."
  [wishlist-data]
  (filter #(not= "n/a" (:date-added %)) wishlist-data))

(defn- format-date
  "Removes the 'Added ' from the added on date string."
  [date-string]
  (try
    (.substring date-string 6)
    (catch Exception e "n/a")))

(defn- format-priority
  [priority-string]
  (if (= " " priority-string)
    "n/a"
    priority-string))

(defn- format-num-ratings
  "Just get the number of ratings."
  [num-ratings-string]
  (first (string/split #" " num-ratings-string)))

;Public API

(defn parse-wishlist-content
  [content]
  (let [items (html/select content *item-selector*)]
    (filter-results
     (replace-nil
      (for [item items]
	(let [product (html/select item *product-title-selector*)
	      price (html/select item *price-selector*)
	      date (html/select item *date-added-selector*)
	      priority (html/select item *priority-selector*)
	      rating (html/select item *rating-selector*)
	      num-ratings (html/select item *num-ratings-selector*)
	      comment (html/select item *comment-selector*)
	      picture (html/select item *picture-selector*)]
	  {:item (first (:content (first product)))
	   :link (:href (:attrs (first product)))
	   :price (first (:content (first price)))
	   :date-added (format-date (first (:content (first date))))
	   :priority (format-priority (first (:content (first priority))))
	   :rating (first (:content (first rating)))
	   :num-ratings (when-let [num-ratings (first (:content (first num-ratings)))]
			  (format-num-ratings num-ratings))
	   :comment (first (:content (first comment)))
	   :picture (:src (:attrs (first picture)))}))))))

(defn wishlist-items
  "Retrieves the item, link, price and date added for all items on an Amazon wishlist.  If affiliate ID is
   included, appends it to all links for items sold on Amazon."
  ([wishlist-id affiliate-id]
     (when-let [items (wishlist-items wishlist-id)]
       (map-affiliate-tag items affiliate-id)))
  ([wishlist-id]
     (when-let [content (get-wishlist wishlist-id)]
       (parse-wishlist-content content))))

(defn wishlist-items-by-email
  "Looks up a wishlist by email address and returns the items on it."
  ([email-address affiliate-id]
     (when-let [items (wishlist-items-by-email email-address)]
       (map-affiliate-tag items affiliate-id)))
  ([email-address]
     (when-let [content (get-wishlist-by-email email-address)]
       (parse-wishlist-content content))))