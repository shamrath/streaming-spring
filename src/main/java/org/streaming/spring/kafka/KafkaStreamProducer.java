package org.streaming.spring.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.spring.StreamingUtils;
import org.streaming.spring.core.DataStream;
import org.streaming.spring.core.StreamProducer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by syodage on 11/10/15.
 */
public class KafkaStreamProducer implements StreamProducer{


    private DataStream dataStream;
    private KafkaProducer<String, String> kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger(KafkaStreamProducer.class);
    private String confFile = "config.properties";

    public KafkaStreamProducer() {


        Properties props = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(confFile);
            props.load(input);
        } catch (IOException e) {
            log.error("Error while loading configurations file : {}", confFile);
            System.exit(1);
        }

        kafkaProducer = new KafkaProducer<>(props);
        dataStream = StreamingUtils.getDataStream(props.getProperty("kafka.topic", "test"),
                props.getProperty("kafka.topic.key", "testKey"), (topic, key, msg) -> {
            ProducerRecord<String, String> pRecord = new ProducerRecord<String, String>(topic, key, msg);
            kafkaProducer.send(pRecord);

        });
    }


    public void start() throws Exception {
        dataStream.open();
    }

    public void stop() throws Exception {
        dataStream.stop();
    }


}
