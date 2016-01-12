package org.streaming.spring;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import org.streaming.spring.core.StreamConsumer;
import org.streaming.spring.kafka.KafkaStreamConsumer;
import org.streaming.spring.kafka.KafkaStreamProducer;
import org.streaming.spring.storm.TPBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by syodage on 11/10/15.
 */
public class Bootstrap {
    static int count = 0;
    static int sec_1 = 1000;
    static int min_1 = 60 * sec_1;
    static int hour_1 = 60 * min_1;


    public static void main(String[] args) throws Exception {
        runKafkaProducer();
        startWordCountTopology();
    }

    private static void runKafkaProducer() throws InterruptedException, java.util.concurrent.ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> kafkaProducerFuture = executorService.submit(() -> new Bootstrap().startKafkaProducer());
        kafkaProducerFuture.get(); // wait until kafka producer finish it works.
        executorService.shutdown();
    }

    private static void startWordCountTopology() {
        TPBuilder builder = new TPBuilder();
        StormTopology stormTopology = builder.wordCountTopology();
        Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(2);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, stormTopology);
        Utils.sleep(min_1);
        cluster.killTopology("test");
        cluster.shutdown();
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
