package org.streaming.perfomance.storm;

import backtype.storm.spout.Scheme;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.IRichSpout;
import io.latent.storm.rabbitmq.RabbitMQSpout;
import storm.kafka.*;

/**
 * Created by syodage on 11/26/15.
 */
public class SpoutProvider {

    private static final String ZOOKEEPER_HOSTS = "localhost:2181";
    private static final String TOPIC = "test";

    public static IRichSpout getKafkaSpout() {
        BrokerHosts bkHost = new ZkHosts(ZOOKEEPER_HOSTS);
        SpoutConfig spoutConfig = new SpoutConfig(bkHost, TOPIC, "/" + TOPIC, "client_1");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.stateUpdateIntervalMs = 2000;
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        spoutConfig.ignoreZkOffsets = false; // use zookeeper status and resume, instead of starting over.
        kafka.api.OffsetRequest.LatestTime();
        return kafkaSpout;
    }

    public static IRichSpout getRabbitMQSpout() {
        // TODO - add custom scheme instead of using scheme comes with kafka-storm
        Scheme scheme = new StringScheme();

        return new RabbitMQSpout(scheme);
    }
}