package org.streaming.perfomance;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.kafka.KafkaConsumerGroup;
import org.streaming.perfomance.kafka.KafkaPublisher;
import org.streaming.perfomance.storm.TPBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by syodage on 1/26/16.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static int sec_1 = 1000;
    static int min_1 = 60 * sec_1;
    static int hour_1 = 60 * min_1;

    public static void main(String[] args) throws Exception {
//        args = new String[2];
//        args[0] = "-p";
//        args[1] = "-d"; args[2] = "data.txt";

        if(args.length == 0){
            printOptions();
            return;
        }

        Properties prop = Util.loadProperties(false);
        prop.list(System.out);
        Map<String, String> options = parseArgs(args);
        for (String s : options.keySet()) {
            log.info("{}  -> {}", s, options.get(s));
        }
        if (options.containsKey("-p")) {
            log.info("Publisher starting");
            publishData(options.get("-d"), prop);
            log.info("Publisher completed");
        }

        if (options.containsKey("-c")) {
            log.info("Consumer starting");
            consumeData(Integer.parseInt(options.get("-c")), prop);
            log.info("Consumer completed");
        }
    }

    private static void publishData(String datafile, Properties prop) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String topic = prop.getProperty("kafka.topic");
                    String key = prop.getProperty("kafka.topic.key");
                    Publisher publisher = new KafkaPublisher(prop);
                    DataStream dataStream = new TimeDataStream(topic, key, publisher, 1000);
//        FileDataStream dataStream = new FileDataStream(datafile, topic, key, publisher);
                    dataStream.open();

                    // close publisher
                    dataStream.close();
                } catch (Exception e) {
                    log.error("Consumer Error! ", e);
                }
            }
        }).start();

    }

    private static void consumeData(int consumerCount, Properties prop) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Consumer consumer = new KafkaConsumerGroup(consumerCount, prop);
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        consumer.close();
                    }
                }));
                consumer.consume();
            }
        }).start();

    }

    private static Map<String, String> parseArgs(String[] args) {
        int n = args.length;
        log.info("*****************  n = " + n);
        Map<String,String> options = new HashMap<>();
        int i = 0;
        while (i < n) {
            if (args[i].equalsIgnoreCase("-p")) {
                options.put("-p", null);
            } else if (args[i].equalsIgnoreCase("-c")) {
                options.put("-c", args[++i]);
            } else if (args[i].equalsIgnoreCase("-d")) {
                options.put("-d", args[++i]);
            } else {
                log.error("Invalid argument: {}" , args[i]);
            }
            i++;
        }
        return options;
    }

    private static void printOptions() {
        log.info("-p <dataFile> : publish lines in datafile as messages to kafka");
        log.info("-c <consumeCount> : start consumer group with <consumerCount> consumers");
    }

    private static void startWordCountTopology() {
        TPBuilder builder = new TPBuilder();
        StormTopology stormTopology = builder.wordCountTopology();
        Config conf = new Config();
//        conf.setDebug(true);
        conf.setNumWorkers(2);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, stormTopology);
        Utils.sleep(min_1);
        cluster.killTopology("test");
        cluster.shutdown();
    }
}
