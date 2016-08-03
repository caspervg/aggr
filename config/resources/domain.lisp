(in-package :mu-cl-resources)

(defparameter *camelcase-json-variables* nil
  "when non-nil, json variable names should be camelcased, rather than dasherized.")

(defparameter *supply-cache-headers-p* t
  "when non-nil, cache headers are supplied.  this works together with mu-cache.")

(define-resource measurement ()
  :class (s-prefix "casc:Measurement")
  :properties `((longitude :number ,(s-prefix "casp:longitude"))
                (latitude :number ,(s-prefix "casp:latitude"))
                (timestamp :string ,(s-prefix "dct:date"))
                (weight :number ,(s-prefix "casp:weight")))
  :features '(no-pagination-defaults)
  :has-one `((aggregation :via ,(s-prefix "dct:isPartOf")
                          :as "aggregation"))
  :has-many `((measurement :via ,(s-prefix "dct:source")
                           :as "parents"))
  :resource-base (s-url "http://mu.semte.ch/resources/measurements/")
  :on-path "measurements")

(define-resource aggregation ()
  :class (s-prefix "casc:Aggregation")
  :properties `(
                ;; KMeans
                (iterations :number ,(s-prefix "casp:iterations"))
                (num-centroids :number ,(s-prefix "casp:num_centroids"))
                ;; Time
                (start :string ,(s-prefix "casp:start"))
                (end :string ,(s-prefix "casp:end"))
                ;; Grid
                (grid-size :number ,(s-prefix "casp:grid_size"))
                ;; Diff
                (subtrahend :string ,(s-prefix "casp:subtrahend"))
                ;; Average
                (components :string ,(s-prefix "casp:others"))
                (amount :number ,(s-prefix "casp:amount"))
                ;; Diff & Average
                (aggregation-key :string ,(s-prefix "casp:key"))
                ;; Global
                (type :uri ,(s-prefix "casp:aggregation_type"))
                (location :string ,(s-prefix "dct:references"))
                (created :string ,(s-prefix "dct:date")))
  :features '(no-pagination-defaults)
  :has-one `((dataset :via ,(s-prefix "dct:isPartOf") :as "dataset"))
  :has-many `((measurement :via ,(s-prefix "dct:isReplacedBy")
                           :inverse t
                           :as "sources")
              (measurement :via ,(s-prefix "dct:isPartOf")
                           :inverse t
                           :as "measurements"))
  :resource-base (s-url "http://mu.semtech.ch/resources/aggregations")
  :on-path "aggregations")

(define-resource dataset ()
    :class (s-prefix "casc:Dataset")
    :properties `((title :string ,(s-prefix "dct:title")))
    :has-many `((aggregation :via ,(s-prefix "dct:isPartOf")
                                                :inverse t
                                                :as "aggregations"))
    :resource-base (s-url "http://mu.semte.ch/resources/datasets/")
    :on-path "datasets")

;;;;
;; NOTE
;; docker-compose stop; docker-compose rm; docker-compose up
;; after altering this file.

;; Describe your resources here

;; The general structure could be described like this:
;;
;; (define-resource <name-used-in-this-file> ()
;;   :class <class-of-resource-in-triplestore>
;;   :properties `((<json-property-name-one> <type-one> ,<triplestore-relation-one>)
;;                 (<json-property-name-two> <type-two> ,<triplestore-relation-two>>))
;;   :has-many `((<name-of-an-object> :via ,<triplestore-relation-to-objects>
;;                                    :as "<json-relation-property>")
;;               (<name-of-an-object> :via ,<triplestore-relation-from-objects>
;;                                    :inverse t ; follow relation in other direction
;;                                    :as "<json-relation-property>"))
;;   :has-one `((<name-of-an-object :via ,<triplestore-relation-to-object>
;;                                  :as "<json-relation-property>")
;;              (<name-of-an-object :via ,<triplestore-relation-from-object>
;;                                  :as "<json-relation-property>"))
;;   :resource-base (s-url "<string-to-which-uuid-will-be-appended-for-uri-of-new-items-in-triplestore>")
;;   :on-path "<url-path-on-which-this-resource-is-available>")


;; An example setup with a catalog, dataset, themes would be:
;;
;; (define-resource catalog ()
;;   :class (s-prefix "dcat:Catalog")
;;   :properties `((:title :string ,(s-prefix "dct:title")))
;;   :has-many `((dataset :via ,(s-prefix "dcat:dataset")
;;                        :as "datasets"))
;;   :resource-base (s-url "http://webcat.tmp.semte.ch/catalogs/")
;;   :on-path "catalogs")

;; (define-resource dataset ()
;;   :class (s-prefix "dcat:Dataset")
;;   :properties `((:title :string ,(s-prefix "dct:title"))
;;                 (:description :string ,(s-prefix "dct:description")))
;;   :has-one `((catalog :via ,(s-prefix "dcat:dataset")
;;                       :inverse t
;;                       :as "catalog"))
;;   :has-many `((theme :via ,(s-prefix "dcat:theme")
;;                      :as "themes"))
;;   :resource-base (s-url "http://webcat.tmp.tenforce.com/datasets/")
;;   :on-path "datasets")

;; (define-resource distribution ()
;;   :class (s-prefix "dcat:Distribution")
;;   :properties `((:title :string ,(s-prefix "dct:title"))
;;                 (:access-url :url ,(s-prefix "dcat:accessURL")))
;;   :resource-base (s-url "http://webcat.tmp.tenforce.com/distributions/")
;;   :on-path "distributions")

;; (define-resource theme ()
;;   :class (s-prefix "tfdcat:Theme")
;;   :properties `((:pref-label :string ,(s-prefix "skos:prefLabel")))
;;   :has-many `((dataset :via ,(s-prefix "dcat:theme")
;;                        :inverse t
;;                        :as "datasets"))
;;   :resource-base (s-url "http://webcat.tmp.tenforce.com/themes/")
;;   :on-path "themes")

;;