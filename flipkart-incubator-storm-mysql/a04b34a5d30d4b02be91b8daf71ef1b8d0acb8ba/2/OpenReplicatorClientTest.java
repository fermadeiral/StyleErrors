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

import com.flipkart.storm.mysql.schema.DatabaseInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OpenReplicatorClientTest {

    private OpenReplicatorClient    openReplicatorClient;
    private MySqlClient             mySqlClient;
    private ZkClient                zkClient;
    private MySqlConfig             mySqlConfig;
    private ZkBinLogStateConfig     zkBinLogStateConfig;
    private LinkedBlockingQueue<TransactionEvent> mockTxEventQueue;

    @Before
    public void init() {
        mySqlClient = Mockito.mock(MySqlClient.class);
        zkClient = Mockito.mock(ZkClient.class);
        openReplicatorClient = new OpenReplicatorClient(mySqlClient, zkClient);
        mockTxEventQueue = Mockito.mock(LinkedBlockingQueue.class);

        mySqlConfig = new MySqlConfig.Builder("testDatabase")
                                        .user("testUser")
                                        .password("testPass")
                                        .host("localhost")
                                        .port(3306)
                                        .serverId(1)
                                        .binLogFilename("mysql-bin.000001")
                                        .binLogPosition(154)
                                        .includeTables(Sets.newHashSet("tbl1", "tbl2"))
                                        .build();

        zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout")
                                                        .servers(Lists.newArrayList("localhost"))
                                                        .port(2181)
                                                        .root("mysql-binlog-spout")
                                                        .ignoreZkBinLogPosition(false)
                                                        .sessionTimeOutInMs(100)
                                                        .retryTimes(5)
                                                        .connectionTimeOutInMs(100)
                                                        .updateRateInMs(1000)
                                                        .build();
    }

    @Test
    public void test_BeginBinLogPositionWhenZkHasOffsetInfo() throws Exception {
        OffsetInfo offsetInfo = new OffsetInfo(123, "testTopology", "top-123", "testDB", 1432, "mysql-bin.000007");
        when(mySqlClient.getDatabaseSchema(anyString(), anySet())).thenReturn(Mockito.mock(DatabaseInfo.class));
        when(zkClient.read(anyString())).thenReturn(offsetInfo);

        BinLogPosition binLogPosition = this.openReplicatorClient.initialize(mySqlConfig, zkBinLogStateConfig, mockTxEventQueue);

        verify(mySqlClient, times(0)).getBinLogDetails();
        verify(zkClient, times(1)).read(anyString());
        assertEquals(binLogPosition.getBinLogFileName(), "mysql-bin.000007");
        assertEquals(binLogPosition.getBinLogPosition(), 1432);
    }

    @Test
    public void test_BeginBinLogPositionWhenZkDoesNotHaveOffsetInfo() throws Exception {
        when(mySqlClient.getDatabaseSchema(anyString(), anySet())).thenReturn(Mockito.mock(DatabaseInfo.class));
        when(zkClient.read(anyString())).thenReturn(null);

        BinLogPosition binLogPosition = this.openReplicatorClient.initialize(mySqlConfig, zkBinLogStateConfig, mockTxEventQueue);

        verify(mySqlClient, times(0)).getBinLogDetails();
        verify(zkClient, times(1)).read(anyString());
        assertEquals(binLogPosition.getBinLogFileName(), "mysql-bin.000001");
        assertEquals(binLogPosition.getBinLogPosition(), 154);
    }

    @Test
    public void test_BeginBinLogPositionWhenZkDoesNotHaveOffsetInfoAndPositionIsNotProvided() throws Exception {

        mySqlConfig = new MySqlConfig.Builder("testDatabase")
                                        .user("testUser")
                                        .password("testPass")
                                        .host("localhost")
                                        .binLogFilename("mysql-bin.000020")
                                        .build();

        when(mySqlClient.getDatabaseSchema(anyString(), anySet())).thenReturn(Mockito.mock(DatabaseInfo.class));
        when(zkClient.read(anyString())).thenReturn(null);

        BinLogPosition binLogPosition = this.openReplicatorClient.initialize(mySqlConfig, zkBinLogStateConfig, mockTxEventQueue);

        verify(mySqlClient, times(0)).getBinLogDetails();
        verify(zkClient, times(1)).read(anyString());
        assertEquals(binLogPosition.getBinLogFileName(), "mysql-bin.000020");
        assertEquals(binLogPosition.getBinLogPosition(), SpoutConstants.DEFAULT_BINLOGPOSITION);
    }

    @Test
    public void test_BeginBinLogPositionWhenZkDoesNotHaveOffsetInfoAndBinLogDetailsNotProvided() throws Exception {

        mySqlConfig = new MySqlConfig.Builder("testDatabase")
                                        .user("testUser")
                                        .password("testPass")
                                        .host("localhost")
                                        .build();

        when(mySqlClient.getDatabaseSchema(anyString(), anySet())).thenReturn(Mockito.mock(DatabaseInfo.class));
        when(mySqlClient.getBinLogDetails()).thenReturn(new BinLogPosition(7231, "mysql-bin.000077"));
        when(zkClient.read(anyString())).thenReturn(null);

        BinLogPosition binLogPosition = this.openReplicatorClient.initialize(mySqlConfig, zkBinLogStateConfig, mockTxEventQueue);

        verify(mySqlClient, times(1)).getBinLogDetails();
        verify(zkClient, times(1)).read(anyString());
        assertEquals(binLogPosition.getBinLogFileName(), "mysql-bin.000077");
        assertEquals(binLogPosition.getBinLogPosition(), 7231);
    }

    @Test
    public void test_BeginBinLogPositionWhenZKIgnored() throws Exception {

        mySqlConfig = new MySqlConfig.Builder("testDatabase")
                                        .user("testUser")
                                        .password("testPass")
                                        .host("localhost")
                                        .binLogFilename("mysql-bin.000061")
                                        .binLogPosition(11)
                                        .build();

        zkBinLogStateConfig = new ZkBinLogStateConfig.Builder("my-spout")
                                    .ignoreZkBinLogPosition(true)
                                    .build();

        when(mySqlClient.getDatabaseSchema(anyString(), anySet())).thenReturn(Mockito.mock(DatabaseInfo.class));
        when(zkClient.read(anyString())).thenReturn(null);

        BinLogPosition binLogPosition = this.openReplicatorClient.initialize(mySqlConfig, zkBinLogStateConfig, mockTxEventQueue);

        verify(mySqlClient, times(0)).getBinLogDetails();
        verify(zkClient, times(0)).read(anyString());
        assertEquals(binLogPosition.getBinLogFileName(), "mysql-bin.000061");
        assertEquals(binLogPosition.getBinLogPosition(), 11);
    }

}
