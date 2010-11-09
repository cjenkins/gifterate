(defproject giftcf "0.1-zeta"
  :description "Application to read in and display Amazon wishlists."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [ring/ring-core "0.3.3"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [enlive "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]
		     [appengine-magic "0.3.0-SNAPSHOT"]]
  :aot [giftcf.app_servlet])