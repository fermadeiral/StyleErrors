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

import com.flipkart.storm.mysql.schema.ColumnDataType;
import com.flipkart.storm.mysql.schema.ColumnInfo;
import com.flipkart.storm.mysql.schema.DatabaseInfo;
import com.flipkart.storm.mysql.schema.RowInfo;
import com.flipkart.storm.mysql.schema.TableInfo;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.common.util.MySQLConstants;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class SpoutBinLogEventListenerTest {

    private final static String TEST_DATABASE   = "mydb";
    private final static String TEST_TABLE      = "mytable";

    private static List<MySqlValue> sampleMySqlValues1;
    private static List<MySqlValue> sampleMySqlValues2;
    private static DatabaseInfo     sampleDatabaseInfo;

    private LinkedBlockingQueue<TransactionEvent>   txEventQueue;
    private SpoutBinLogEventListener                testListener;

    @BeforeClass
    public static void initAll() {
        sampleMySqlValues1 = new ArrayList<MySqlValue>();
        sampleMySqlValues1.add(new MySqlValue(ColumnDataType.INT, 1));
        sampleMySqlValues1.add(new MySqlValue(ColumnDataType.VARCHAR, "Emp1"));

        sampleMySqlValues2 = new ArrayList<MySqlValue>();
        sampleMySqlValues2.add(new MySqlValue(ColumnDataType.INT, 2));
        sampleMySqlValues2.add(new MySqlValue(ColumnDataType.VARCHAR, "Emp2"));

        sampleDatabaseInfo = getSampleDatabaseInfo();
    }

    @Before
    public void init() {
        txEventQueue = new LinkedBlockingQueue<TransactionEvent>();
        testListener = new SpoutBinLogEventListener(txEventQueue, sampleDatabaseInfo, "testbinlog");
    }

    @Test
    public void test_generateInsertV2Events() {
        List<BinlogEventV4> insertEventList = BinLogEventGenerator.getEventsForSingleTransaction(TEST_DATABASE,
                TEST_TABLE, 1, MySQLConstants.WRITE_ROWS_EVENT_V2, sampleMySqlValues1);

        raiseEvents(insertEventList);
        checkSingleTransactionEvent(DataEventType.INSERT);
    }

    @Test
    public void test_generateInsertV1Events() {
        List<BinlogEventV4> insertEventList = BinLogEventGenerator.getEventsForSingleTransaction(TEST_DATABASE,
                TEST_TABLE, 1, MySQLConstants.WRITE_ROWS_EVENT, sampleMySqlValues1);

        raiseEvents(insertEventList);
        checkSingleTransactionEvent(DataEventType.INSERT);
    }

    @Test
    public void test_generateDeleteV2Events() {
        List<BinlogEventV4> deleteEventList = BinLogEventGenerator.getEventsForSingleTransaction(TEST_DATABASE,
                TEST_TABLE, 1, MySQLConstants.DELETE_ROWS_EVENT_V2, sampleMySqlValues1);

        raiseEvents(deleteEventList);
        checkSingleTransactionEvent(DataEventType.DELETE);
    }

    @Test
    public void test_generateDeleteV1Events() {
        List<BinlogEventV4> deleteEventList = BinLogEventGenerator.getEventsForSingleTransaction(TEST_DATABASE,
                TEST_TABLE, 1, MySQLConstants.DELETE_ROWS_EVENT, sampleMySqlValues1);

        raiseEvents(deleteEventList);
        checkSingleTransactionEvent(DataEventType.DELETE);
    }

    @Test
    public void test_generateUpdateV2Events() {
        List<BinlogEventV4> updateEventList = BinLogEventGenerator.getEventsForUpdateTransaction(TEST_DATABASE,
                TEST_TABLE, 1, MySQLConstants.UPDATE_ROWS_EVENT_V2, sampleMySqlValues1, sampleMySqlValues2);
        raiseEvents(updateEventList);
        checkUpdateEvent();
    }

    @Test
    public void test_generateUpdateV1Events() {
        List<BinlogEventV4> updateEventList = BinLogEventGenerator.getEventsForUpdateTransaction(TEST_DATABASE,
                TEST_TABLE, 1, MySQLConstants.UPDATE_ROWS_EVENT, sampleMySqlValues1, sampleMySqlValues2);
        raiseEvents(updateEventList);
        checkUpdateEvent();
    }

    private static DatabaseInfo getSampleDatabaseInfo() {
        List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
        columnInfoList.add(new ColumnInfo("id", 0, "int"));
        columnInfoList.add(new ColumnInfo("name", 1, "varchar"));

        RowInfo rowInfo = new RowInfo(columnInfoList);
        TableInfo tableInfo = new TableInfo(TEST_TABLE, rowInfo);

        Map<String, TableInfo> tableInfoMap = new HashMap<String, TableInfo>();
        tableInfoMap.put(TEST_TABLE, tableInfo);
        return new DatabaseInfo(TEST_DATABASE, tableInfoMap);
    }

    private void checkSingleTransactionEvent(DataEventType expectedEventType) {
        assertEquals(txEventQueue.size(), 1);

        TransactionEvent transactionEvent = txEventQueue.poll();
        assertEquals(transactionEvent.getDatabaseName(), TEST_DATABASE);
        assertEquals(transactionEvent.getTransactionState(), TransactionState.END);
        assertEquals(transactionEvent.getDataEvents().size(), 1);

        DataEvent dataEvent = transactionEvent.getDataEvents().get(0);
        assertEquals(dataEvent.getOldData(), null);
        assertEquals(dataEvent.getDataEventType(), expectedEventType);
        assertEquals(dataEvent.getTableName(), TEST_TABLE);

        List<Map<String, Object>> dataEventList = dataEvent.getData();
        assertEquals(dataEventList.size(), 1);
        Map<String, Object> actualData = dataEventList.get(0);
        assertEquals(((Integer) actualData.get("id")).intValue(), 1);
        assertEquals(actualData.get("name"), "Emp1");
    }

    private void checkUpdateEvent() {
        assertEquals(txEventQueue.size(), 1);

        TransactionEvent transactionEvent = txEventQueue.poll();
        assertEquals(transactionEvent.getDatabaseName(), TEST_DATABASE);
        assertEquals(transactionEvent.getTransactionState(), TransactionState.END);
        assertEquals(transactionEvent.getDataEvents().size(), 1);

        DataEvent dataEvent = transactionEvent.getDataEvents().get(0);
        assertNotEquals(dataEvent.getOldData(), null);
        assertEquals(dataEvent.getDataEventType(), DataEventType.UPDATE);
        assertEquals(dataEvent.getTableName(), TEST_TABLE);

        List<Map<String, Object>> oldDataEventList = dataEvent.getOldData();
        assertEquals(oldDataEventList.size(), 1);
        Map<String, Object> actualData1 = oldDataEventList.get(0);
        assertEquals(((Integer) actualData1.get("id")).intValue(), 1);
        assertEquals(actualData1.get("name"), "Emp1");

        List<Map<String, Object>> newDataEventList = dataEvent.getData();
        assertEquals(newDataEventList.size(), 1);
        Map<String, Object> actualData2 = newDataEventList.get(0);
        assertEquals(((Integer) actualData2.get("id")).intValue(), 2);
        assertEquals(actualData2.get("name"), "Emp2");
    }

    private void raiseEvents(List<BinlogEventV4> eventList) {
        for (BinlogEventV4 event : eventList) {
            testListener.onEvents(event);
        }
    }
}
