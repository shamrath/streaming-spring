/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.streaming.spring;

import org.streaming.spring.core.DataStream;
import org.streaming.spring.core.Producer;
import org.streaming.spring.source.FileDataStream;
import org.streaming.spring.source.TwitterDataStream;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class StreamingUtils {
    private static final String _consumerkey = "6MDYbriKbG5IpxbUPZoGP9c6u";
    private static final String _consumerSecret = "ecVj5Xhupfv9cdVYTAzrP2GctQwmJPTa3LnUkzZOPvQXDrDZvl";
    private static final String _accessToken = "375026574-tsaeYcvrs8qXJfP4CwEFO1q1zHrTusmnuO9F4JjP";
    private static final String _accessTokenSecret = "aK2QnRotM47tpqOTM5WF9PbRplthdGDW2QudmijVbFcuV";

    public static Configuration getTwitterConfig() {
        Configuration config = new ConfigurationBuilder().setOAuthConsumerKey(_consumerkey).setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken).setOAuthAccessTokenSecret(_accessTokenSecret).build();
        return config;
    }

    public static DataStream getDataStream(String topic , String key , Producer producer) {
//        return new TwitterDataStream(new TwitterStreamListener(topic, key , producer));
        return new FileDataStream(topic, key, producer);
    }

    private static class TwitterStreamListener implements StatusListener {


        private Producer _producer;
        private String _topic;
        private String _key;


        public TwitterStreamListener(String _topic, String _key) {
            this._topic = _topic;
            this._key = _key;
        }

        public TwitterStreamListener(String _topic, String _key, Producer producer) {
            this._producer = producer;
            this._topic = _topic;
            this._key = _key;
        }

        public void onStatus(Status status) {
            if (status.getLang().equals("en")) {
                _producer.send(_topic, _key,status.getLang() + " : " + status.getText());
            }
        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            _producer.send(_topic, _key, String.valueOf(statusDeletionNotice.getStatusId()));

        }

        public void onTrackLimitationNotice(int i) {

        }

        public void onScrubGeo(long l, long l1) {

        }

        public void onStallWarning(StallWarning stallWarning) {

        }

        public void onException(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
