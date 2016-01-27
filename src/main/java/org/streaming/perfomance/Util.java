package org.streaming.perfomance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static Properties loadProperties() throws IOException {
        if (props == null) {
            synchronized (Util.class) {
                if (props == null){
                    props = new Properties();
                    InputStream input = Util.class.getResourceAsStream("config.properties");
                    if (input == null) {
                        log.error("Error! config file doesn't exist");
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
