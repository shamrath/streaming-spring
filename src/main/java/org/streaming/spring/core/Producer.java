package org.streaming.spring.core;

/**
 * Created by syodage on 11/26/15.
 */
public interface Producer {

    void send(String _topic, String _key, String msg);
}
