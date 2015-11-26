package org.streaming.spring.spark;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.streaming.spring.core.StreamConsumer;

import java.util.Map;

/**
 * Created by syodage on 11/20/15.
 */
public class KafkaSpout extends BaseRichSpout {

    private final StreamConsumer streamConsumer;
    private SpoutOutputCollector _spoutOutputCollector;

    public KafkaSpout(StreamConsumer streamConsumer) {
        this.streamConsumer = streamConsumer;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("sen"));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        _spoutOutputCollector = spoutOutputCollector;

    }

    @Override
    public void nextTuple() {
        try {
            String tuple = new String(streamConsumer.next());
            _spoutOutputCollector.emit(new Values(tuple));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
