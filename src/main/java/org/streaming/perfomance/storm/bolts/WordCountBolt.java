package org.streaming.perfomance.storm.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by syodage on 11/20/15.
 */
public class WordCountBolt extends BaseRichBolt {
    OutputCollector _collector;
    Map<String, Integer> wordCountMap = new HashMap<>();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this._collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String val = tuple.getString(0);
        String word = null;
        StringTokenizer stringTokenizer = new StringTokenizer(val," ");
        while (stringTokenizer.hasMoreTokens()) {
            word = stringTokenizer.nextToken();
            Integer count = wordCountMap.get(word);
            if (count != null) {
                count++;
            } else {
                count = 1;
            }
            wordCountMap.put(word, count);
//            _collector.emit(tuple, new Values(stringTokenizer.nextToken()));
        }
        _collector.ack(tuple);

    }

    @Override
    public void cleanup() {
        super.cleanup();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("streaming_out.txt"));
            for (Map.Entry<String, Integer> stringIntegerEntry : wordCountMap.entrySet()) {
                bufferedWriter.write(stringIntegerEntry.getKey() + " -> " + stringIntegerEntry.getValue());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//        outputFieldsDeclarer.declare(new Fields("words"));
    }
}
