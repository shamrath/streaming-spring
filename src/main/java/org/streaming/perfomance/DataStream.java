package org.streaming.perfomance;

/**
 * Created by syodage on 11/10/15.
 */
public interface DataStream extends Constants{

    /**
     * Start data streaming operations
     * @throws Exception
     */
    void open() throws Exception;

    /**
     * Pause data streaming
     * @throws Exception
     */
    void pause() throws Exception;

    /**
     * Stop data streaming
     * @throws Exception
     */
    void stop() throws Exception;

    /**
     * Close all open state channels.
      * @throws Exception
     */
    void close() throws Exception;

//    /**
//     * Stream is fully consumed or still processing
//     * @return
//     */
//    boolean isComplete();
}
