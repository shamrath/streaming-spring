package org.streaming.perfomance;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by syodage on 1/26/16.
 */
public class FileDataStream implements DataStream{

    private final String topic;
    private final String key;
    private final Publisher publisher;
    private BufferedReader br;

    private static final Logger log = LoggerFactory.getLogger(FileDataStream.class);

    public FileDataStream(String topic, String key, Publisher publisher) {
        this.topic = topic;
        this.key = key;
        this.publisher = publisher;
    }

    @Override
    public void open() throws Exception {
        br = new BufferedReader(new FileReader("/Users/syodage/workspace/mydata/data.txt"));
        String line = null;
        while ((line = br.readLine()) != null) {
            publisher.publish(topic, key, line);
        }
    }

    @Override
    public void pause() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        br.close();
    }

    @Override
    public void close() throws Exception {

    }

}
