package org.streaming.spring;

import org.streaming.spring.kafka.KafkaStreamProducer;

/**
 * Created by syodage on 11/10/15.
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        startKafkaProducer();
    }

    public static void startKafkaProducer() throws Exception {
        KafkaStreamProducer streamKafkaProducer = new KafkaStreamProducer();
        streamKafkaProducer.start();
        waitFor(10000);
        streamKafkaProducer.stop();
    }

    private static void waitFor(long waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
