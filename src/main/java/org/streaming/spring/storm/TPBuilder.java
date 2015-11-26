package org.streaming.spring.storm;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import org.streaming.spring.storm.bolts.PersisteBolt;
import org.streaming.spring.storm.bolts.WordCountBolt;

/**
 * Created by syodage on 11/25/15.
 */
public class TPBuilder {

    public StormTopology wordCountTopology() {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("sentence", SpoutProvider.getKafkaSpout());
        topologyBuilder.setBolt("word", new WordCountBolt()).shuffleGrouping("sentence");
        topologyBuilder.setBolt("writeToFile", new PersisteBolt()).shuffleGrouping("word");
        return topologyBuilder.createTopology();

    }
}
