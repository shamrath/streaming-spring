package org.streaming.spring.source;

import org.streaming.spring.core.DataStream;
import org.streaming.spring.core.Producer;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by syodage on 12/1/15.
 */
public class FileDataStream implements DataStream {

    private final String _topic;
    private final String _key;
    private final Producer _producer;
    private BufferedReader br;


    public FileDataStream(String topic, String key, Producer producer) {
        this._topic = topic;
        this._key = key;
        this._producer = producer;
    }

    @Override
    public void open() throws Exception {
        br = new BufferedReader(new FileReader("/Users/syodage/workspace/mydata/data.txt"));
        String line = null;
        while ((line = br.readLine()) != null) {
            _producer.send(_topic, _key, line);
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
