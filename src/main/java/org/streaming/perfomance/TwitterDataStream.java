package org.streaming.perfomance;

import org.streaming.spring.StreamingUtils;
import org.streaming.spring.core.DataStream;
import twitter4j.*;

/**
 * Created by syodage on 11/10/15.
 */
public class TwitterDataStream implements DataStream {

    private final TwitterStream twitterStream;

    public TwitterDataStream(StatusListener statusListener) {
        twitterStream = new TwitterStreamFactory(StreamingUtils.getTwitterConfig()).getInstance();
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

}
