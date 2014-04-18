(ns zhihugrabber.core-test
	(:require [clojure.test :refer :all]
		[zhihugrabber.core :refer :all]
		[clojure.java.io :as io]
		[clojure.pprint :as pp]
		[clojure.core.incubator :refer :all]))

(def dummy-config
	{:sources #{:a :b :c} 
	 :latest {:a "2014-04-10T00:00:00+08:00"
	 		  :b "2014-04-11T00:00:00+08:00"
	 		  :c "2014-04-12T00:00:00+08:00"}})

(deftest read-config-test
	(testing "Testing zhihugrabber.core/read-config"
		(testing "with empty configuration file."
			(with-redefs [slurp (constantly "")]
				(is (empty? (read-config)))))
		(testing "with all latest dates set."
			(with-redefs [slurp (constantly (with-out-str (pp/pprint dummy-config)))]
				(is (= dummy-config (read-config)))))
		(testing "with one latest date lacked."
			(with-redefs [slurp (constantly 
									(with-out-str
										(pp/pprint (dissoc-in dummy-config [:latest :a]))))]
				(is (= (assoc-in dummy-config [:latest :a] "1900-01-01T00:00:00+08:00")
					   (read-config)))))
		(testing "with no latest dates."
			(with-redefs [slurp (constantly
									(with-out-str
										(pp/pprint (dissoc dummy-config :latest))))]
				(is (= (assoc dummy-config :latest {:a "1900-01-01T00:00:00+08:00" 
													:b "1900-01-01T00:00:00+08:00" 
													:c "1900-01-01T00:00:00+08:00"})
						(read-config)))))))
