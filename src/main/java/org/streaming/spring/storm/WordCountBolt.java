package org.streaming.spring.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by syodage on 11/20/15.
 */
public class WordCountBolt extends BaseRichBolt {
    OutputCollector _collector;
    Map<String, Integer> wordCount = new HashMap<>();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this._collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String val = tuple.getString(0);
        System.out.println(val);

        _collector.emit(tuple, new Values(val));
        _collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("words"));
    }
}
