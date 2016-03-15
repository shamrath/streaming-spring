package org.streaming.perfomance.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.DataStream;
import org.streaming.perfomance.Publisher;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by syodage on 1/26/16.
 */
public class KafkaPublisher implements Publisher {


    private KafkaProducer<String,String> kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);
    private DataStream dataStream;
    private int partitionCount;
    private int partition = 0;

    public KafkaPublisher(Properties prop, int partitionCount) throws IOException {
        this.partitionCount = partitionCount;
        kafkaProducer = new KafkaProducer<>(prop);
    }

    @Override
    public void publish(String topic, String key, String msg) {
        partition = partition % partitionCount;
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, partition, key, msg);
        kafkaProducer.send(record);
        partition++;
    }
}
