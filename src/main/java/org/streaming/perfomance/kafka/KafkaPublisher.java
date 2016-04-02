package org.streaming.perfomance.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.ConfigReader;
import org.streaming.perfomance.DataStream;
import org.streaming.perfomance.Publisher;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by syodage on 1/26/16.
 */
public class KafkaPublisher implements Publisher {


    private final String topic;
    private final String key;
    private KafkaProducer<String,String> kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);
    private DataStream dataStream;
    private int partitionCount;
    private int partition = 0;

    public KafkaPublisher() throws IOException {
        this.partitionCount = ConfigReader.getIntProperty(KAFKA_PARTITION_COUNT);
        this.topic = ConfigReader.getProperty(KAFKA_TOPIC);
        this.key = ConfigReader.getProperty(KAFKA_TOPIC_KEY);
        kafkaProducer = new KafkaProducer<>(ConfigReader.getProperties());
    }

    @Override
    public void publish(String msg) {
        partition = partition % partitionCount;
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.topic, partition, this.key, msg);
        kafkaProducer.send(record);
        partition++;
    }

    @Override
    public void close() {
        // nothing to do
    }
}
