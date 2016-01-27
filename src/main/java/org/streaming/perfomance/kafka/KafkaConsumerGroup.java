package org.streaming.perfomance.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.Consumer;
import org.streaming.perfomance.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by syodage on 1/26/16.
 */
public class KafkaConsumerGroup implements Consumer{

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerGroup.class);
    private final ConsumerConnector consumer;
    private final String topic;
    private final int consumerCount;
    private ExecutorService executor;

    public KafkaConsumerGroup(int consumerCount) {
        Properties prop  = null;
        try {
            prop = Util.loadProperties();
        } catch (IOException e) {
            log.error("Error! Property file load attempt failed", e);
            System.exit(-1);
        }
        ConsumerConfig consumerConfig = new ConsumerConfig(prop);
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
        this.topic = prop.getProperty("kafka.topic");
        this.consumerCount = consumerCount;
    }

    @Override
    public void consume() {
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, consumerCount);
        Decoder<String> keyDecoder = new StringDecoder(null);
        Decoder<String> valueDecoder = new StringDecoder(null);
        Map<String, List<KafkaStream<String, String>>> messageStreams = consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        List<KafkaStream<String, String>> m_streams = messageStreams.get(topic);

        executor = Executors.newFixedThreadPool(consumerCount);

        int threadNumber = 0;
        for (KafkaStream<String, String> m_stream : m_streams) {
            executor.submit(new KafkaStreamConsumer(m_stream, threadNumber));
            threadNumber++;
        }
    }
}
