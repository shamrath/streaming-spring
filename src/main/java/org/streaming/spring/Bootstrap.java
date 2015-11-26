package org.streaming.spring;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import org.streaming.spring.core.StreamConsumer;
import org.streaming.spring.kafka.KafkaStreamConsumer;
import org.streaming.spring.kafka.KafkaStreamProducer;
import org.streaming.spring.spark.TPBuilder;

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

        while (count++ < 2) {
            waitFor(5000);
        }
        executorService.shutdown();
        startWordCountTopology();
    }

    private static void startWordCountTopology() {
        TPBuilder builder = new TPBuilder();
        StormTopology stormTopology = builder.wordCountTopology(new Bootstrap().startKafkaConsumer());
        Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(2);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, stormTopology);
        Utils.sleep(10000);
        cluster.killTopology("test");
        cluster.shutdown();
    }

    private StreamConsumer startKafkaConsumer(){
        try {
            String zooKeeper = "localhost:2181";
            String groupId = "group_1";
            String topic = "test";
            int threads = 1;

            StreamConsumer kafkaStreamConsumer = new KafkaStreamConsumer(zooKeeper, groupId, topic, threads);
            kafkaStreamConsumer.start();
            return kafkaStreamConsumer;
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException ie) {
//
//            }
//            kafkaStreamConsumer.stop();
        } catch (Exception e) {
            System.out.println("Error while consuming kafka streams");
            return null;
        }
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
    }

    private static void waitFor(long waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static class Person {
        public String surname;
        public int age;

        public Person(String surname, int age) {
            this.surname = surname;
            this.age = age;
        }
    }
}
