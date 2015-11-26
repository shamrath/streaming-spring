package org.streaming.spring.core;

import java.io.Serializable;

/**
 * Created by syodage on 11/10/15.
 */
public interface StreamConsumer extends Serializable{

    /**
     * Consume messages/requests.
     */
    void start() throws Exception;

    /**
     *
     */
    byte[] next() throws Exception;
    /**
     * Stop message consume
     */
    void stop() throws Exception;
}
