(ns com.giftcf.test-wishlist
  (:use clojure.test com.giftcf.wishlist)
  (:require [net.cgrand.enlive-html :as html]))

(def test-data (html/html-resource (java.io.File. "./test/1EK29ZLJDUVDD.htm")))

(def parsed-items (parse-wishlist-content test-data))

(def test-item (second parsed-items))

(deftest product-test
  (is (= "A Thousand Splendid Suns"
	 (:item test-item))))

(deftest link-test
  (is (= "http://www.amazon.com/Thousand-Splendid-Suns-Khaled-Hosseini/dp/159448385X/ref=wl_it_dp_v?ie=UTF8&coliid=I2BLAVVD5X4R5H&colid=1EK29ZLJDUVDD"
	 (:link test-item))))

(deftest picture-test
  (is (= "http://ecx.images-amazon.com/images/I/51ELJuzFsyL._SL500_PIsitb-sticker-arrow-big,TopRight,35,-73_OU01_SL135_.jpg"
	 (:picture test-item))))

(deftest price-test
  (is (= "$9.70" (:price test-item))))

(deftest date-test
  (is (= "August 25, 2011" (:date-added test-item))))

(deftest rating-test
  (is (= "4.6 out of 5 stars" (:rating test-item)))
  (is (= "1,704" (:num-ratings test-item))))

(deftest priority-test
  (is (= "highest" (:priority test-item))))

(deftest comment-test
  (is (= "Kite Runner was such a great book.  Can't wait to read this one!"
	 (:comment test-item))))