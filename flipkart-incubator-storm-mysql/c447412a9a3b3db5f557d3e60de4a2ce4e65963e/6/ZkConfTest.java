/**
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.storm.mysql;

import com.google.common.collect.Lists;
import org.apache.storm.Config;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ZkConfTest {

    private Map<String, Object> stormConf;

    @Before
    public void init() {

        stormConf = new HashMap<String, Object>();
        stormConf.put(Config.STORM_ZOOKEEPER_SERVERS, Lists.newArrayList("myhost"));
        stormConf.put(Config.STORM_ZOOKEEPER_PORT, 1234);
        stormConf.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 10);
        stormConf.put(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT, 100);
        stormConf.put(Config.STORM_ZOOKEEPER_RETRY_TIMES, 5);
        stormConf.put(Config.STORM_ZOOKEEPER_RETRY_INTERVAL, 20);
    }

    @Test
    public void test_UserZkConfNotProvided() {
        ZkBinLogStateConfig zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout").build();
        ZkConf zkConf = new ZkConf(stormConf, zkBinLogStateConfig);
        assertEquals(zkConf.getZkServers(), Lists.newArrayList("myhost"));
        assertEquals(zkConf.getZkPort(), (Integer)1234);
        assertEquals(zkConf.getZkSessionTimeout(), (Integer)10);
        assertEquals(zkConf.getZkConnectionTimeout(), (Integer)100);
        assertEquals(zkConf.getRetryTimes(), (Integer)5);
        assertEquals(zkConf.getSleepMsBetweenRetries(), (Integer)20);
    }

    @Test
    public void test_UserZkConfProvided() {
        ZkBinLogStateConfig zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout")
                                                    .servers(Lists.newArrayList("localhost"))
                                                    .port(2181)
                                                    .sessionTimeOutInMs(100)
                                                    .retryTimes(7)
                                                    .sleepMsBetweenRetries(200)
                                                    .connectionTimeOutInMs(50)
                                                    .build();
        ZkConf zkConf = new ZkConf(stormConf, zkBinLogStateConfig);
        assertEquals(zkConf.getZkServers(), Lists.newArrayList("localhost"));
        assertEquals(zkConf.getZkPort(), (Integer)2181);
        assertEquals(zkConf.getZkSessionTimeout(), (Integer)100);
        assertEquals(zkConf.getZkConnectionTimeout(), (Integer)50);
        assertEquals(zkConf.getRetryTimes(), (Integer)7);
        assertEquals(zkConf.getSleepMsBetweenRetries(), (Integer)200);
    }

    @Test
    public void test_MixedConfig() {
        ZkBinLogStateConfig zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout")
                                                    .port(2181)
                                                    .sessionTimeOutInMs(100)
                                                    .connectionTimeOutInMs(50)
                                                    .build();
        ZkConf zkConf = new ZkConf(stormConf, zkBinLogStateConfig);
        assertEquals(zkConf.getZkServers(), Lists.newArrayList("myhost"));
        assertEquals(zkConf.getZkPort(), (Integer)2181);
        assertEquals(zkConf.getZkSessionTimeout(), (Integer)100);
        assertEquals(zkConf.getZkConnectionTimeout(), (Integer)50);
        assertEquals(zkConf.getRetryTimes(), (Integer)5);
        assertEquals(zkConf.getSleepMsBetweenRetries(), (Integer)20);
    }
}
