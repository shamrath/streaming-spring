package org.streaming.perfomance;

/**
 * Created by syodage on 1/26/16.
 */
public interface Publisher extends Constants{

    void publish(String msg);


    void close();
}
