package org.streaming.spring;

import org.streaming.spring.core.StreamConsumer;
import org.streaming.spring.kafka.KafkaStreamConsumer;
import org.streaming.spring.kafka.KafkaStreamProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by syodage on 11/10/15.
 */
public class Bootstrap {
    static int count = 0;

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> new Bootstrap().startKafkaProducer());
        executorService.submit(() -> new Bootstrap().startKafkaConsumer());
        while (count < 2) {
            waitFor(5000);
        }
        executorService.shutdown();
    }

    private void startKafkaConsumer(){
        try {
            String zooKeeper = "localhost:2181";
            String groupId = "group_1";
            String topic = "test";
            int threads = 1;

            StreamConsumer example = new KafkaStreamConsumer(zooKeeper, groupId, topic, threads);
            example.start();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {

            }
            example.stop();
        } catch (Exception e) {
            System.out.println("Error while consuming kafka streams");
        }
        ++count;
    }

    public void startKafkaProducer() {
        try {
            KafkaStreamProducer streamKafkaProducer = new KafkaStreamProducer();
            streamKafkaProducer.start();
            waitFor(30000);
            streamKafkaProducer.stop();
        } catch (Exception e) {
            System.out.println("Error while producing stream producer");
        }
        ++count;
    }

    private static void waitFor(long waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
