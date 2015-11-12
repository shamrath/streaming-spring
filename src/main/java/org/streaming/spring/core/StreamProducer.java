package org.streaming.spring.core;

/**
 * Created by syodage on 11/10/15.
 */
public interface StreamProducer {


    void start() throws Exception;


    void stop() throws Exception;
}
