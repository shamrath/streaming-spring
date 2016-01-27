package org.streaming.perfomance.storm;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import org.streaming.spring.storm.bolts.WordCountBolt;

/**
 * Created by syodage on 11/25/15.
 */
public class TPBuilder {

    public StormTopology wordCountTopology() {
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // Kafka spout
        topologyBuilder.setSpout("sentence", SpoutProvider.getKafkaSpout());

        // RabbitMQ spout
/*        ConnectionConfig connectionConfig = new ConnectionConfig("localhost", 5672, "guest", "guest",
                ConnectionFactory.DEFAULT_VHOST, 10); // host, port, username, password, virtualHost, heartBeat
        ConsumerConfig spoutConfig = new ConsumerConfigBuilder().connection(connectionConfig)
                .queue("your.rabbitmq.queue")
                .prefetch(200)
                .requeueOnFail()
                .build();
        topologyBuilder.setSpout("sentence", SpoutProvider.getRabbitMQSpout()).addConfigurations(spoutConfig.asMap())
                .setMaxSpoutPending(200);*/

        topologyBuilder.setBolt("word", new WordCountBolt()).shuffleGrouping("sentence");
//        topologyBuilder.setBolt("writeToFile", new PersistBolt()).shuffleGrouping("word");
        return topologyBuilder.createTopology();

    }


}
