(ns zhihugrabber.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.pprint :as pp]
            [postal.core :as mail]
            [hiccup.core :as hi]
            [hiccup.page :as hip]))

(def base-api-url "http://zhuanlan.zhihu.com/api/columns/%s/posts?limit=100")

(def base-directory
  "Directory of running jar"
  (-> (class *ns*)
      .getProtectionDomain
      .getCodeSource .getLocation .getPath io/file .getParent))

(def ^:dynamic config-file
  (io/file base-directory "zhihugrabber.edn"))

(defn read-config
  []
  (if-let [c (edn/read-string (slurp config-file))]
    (let [sources (:sources c)
          default-time "1900-01-01T00:00:00+08:00"
          latestSyncedDate (merge
                            (into {} (map #(vector % default-time) sources))
                            (:latest c))]
      (assoc c :latest latestSyncedDate))))

(defn grab-one
  "Fetch article collection form one source since lastDate from source"
  [source lastDate]
  (let [resp (client/get
              (format
               base-api-url
               (name source)))
        body (json/read-str (str (:body resp)))]
    (filter #(> (compare (get % "publishedTime") lastDate) 0) body)))

(defn- format-article
  [{:strs [title content]}]
  (hi/html [:br]
           [:a {:name title}]
           [:h1 title [:br]]
           [:p content]))

(defn- build-toc
  "Build TOC for articles"
  [articles]
  (str (hi/html [:a {:name "TOC"}] [:center [:h4 "CONTENTS"]])
       (apply str
              (map #(let [{:strs [title]} %]
                      (hi/html [:a {:href (str "#" title)}
                                [:b title]]
                               [:br]))
                   articles))
       " <!------------><mbp:pagebreak /><!---------------->"))

(defn pack-to-file
  [articles]
  (if (seq articles)
    (let [file (java.io.File/createTempFile "zhihugrabber" ".html")
          sorted-articles (sort-by #(get % "publishedTime") articles)
          newLatestDate (get (last sorted-articles) "publishedTime")]
      (spit file
            (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">
<html>
<head>
  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">
</head>
<body>"
                 (build-toc sorted-articles))
            :append true)
      (doall
       (map #(spit
              file
              (format-article %)
              :append true)
            sorted-articles))
      (spit file "</body></html>" :append true)
      [file newLatestDate])))

(defn grab-all
  [{:keys [sources latest] :as config}]
  (let [available-sources (select-keys latest sources)]
    (filter #(not (nil? (second %)))
            (doall
             (map
              (fn [[source date]]
                (vector source
                        ((comp pack-to-file grab-one) source date)))
              available-sources)))))

(def run-time-string
  (.format (java.text.SimpleDateFormat. "yyyyMMddHHmmss") (java.util.Date.)))

(defn send-files
  [files {:keys [mail]}]
  (doseq [[source file] files]
    (let [attachment-name (str (name source) run-time-string ".html")
          {:keys [smtp-server send-to send-from subject]} mail]
      (mail/send-message
       smtp-server
       {:from send-from :to send-to :subject subject
        :body [{:type "text/plain" :content ""}
               {:type :attachment
                :content file
                :file-name attachment-name}]}))))

(defn -main
  []
  (if-let [config (read-config)]
    (let [result (grab-all config)
          new-dates (into {} (map #(vector (first %) (last (last %))) result))
          merged-latest (merge (:latest config) new-dates)
          new-config (assoc config :latest merged-latest)
          files-to-send (into {} (map #(vector (first %) (first (last %))) result))]
      (send-files files-to-send config)
      (pp/pprint new-config (io/writer config-file)))))
