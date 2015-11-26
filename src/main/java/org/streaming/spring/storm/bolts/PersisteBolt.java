package org.streaming.spring.storm.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by syodage on 11/26/15.
 */
public class PersisteBolt extends BaseRichBolt {

    private OutputCollector _outputCollector;
    private BufferedWriter bufferedWriter;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _outputCollector = outputCollector;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("streaming_put.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            String wordsCount = tuple.getString(0);
            bufferedWriter.flush();
            bufferedWriter.write(wordsCount);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
