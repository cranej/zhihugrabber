(defproject zhihugrabber "0.1.0"
  :description "Grabber articles from zhuanlan.zhihu.com"
  :url "https://github.com/cranej/zhihugrabber/releases"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.draines/postal "1.11.1"]
                 [clj-http "0.9.1"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/core.incubator "0.1.3"]
                 [hiccup "1.0.5"]]
  :main zhihugrabber.core
  :aot :all)
