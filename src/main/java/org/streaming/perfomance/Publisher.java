package org.streaming.perfomance;

/**
 * Created by syodage on 1/26/16.
 */
public interface Publisher {

    void publish(String topic, String key, String msg);
}
