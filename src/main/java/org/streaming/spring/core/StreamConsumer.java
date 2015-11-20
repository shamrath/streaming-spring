package org.streaming.spring.core;

/**
 * Created by syodage on 11/10/15.
 */
public interface StreamConsumer {

    /**
     * Consume messages/requests.
     */
    void start() throws Exception;

    /**
     * Stop message consume
     */
    void stop() throws Exception;
}
