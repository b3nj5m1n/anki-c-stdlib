(ns anki-c-stdlib.core
  (:require
    [hickory.core :as hick]
    [hickory.select :as s]
    [hickory.render :as render]
    [babashka.fs :as fs]
    [clojure.pprint :as pp]
    [clojure.string :as string]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]))
             

(defrecord FunctionDocumentationFile [file-path headers-path content])
(defrecord FunctionDocumentationData [headers-path function-name documentation-html])
(defn fn-doc-data->array [fn-doc-data]
  [(.headers-path fn-doc-data)
   (.function-name fn-doc-data)
   (string/join "\n" (.documentation-html fn-doc-data))])

(defn get-content [file]
  (hick/parse (slurp file)))

(defn get-documentation-files [headers-path directory]
  (let [children (fs/list-dir directory)
        sub-dirs (filter fs/directory? children)]
    (if (empty? sub-dirs)
      (map #(->FunctionDocumentationFile % headers-path (get-content (fs/file %))) children)
      (map #(get-documentation-files
                      (let [current-header (fs/file-name %)]
                        (str headers-path (if (= "reference" current-header) "" current-header) "/"))
                      %)
           sub-dirs))))

(defn get-paragraphs [function-documentation]
  (s/select
    (s/descendant
       (s/class "mw-content-ltr")
       (s/tag :p))
   (hick/as-hickory (.content function-documentation))))
         
(defn render-doc [function-documentation]
  (let [headers-path (.headers-path function-documentation)
        function-name (fs/file-name (fs/strip-ext (.file-path function-documentation)))
        documentation-html (map render/hickory-to-html (get-paragraphs function-documentation))]
       (->FunctionDocumentationData headers-path function-name documentation-html)))

(defn write-csv [fn-doc-datas]
  (with-open [writer (io/writer "test.csv")]
    (csv/write-csv writer fn-doc-datas)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (let [data (get-documentation-files "" (fs/file (fs/cwd) "data/reference"))] ; get all files in the dir containing the documentation
    (->> (flatten data)
      (map render-doc)
      (map fn-doc-data->array)
      (write-csv))))

(comment
  (write-csv
    [(fn-doc-data->array
       (render-doc
                  (->FunctionDocumentationFile "./bla.html" "variadic/" (hick/parse (slurp "/home/b3nj4m1n/Documents/Github/anki-c-stlib/data/data/reference/variadic/va_arg.html")))))]))
  
  
