(defproject anki-c-stdlib "0.1.0-SNAPSHOT"
  :description "Convert cppreference html files to anki deck"
  :url "https://github.com/b3nj5m1n/anki-c-stdlib"
  :main anki-c-stdlib.core
  :dependencies [
                 [org.clojure/clojure "1.11.1"]
                 [hickory "0.7.1"]
                 [babashka/fs "0.2.12"]
                 [org.clojure/data.csv "1.0.1"]]
  :repl-options {:init-ns anki-c-stdlib.core})
