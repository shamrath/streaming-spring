package org.streaming.spring.kafka;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.streaming.spring.core.StreamConsumer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by syodage on 11/12/15.
 * we don't need to write consumer as there are kafka spout already available.
 */

@Deprecated
public class KafkaStreamConsumer implements StreamConsumer {

    private final ConsumerConnector consumerConnector;
    private final String topic;
    private final int a_numThreads;
    private ExecutorService executor;
    private ConsumerTest consumer;

    public KafkaStreamConsumer(String a_zookeeper, String a_groupId, String a_topic, int a_numThreads) {
        consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId));
        this.topic = a_topic;
        this.a_numThreads = a_numThreads;
    }

    public void stop() throws Exception {
        if (consumerConnector != null) consumerConnector.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumerConnector threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }

    public void start() throws Exception {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        // now launch all the threads
        //
        executor = Executors.newFixedThreadPool(a_numThreads);

        // now create an object to consume the messages
        //
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            consumer = new ConsumerTest(stream, threadNumber);
            executor.submit(consumer);
            threadNumber++;
        }
    }

    @Override
    public byte[] next() throws Exception {
        return consumer.nextMessage();
    }

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);
    }


    public class ConsumerTest implements Runnable, Serializable {
        private KafkaStream m_stream;
        private int m_threadNumber;
        private ConsumerIterator<byte[], byte[]> it;

        public ConsumerTest(KafkaStream a_stream, int a_threadNumber) {
            m_threadNumber = a_threadNumber;
            m_stream = a_stream;
        }

        public void run() {
            it = m_stream.iterator();
            while (it.hasNext()) {
//                System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));

            }
            System.out.println("Shutting down Thread: " + m_threadNumber);
        }

        public byte[] nextMessage() throws Exception {
            if (it.hasNext()) {
                return it.next().message();
            } else {
                throw new Exception("End of stream");
            }
        }
    }

}
