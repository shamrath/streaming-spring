package org.streaming.perfomance;

import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by syodage on 11/10/15.
 */
public class TwitterDataStream implements DataStream {

    private final TwitterStream twitterStream;

    private static final String _consumerkey = "6MDYbriKbG5IpxbUPZoGP9c6u";
    private static final String _consumerSecret = "ecVj5Xhupfv9cdVYTAzrP2GctQwmJPTa3LnUkzZOPvQXDrDZvl";
    private static final String _accessToken = "375026574-tsaeYcvrs8qXJfP4CwEFO1q1zHrTusmnuO9F4JjP";
    private static final String _accessTokenSecret = "aK2QnRotM47tpqOTM5WF9PbRplthdGDW2QudmijVbFcuV";

    public TwitterDataStream(StatusListener statusListener) {
        twitterStream = new TwitterStreamFactory(getTwitterConfig()).getInstance();
        twitterStream.addListener(statusListener);
        twitterStream.filter(new FilterQuery().track(new String[]{"java", "scala", "python", "test", "got", "today"})
                .language(new String[]{"en"}));
    }

    public void open() throws Exception {
        twitterStream.sample();
    }

    public void pause() throws Exception {

    }

    public void stop() throws Exception {
        twitterStream.cleanUp();
        twitterStream.shutdown();
    }

    public void close() throws Exception {

    }


    public static Configuration getTwitterConfig() {
        Configuration config = new ConfigurationBuilder().setOAuthConsumerKey(_consumerkey).setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken).setOAuthAccessTokenSecret(_accessTokenSecret).build();
        return config;
    }

    private static class TwitterStreamListener implements StatusListener {


        private Publisher _producer;
        private String _topic;
        private String _key;


        public TwitterStreamListener(String _topic, String _key) {
            this._topic = _topic;
            this._key = _key;
        }

        public TwitterStreamListener(String _topic, String _key, Publisher producer) {
            this._producer = producer;
            this._topic = _topic;
            this._key = _key;
        }

        public void onStatus(Status status) {
            if (status.getLang().equals("en")) {
                _producer.publish(status.getLang() + " : " + status.getText());
            }
        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            _producer.publish(String.valueOf(statusDeletionNotice.getStatusId()));

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
