package org.streaming.perfomance;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;
import org.streaming.perfomance.storm.TPBuilder;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by syodage on 1/26/16.
 */
public class Main {

    static int sec_1 = 1000;
    static int min_1 = 60 * sec_1;
    static int hour_1 = 60 * min_1;

    public static void main(String[] args) throws IOException {
        Properties prop = Util.loadProperties();
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
