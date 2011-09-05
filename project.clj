(defproject giftcf "0.1-zeta"
  :description "Application to read in and display Amazon wishlists."
  :dependencies [[org.clojure/clojure "1.2.1"]
	           [org.clojure/clojure-contrib "1.2.0"]
                 [net.cgrand/moustache "1.0.0"]
                 [enlive "1.2.0-alpha1"]]
  :dev-dependencies [[swank-clojure "1.3.2"]
		     [appengine-magic "0.4.4"]
                 [lancet "1.0.1"]]
  :aot [giftcf.app_servlet])