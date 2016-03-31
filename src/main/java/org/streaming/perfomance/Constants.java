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

public interface Constants {

    String KAFKA_PUBLISHER = "-kp";
    String KAFKA_CONSUMER = "-kc";
    String RABBITMQ_PUBLISHER = "-rp";
    String RABBITMQ_CONSUMER = "-rc";

    String PUBLISHER_MESSAGE_DELAY = "publisher.message.delay";
    String ENBALE_DEBUG = "enable.debug";
    String KAFKA_TOPIC = "kafka.topic";
    String KAFKA_TOPIC_KEY = "kafka.topic.key";
    String KAFKA_PARTITION_COUNT = "partition.count";


}
