package org.streaming.spring.core;

/**
 * Created by syodage on 11/26/15.
 * This interface decouple stream producer from stream process framework eg: kafka , rabbitmq
 */
public interface Producer {

    void send(String _topic, String _key, String msg);
}
