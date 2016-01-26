package org.streaming.perfomance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by syodage on 1/26/16.
 */
public class Util {

    private static Properties props = null;

    public static Properties loadProperties() throws IOException {
        if (props == null) {
            synchronized (Util.class) {
                if (props == null){
                    props = new Properties();
                    InputStream input = Util.class.getResourceAsStream("config.properties");
                    props.load(input);
                }
            }
        }
        return props;
    }
}
