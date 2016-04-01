/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.streaming.perfomance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This produce current time as data stream
 */
public class PerfDataStream implements DataStream {

    private static final Logger log = LoggerFactory.getLogger(PerfDataStream.class);
    private final String topic;
    private final String key;
    private final Publisher publisher;
    private final int count;
    private final String data;
    private final boolean isDebug;

    public PerfDataStream(String topic, String key, Publisher publisher, int count, String data) {
        this.topic = topic;
        this.key = key;
        this.publisher = publisher;
        this.count = count;
        this.data = data;
        this.isDebug = ConfigReader.getBoolProperty(ENBALE_DEBUG, false);
    }

    @Override
    public void open() throws Exception {
        log.info("Start publishing data to kafka");
        int i = count;
        long sleep = ConfigReader.getLongProperty(PUBLISHER_MESSAGE_DELAY, 100);
        long time = System.nanoTime();
        String chunk = data.substring(0, data.length() - String.valueOf(time).length() + 1);
        if (isDebug) {
            log.info("Data size : {}, chunk size : {}, thread sleep time ; {}", data.length(), chunk.length(), sleep);
        }
        while (i > 0) {
            StringBuilder sb = new StringBuilder(chunk);
            if (isDebug) {
                log.info("Publishing {} message", i);
            }
            time = System.nanoTime();
            sb.append(time);
            publisher.publish(topic, key, sb.toString());
            if (isDebug) {
                log.info("Published message size : {}", sb.length());
            }
            Thread.sleep(sleep);
            i--;
        }
        log.info("Completed message publishing to kafka");
    }

    @Override
    public void pause() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }
}
