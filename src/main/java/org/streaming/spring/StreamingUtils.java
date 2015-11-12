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
package org.streaming.spring;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class StreamingUtils {
    private static final String _consumerkey = "6MDYbriKbG5IpxbUPZoGP9c6u";
    private static final String _consumerSecret = "ecVj5Xhupfv9cdVYTAzrP2GctQwmJPTa3LnUkzZOPvQXDrDZvl";
    private static final String _accessToken = "375026574-tsaeYcvrs8qXJfP4CwEFO1q1zHrTusmnuO9F4JjP";
    private static final String _accessTokenSecret = "aK2QnRotM47tpqOTM5WF9PbRplthdGDW2QudmijVbFcuV";

    public static Configuration getTwitterConfig() {
        Configuration config = new ConfigurationBuilder().setOAuthConsumerKey(_consumerkey).setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken).setOAuthAccessTokenSecret(_accessTokenSecret).build();
        return config;
    }


}
