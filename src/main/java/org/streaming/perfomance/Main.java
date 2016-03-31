package org.streaming.perfomance;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.kafka.KafkaConsumerGroup;
import org.streaming.perfomance.kafka.KafkaPublisher;
import org.streaming.perfomance.rabbitmq.RabbitmqConsumer;
import org.streaming.perfomance.rabbitmq.RabbitmqPublisher;
import org.streaming.perfomance.storm.TPBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by syodage on 1/26/16.
 * command line arguments
 * -kp = start kafka producer
 *      -d = data file
 *      -n = num of messages
 * -kc = start kafka consumer
 *      -c = consumer count
 * -rp = start rabbitmq publisher
 *      -d data file
 *      -n num of messages
 * -rc = start rabbitmq consumer
 */
public class Main implements Constants{

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

        Properties prop = ConfigReader.loadProperties(true);
//        prop.list(System.out);
        Map<String, String> options = parseArgs(args);
        for (String s : options.keySet()) {
            log.info("{}  -> {}", s, options.get(s));
        }
        Publisher publisher = null;
        Consumer consumer = null;
        switch (args[0]){
            case KAFKA_PUBLISHER:
                log.info("Kafka Publisher starting");
                publisher = new KafkaPublisher();
                publishData(options.get("-d"), Integer.valueOf(options.get("-n")), publisher);
                log.info("Kafka Publisher completed");
                break;
            case KAFKA_CONSUMER:
                log.info("Kafka Consumer starting");
                consumer = new KafkaConsumerGroup(Integer.valueOf(options.get("-n")));
                consumeData(consumer);
                log.info("Kafka Consumer completed");
                break;
            case RABBITMQ_PUBLISHER:
                log.info("Rabbitmq Publisher starting");
                publisher = new RabbitmqPublisher();
                publishData(options.get("-d"), Integer.valueOf(options.get("-n")), publisher);
                log.info("Rabbitmq Publisher completed");
                break;
            case RABBITMQ_CONSUMER:
                log.info("Rabbitmq Consumer Starting");
                consumer = new RabbitmqConsumer();
                consumeData(consumer);
                log.info("Rabbitmq Consumer completed");
                break;
            default:
                log.error("Invalid first argument");
                break;
        }

    }

    private static void publishData(String datafile, int n, Publisher publisher) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String topic = ConfigReader.getProperty(KAFKA_TOPIC);
                    String key = ConfigReader.getProperty(KAFKA_TOPIC_KEY);
                    String data = getData(datafile);
                    DataStream dataStream = new PerfDataStream(topic, key, publisher, n, data);
//        FileDataStream dataStream = new FileDataStream(datafile, topic, key, publisher);
                    dataStream.open();

                    // close publisher
                    dataStream.close();
                } catch (Exception e) {
                    log.error("Consumer Error! ", e);
                }
            }

            private String getData(String dataFile) throws IOException {
                StringBuffer sb = new StringBuffer();
                if (dataFile != null) {
                    String line = null;
                    BufferedReader br = new BufferedReader(new FileReader(dataFile));
                    while ((line = br.readLine()) != null){
                        sb.append(line);
                    }
                }
                return sb.toString();
            }
        }).start();

    }

    private static void consumeData(Consumer consumer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            switch (args[i]){
                case KAFKA_PUBLISHER:
                case KAFKA_CONSUMER:
                case RABBITMQ_PUBLISHER:
                case RABBITMQ_CONSUMER:
                    options.put(args[i], null);
                    break;
                case "-d":case "-n":
                    options.put(args[i], args[++i]);
                    break;
                default:
                    log.error("Invalid argument: {}" , args[i]);
                    break;
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
