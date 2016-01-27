package org.streaming.perfomance;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by syodage on 1/26/16.
 */
public class FileDataStream implements DataStream{

    private static final Logger log = LoggerFactory.getLogger(FileDataStream.class);
    private final String topic;
    private final String key;
    private final Publisher publisher;
    private BufferedReader br;

    private final String dataFile;

    public FileDataStream(String datafile, String topic, String key, Publisher publisher) {
        this.topic = topic;
        this.key = key;
        this.publisher = publisher;
        this.dataFile = datafile;
    }

    @Override
    public void open() throws Exception {
        br = new BufferedReader(new FileReader(dataFile));
        String line = null;
        log.info("Start publishing data to kafka");
        while ((line = br.readLine()) != null) {
            publisher.publish(topic, key, line);
        }
        log.info("Completed message publishing to kafka");
    }

    @Override
    public void pause() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        br.close();
        log.info("Closed resources");
    }

    @Override
    public void close() throws Exception {

    }

}
