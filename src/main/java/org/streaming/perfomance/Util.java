package org.streaming.perfomance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by syodage on 1/26/16.
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    private static Properties props = null;

    public static Properties loadProperties(boolean originalConfig) throws IOException {
//        File f = new File("test.txt");
//        if (f.createNewFile()) {
//            log.info("File Created");
//        } else {
//            log.error("File doesn't create");
//        }
        if (props == null) {
            synchronized (Util.class) {
                if (props == null){
                    props = new Properties();
                    InputStream input;
                    if (originalConfig) {
                        input = Util.class.getClassLoader().getResourceAsStream("config.properties");
                    } else {
                        input = new FileInputStream("/Users/syodage/Projects/streaming-spring/src/main/resources/config.properties");
                    }
                    if (input == null) {
                        log.error("Error! config file doesn't exist");
                        System.exit(-1);
                    } else {
                        props.load(input);
                        log.info("property loaded");
                    }
                }
            }
        }
        return props;
    }
}
