package org.streaming.spring.storm;

import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.IRichSpout;
import storm.kafka.*;

import java.util.UUID;

/**
 * Created by syodage on 11/26/15.
 */
public class SpoutProvider {

    private static final String ZOOKEEPER_HOSTS = "localhost:2181";
    private static final String TOPIC = "test";

    public static IRichSpout getKafkaSpout() {
        BrokerHosts bkHost = new ZkHosts(ZOOKEEPER_HOSTS);
        SpoutConfig spoutConfig = new SpoutConfig(bkHost, TOPIC, "/" + TOPIC, UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        return kafkaSpout;
    }
}
