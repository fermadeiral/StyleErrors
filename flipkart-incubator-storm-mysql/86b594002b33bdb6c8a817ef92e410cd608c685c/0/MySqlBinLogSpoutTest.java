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

import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MySqlBinLogSpoutTest {

    private MySqlConfig             mySqlConfig;
    private ZkBinLogStateConfig     zkBinLogStateConfig;
    private MySqlSpoutConfig        mySqlSpoutConfig;
    private Map<String, Object>     mockStormConfig;
    private TopologyContext         mockTopologyContext;
    private SpoutOutputCollector    mockSpoutOutputCollector;
    private LinkedBlockingQueue     internalBuffer;

    private MySqlClient             mockMySqlClient;
    private ZkClient                mockZkClient;
    private OpenReplicatorClient    mockOpenReplicatorClient;
    private ClientFactory           mockClientFactory;

    @Before
    public void init() {

        mySqlConfig = new MySqlConfig.Builder("testDatabase").build();
        zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout").build();
        mySqlSpoutConfig = new MySqlSpoutConfig(mySqlConfig, zkBinLogStateConfig);
        mockStormConfig = Mockito.mock(Map.class);
        mockTopologyContext = Mockito.mock(TopologyContext.class);
        mockSpoutOutputCollector = Mockito.mock(SpoutOutputCollector.class);
        internalBuffer = new LinkedBlockingQueue();

        mockMySqlClient = Mockito.mock(MySqlClient.class);
        mockZkClient = Mockito.mock(ZkClient.class);
        mockOpenReplicatorClient = Mockito.mock(OpenReplicatorClient.class);
        mockClientFactory = Mockito.mock(ClientFactory.class);
    }

    @Test(expected = NullPointerException.class)
    public void test_ExceptionWhenNoMySqlConfig() {
        mySqlSpoutConfig = new MySqlSpoutConfig(null, zkBinLogStateConfig);
        MySqlBinLogSpout spout = new MySqlBinLogSpout(mySqlSpoutConfig);
        spout.open(mockStormConfig, mockTopologyContext, mockSpoutOutputCollector);
    }

    @Test(expected = NullPointerException.class)
    public void test_ExceptionWhenNoZkConfig() {
        mySqlSpoutConfig = new MySqlSpoutConfig(mySqlConfig, null);
        MySqlBinLogSpout spout = new MySqlBinLogSpout(mySqlSpoutConfig, mockClientFactory);
        spout.open(mockStormConfig, mockTopologyContext, mockSpoutOutputCollector);
    }

    @Test
    public void test_OpenAndLastEmittedBeginTxPosition() {

        MySqlBinLogSpout spout = new MySqlBinLogSpout(mySqlSpoutConfig, mockClientFactory);

        when(mockStormConfig.get(Config.TOPOLOGY_NAME)).thenReturn("test-top");
        when(mockClientFactory.getMySqlClient(mySqlConfig)).thenReturn(mockMySqlClient);
        when(mockClientFactory.getZkClient(mockStormConfig, zkBinLogStateConfig)).thenReturn(mockZkClient);
        when(mockClientFactory.getReplicatorClient(mockMySqlClient, mockZkClient)).thenReturn(mockOpenReplicatorClient);
        when(mockClientFactory.initializeBuffer(any(Integer.class))).thenReturn(internalBuffer);
        when(mockOpenReplicatorClient.initialize(mySqlConfig, zkBinLogStateConfig, internalBuffer))
                                     .thenReturn(new BinLogPosition(1121, "mysql-bin2"));

        spout.open(mockStormConfig, mockTopologyContext, mockSpoutOutputCollector);
        assertEquals(spout.lastEmittedBeginTxPosition.getBinLogPosition(), 1121);
        assertEquals(spout.lastEmittedBeginTxPosition.getBinLogFileName(), "mysql-bin2");
    }

}
