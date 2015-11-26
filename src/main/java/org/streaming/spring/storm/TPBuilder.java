package org.streaming.spring.storm;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import org.streaming.spring.core.StreamConsumer;

/**
 * Created by syodage on 11/25/15.
 */
public class TPBuilder {

    public StormTopology wordCountTopology(StreamConsumer streamConsumer) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("sentence", new KafkaSpout(streamConsumer));
        topologyBuilder.setBolt("word", new WordCountBolt()).shuffleGrouping("sentence");
        return topologyBuilder.createTopology();

    }
}
