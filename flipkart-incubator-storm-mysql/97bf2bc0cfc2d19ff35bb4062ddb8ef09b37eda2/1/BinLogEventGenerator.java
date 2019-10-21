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
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.BinlogEventV4HeaderImpl;
import com.google.code.or.binlog.impl.event.DeleteRowsEvent;
import com.google.code.or.binlog.impl.event.DeleteRowsEventV2;
import com.google.code.or.binlog.impl.event.QueryEvent;
import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.binlog.impl.event.WriteRowsEvent;
import com.google.code.or.binlog.impl.event.WriteRowsEventV2;
import com.google.code.or.binlog.impl.event.XidEvent;
import com.google.code.or.common.glossary.Column;
import com.google.code.or.common.glossary.Row;
import com.google.code.or.common.glossary.column.Int24Column;
import com.google.code.or.common.glossary.column.StringColumn;
import com.google.code.or.common.util.MySQLConstants;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * This generator takes care of generating mock BinLog Events as done by Open Replicator.
 */
public class BinLogEventGenerator {

    public static List<BinlogEventV4> getSingleActionResult(String dbName, String tableName, long tableId,
                                  int eventType, List<MySqlValue> valueList) {

        Preconditions.checkArgument(validate(eventType),
                "Incorrect Usage, Event must a supported DML(Delete/Write/Update) type");

        List<BinlogEventV4> eventV4List = new ArrayList<BinlogEventV4>();
        eventV4List.add(formBinLogQueryEvent(dbName));
        eventV4List.add(formBinLogTableMapEvent(dbName, tableName, tableId));
        eventV4List.add(formDataEvent(tableId, eventType, valueList));
        eventV4List.add(formXidEvent(1234));
        return eventV4List;
    }

    /**
     * This creates the Data Event based on the parameters passed.
     */
    private static BinlogEventV4 formDataEvent(long tableId, int eventType, List<MySqlValue> valueList) {
        //Third Event is the actual DML event
        //Currently only write and update. Do Update later.
        BinlogEventV4HeaderImpl eventV4Header = new BinlogEventV4HeaderImpl();
        eventV4Header.setTimestamp(System.currentTimeMillis());
        eventV4Header.setEventType(eventType);
        eventV4Header.setServerId(1);
        if (eventType == MySQLConstants.WRITE_ROWS_EVENT_V2) {
            WriteRowsEventV2 writeRowsEventV2 = new WriteRowsEventV2(eventV4Header);
            writeRowsEventV2.setTableId(tableId);
            writeRowsEventV2.setRows(getRows(valueList));
            return writeRowsEventV2;
        } else if (eventType == MySQLConstants.WRITE_ROWS_EVENT) {
            WriteRowsEvent writeRowsEvent = new WriteRowsEvent(eventV4Header);
            writeRowsEvent.setTableId(tableId);
            writeRowsEvent.setRows(getRows(valueList));
            return writeRowsEvent;
        } else if (eventType == MySQLConstants.DELETE_ROWS_EVENT_V2) {
            DeleteRowsEventV2 deleteRowsEventV2 = new DeleteRowsEventV2(eventV4Header);
            deleteRowsEventV2.setTableId(tableId);
            deleteRowsEventV2.setRows(getRows(valueList));
            return deleteRowsEventV2;
        } else if (eventType == MySQLConstants.DELETE_ROWS_EVENT) {
            DeleteRowsEvent deleteRowsEvent = new DeleteRowsEvent(eventV4Header);
            deleteRowsEvent.setTableId(tableId);
            deleteRowsEvent.setRows(getRows(valueList));
            return deleteRowsEvent;
        }
        return null;
    }

    /**
     * The final event in the case InnoDB Transactions is the Xid Event.
     * For MyISAM the final event would be a Query Event with a "Commit"/ "Rollback" Sql
     */
    private static BinlogEventV4 formXidEvent(int txId) {

        BinlogEventV4HeaderImpl xidEventV4Header = new BinlogEventV4HeaderImpl();
        xidEventV4Header.setTimestamp(System.currentTimeMillis());
        xidEventV4Header.setEventType(MySQLConstants.XID_EVENT);
        xidEventV4Header.setServerId(1);
        XidEvent xidEvent = new XidEvent(xidEventV4Header);
        xidEvent.setXid(txId);
        return xidEvent;
    }

    /**
     * The second bin log event of a transaction is always a Table Map Event
     */
    private static BinlogEventV4 formBinLogTableMapEvent(String dbName, String tableName, long tableId) {
        //Second event is always a Table_Map_Event
        BinlogEventV4HeaderImpl tableMapEventHeader = new BinlogEventV4HeaderImpl();
        tableMapEventHeader.setTimestamp(System.currentTimeMillis());
        tableMapEventHeader.setEventType(MySQLConstants.TABLE_MAP_EVENT);
        tableMapEventHeader.setServerId(1);
        TableMapEvent tableMapEvent = new TableMapEvent(tableMapEventHeader);
        tableMapEvent.setTableId(tableId);
        tableMapEvent.setDatabaseNameLength(dbName.length());
        tableMapEvent.setDatabaseName(StringColumn.valueOf(dbName.getBytes()));
        tableMapEvent.setTableNameLength(tableName.length());
        tableMapEvent.setTableName(StringColumn.valueOf(tableName.getBytes()));
        return tableMapEvent;
    }

    /**
     * The first bin log event of a transaction is always a Query Event.
    */
    private static BinlogEventV4 formBinLogQueryEvent(String dbName) {

        BinlogEventV4HeaderImpl queryEventHeader = new BinlogEventV4HeaderImpl();
        queryEventHeader.setTimestamp(System.currentTimeMillis());
        queryEventHeader.setEventType(MySQLConstants.QUERY_EVENT);
        queryEventHeader.setServerId(1);
        QueryEvent queryEvent = new QueryEvent(queryEventHeader);
        queryEvent.setDatabaseNameLength(dbName.length());
        queryEvent.setDatabaseName(StringColumn.valueOf(dbName.getBytes()));
        queryEvent.setSql(StringColumn.valueOf("BEGIN".getBytes()));
        return queryEvent;
    }

    private static List<Row> getRows(List<MySqlValue> valueList) {
        List<Row> rows = new ArrayList<Row>();
        Row singleRow = new Row();
        List<Column> columns = new ArrayList<Column>();
        for (MySqlValue value : valueList) {
            if (value.getColumnDataType() == ColumnDataType.VARCHAR) {
                String s = (String)value.getValue();
                columns.add(StringColumn.valueOf(s.getBytes()));
            } else if (value.getColumnDataType() == ColumnDataType.INT) {
                int i = (Integer)value.getValue();
                columns.add(Int24Column.valueOf(i));
            } else {
                throw new RuntimeException("This type is not supported.." + value.getColumnDataType() );
            }
        }
        singleRow.setColumns(columns);
        rows.add(singleRow);
        return rows;
    }

    private static boolean validate(int eventType) {
        if (!((eventType == MySQLConstants.DELETE_ROWS_EVENT) ||
                (eventType == MySQLConstants.DELETE_ROWS_EVENT_V2) ||
                (eventType == MySQLConstants.WRITE_ROWS_EVENT) ||
                (eventType == MySQLConstants.WRITE_ROWS_EVENT_V2) ||
                (eventType == MySQLConstants.UPDATE_ROWS_EVENT) ||
                (eventType == MySQLConstants.UPDATE_ROWS_EVENT_V2))) {
            return false;
        }
        return true;
    }
}
