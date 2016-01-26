package org.streaming.perfomance;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by syodage on 1/26/16.
 */
public class KafkaPublisher implements Publisher{


    private KafkaProducer<String,String> kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);
    private DataStream dataStream;

    public KafkaPublisher() throws IOException {
        // load properties from configuration file
        Properties prop = null;
        try {
            prop = Util.loadProperties();
        } catch (IOException e) {
            log.error("Error! configuration loading failed");
            throw e;
        }
        kafkaProducer = new KafkaProducer<String, String>(prop);

    }

    @Override
    public void publish(String topic, String key, String msg) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, msg);
        kafkaProducer.send(record);
    }
}
