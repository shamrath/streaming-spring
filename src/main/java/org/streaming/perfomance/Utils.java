package org.streaming.perfomance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by syodage on 1/26/16.
 */
public class Utils implements Constants{

    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static final boolean isDebug = ConfigReader.getBoolProperty(ENBALE_DEBUG);

    public static Long getProduceTime(String message, long time) {
        int length = message.length();
        String sTime;
        int t_len = String.valueOf(time).length();
        if (length > t_len) {
            sTime = message.substring(length - t_len, length);
        } else {
            sTime = message;
        }
        if (isDebug) {
            log.info("Consumed message length : {}, prodTime : {} ", length, sTime);
        }
        return Long.valueOf(sTime);
    }
}
