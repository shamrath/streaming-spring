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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static Properties props = null;

    public static Properties loadProperties(boolean originalConfig) throws IOException {
        if (props == null) {
            synchronized (Util.class) {
                if (props == null){
                    props = new Properties();
                    InputStream input;
                    if (originalConfig) {
                        input = Util.class.getClassLoader().getResourceAsStream("config.properties");
                    } else {
                        input = new FileInputStream("/Users/syodage/Projects/streaming-spring/src/main/resources/config.properties");
                    }
                    if (input == null) {
                        log.error("Error! config file doesn't exist");
                        System.exit(-1);
                    } else {
                        props.load(input);
                        log.info("property loaded");
                    }
                }
            }
        }
        return props;
    }

    public static Properties getProperties(){
        return props;
    }

    public static String getProperty(String key){
        if (props == null) {
            return null;
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue){
        if (props == null) {
            return defaultValue;
        }
        return props.getProperty(key, defaultValue);
    }

    public static long getLongProperty(String key, long defaultValue) {
        return Long.parseLong(getProperty(key, String.valueOf(defaultValue)));
    }

    public static long getLongProperty(String key) {
        return Long.parseLong(getProperty(key));
    }

    public static int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
    }

    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static boolean getBoolProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    public static boolean getBoolProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

}
