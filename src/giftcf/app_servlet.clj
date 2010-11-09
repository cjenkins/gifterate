(ns giftcf.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use com.giftcf.webservice)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method giftcf-app) this request response))
