package org.streaming.spring.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.streaming.spring.StreamingUtils;
import org.streaming.spring.core.DataStream;
import org.streaming.spring.core.StreamProducer;

import java.util.Properties;

/**
 * Created by syodage on 11/10/15.
 */
public class KafkaStreamProducer implements StreamProducer{


    private DataStream dataStream;
    private KafkaProducer<String, String> kafkaProducer;

    public KafkaStreamProducer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("partitioner.class", "org.sham.codestation.kafka.SimplePartitioner");
        props.put("request.required.ack", "1");
        props.put("client.id", "SimpleProducer");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProducer = new KafkaProducer<>(props);
        dataStream = StreamingUtils.getDataStream("test", "testKey", (topic, key, msg) -> {
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
