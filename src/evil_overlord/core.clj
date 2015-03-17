(ns evil-overlord.core
(:require [aleph.http :as http] [byte-streams :as bs] [clojure.data.json :as json] [manifold.stream :as stream] [clojure.core.async :as async] [cheshire.core :as ches])
  (:gen-class))

(def token "TOKEN HERE")

(def url (str "https://slack.com/api/rtm.start?token=" token))

(defn message?
  "is this a real message or meta stuff"
  [m] 
  (= "message" (:type m)))

(defn parse-json [json] (ches/parse-string json true))

(defn split-spliced-stream 
  "splits our spliced manifold stream into 2 core.async
  and adds the transducer"
  [s in-xform out-xform]
  (let [in (async/chan 10 in-xform)
        out (async/chan 10 out-xform)]
    (stream/connect s in)
    (stream/connect out s)
    [in out]))


(defn get-slack-data 
  "returns http response body containing slack meta data"
  [url]
  (-> @(http/get url) :body bs/to-string parse-json))

(defn with-websocket-streams
  [slack callback]
  (let [con @(http/websocket-client (:url slack))]
    (callback con slack)))


(defn slack-handler 
  [con slack]
  (let [
        
        in-xform (comp 
                  (map parse-json)
                  (filter message?)
                  ;; is message for me?
                  (filter (fn 
                            [m]
                            (and (contains? m :text)
                             (.contains (:text m) (:id (:self slack)))))))
        out-xform (comp 
                   (map (fn [m] {:type "message" :text "Yes" :channel (:channel m) :id 1}))
                   (map ches/generate-string))
        streams (split-spliced-stream con in-xform out-xform)
        
        ] 
    (async/pipe 
     (first streams) 
     (nth streams 1))))

(defn -main 
  [& args] 
  (let [slack (get-slack-data url)]
    (with-websocket-streams slack 
      slack-handler)))
