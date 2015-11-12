package org.streaming.spring.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.streaming.spring.core.DataStream;
import org.streaming.spring.core.StreamProducer;
import org.streaming.spring.source.TwitterDataStream;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

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
        kafkaProducer = new KafkaProducer<String, String>(props);
        dataStream = getTwitterDataStream();
    }

    private DataStream getTwitterDataStream() {
        return new TwitterDataStream(new TwitterStreamListener("test", "testKey"));
    }

    public void start() throws Exception {
        dataStream.open();
    }

    public void stop() throws Exception {
        dataStream.stop();
    }

    private class TwitterStreamListener implements StatusListener {


        private ProducerRecord<String, String> pRecord;
        private String _topic;
        private String _key;

        public TwitterStreamListener(String _topic, String _key) {
            this._topic = _topic;
            this._key = _key;
        }

        public void onStatus(Status status) {
            if (status.getLang().equals("en")) {
                pRecord = new ProducerRecord<String, String>(_topic, _key, status.getLang() + " : " + status.getText());
                kafkaProducer.send(pRecord);
            }
        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            pRecord = new ProducerRecord<String, String>(_topic, _key, String.valueOf(statusDeletionNotice.getStatusId()));
            kafkaProducer.send(pRecord);
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
