(defproject giftcf "0.1-zeta"
  :description "Application to read in and display Amazon wishlists."
  :dependencies [[org.clojure/clojure "1.2.1"]
	           [org.clojure/clojure-contrib "1.2.0"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [enlive "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.3.1"]
		     [appengine-magic "0.4.3"]]
  :aot [giftcf.app_servlet])