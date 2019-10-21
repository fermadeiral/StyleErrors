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
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ZkClientTest {

    private TestingServer testingServer;
    private ZkBinLogStateConfig zkBinLogStateConfig;
    private ZkClient zkClient;

    @Before
    public void init() throws Exception {
        testingServer = new TestingServer();

        String testConnectString = testingServer.getConnectString();
        String testHost = testConnectString.split(":")[0];
        int testPort = testingServer.getPort();

        zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-test-spout")
                                                        .servers(Lists.newArrayList(testHost))
                                                        .port(testPort)
                                                        .sessionTimeOutInMs(10000)
                                                        .retryTimes(5)
                                                        .sleepMsBetweenRetries(50)
                                                        .connectionTimeOutInMs(10000)
                                                        .build();

        ZkConf zkConf = new ZkConf(new HashMap<String, Object>(), zkBinLogStateConfig);
        zkClient = new ZkClient(zkConf);
        zkClient.start();
    }

    @After
    public void tearDown() throws Exception {
        zkClient.close();
        testingServer.close();
    }

    @Test
    public void test_readOffsetInfo() throws Exception {
        OffsetInfo offsetInfo = new OffsetInfo(12345, "topName", "topInstanceId", "testDb", 777, "testBinLogFileName");
        zkClient.write(zkBinLogStateConfig.getZkScnCommitPath(), offsetInfo);
        OffsetInfo retrievedFromZk = zkClient.read(zkBinLogStateConfig.getZkScnCommitPath());
        assertEquals(offsetInfo, retrievedFromZk);
    }

    @Test
    public void test_readOffsetInfoAfterTwoUpdates() throws Exception {
        OffsetInfo offsetInfo1 = new OffsetInfo(12345, "topName", "topInstanceId", "testDb", 777, "testBinLogFileName");
        zkClient.write(zkBinLogStateConfig.getZkScnCommitPath(), offsetInfo1);
        OffsetInfo retrievedFromZk1 = zkClient.read(zkBinLogStateConfig.getZkScnCommitPath());
        assertEquals(offsetInfo1, retrievedFromZk1);
        OffsetInfo offsetInfo2 = new OffsetInfo(12346, "topName", "topInstanceId", "testDb", 778, "testBinLogFileName");
        zkClient.write(zkBinLogStateConfig.getZkScnCommitPath(), offsetInfo2);
        OffsetInfo retrievedFromZk2 = zkClient.read(zkBinLogStateConfig.getZkScnCommitPath());
        assertEquals(offsetInfo2, retrievedFromZk2);
    }

    @Test
    public void test_readWhenPathDoesNotExist() throws Exception {
        OffsetInfo retrievedFromZk = zkClient.read(zkBinLogStateConfig.getZkScnCommitPath());
        assertEquals(retrievedFromZk, null);
    }

}
