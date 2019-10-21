///**
// * Copyright 2016 Flipkart Internet Pvt. Ltd.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.flipkart.storm.mysql;
//
//import com.google.common.collect.Lists;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//
//import java.sql.DatabaseMetaData;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//public class MySqlClientTest {
//
//    private MySqlClient             mySqlClient;
//    private MySqlConnectionFactory  mockConnectionFactory;
//    private DatabaseMetaData        mockDatabaseMetaData;
//    private ResultSet               mockResultSet;
//
//    private ResultSet               mockResultSet1;
//    private ResultSet               mockResultSet2;
//    private ResultSet               mockResultSet3;
//    private PreparedStatement       preparedStatement1;
//    private PreparedStatement       preparedStatement2;
//    private PreparedStatement       preparedStatement3;
//
//    private static final String GET_MYSQL_TABLE_SCHEMA1 =
//            "SELECT * FROM information_schema.columns WHERE table_schema = 'testDb' and table_name = 'tbl1'";
//    private static final String GET_MYSQL_TABLE_SCHEMA2 =
//            "SELECT * FROM information_schema.columns WHERE table_schema = 'testDb' and table_name = 'tbl2'";
//    private static final String GET_MYSQL_TABLE_SCHEMA3 =
//            "SELECT * FROM information_schema.columns WHERE table_schema = 'testDb' and table_name = 'tbl3'";
//
//
//    @Before
//    public void init() throws Exception {
//        mockConnectionFactory = Mockito.mock(MySqlConnectionFactory.class);
//        mySqlClient = new MySqlClient(mockConnectionFactory);
//        mockDatabaseMetaData = Mockito.mock(DatabaseMetaData.class);
//        mockResultSet = Mockito.mock(ResultSet.class);
//
//        when(mockConnectionFactory.getConnection().getMetaData()).thenReturn(mockDatabaseMetaData);
//        when(mockDatabaseMetaData.getTables(null, null, null, new String[] {"TABLE"})).thenReturn(mockResultSet);
//        //Return three tables
//        when(mockResultSet.next()).thenReturn(true, true, true, false);
//        doAnswer(new Answer() {
//            private int count = 0;
//            private List<String> tableList = Lists.newArrayList("tbl1", "tbl2", "tbl3");
//
//            public Object answer(InvocationOnMock invocation) {
//                return tableList.get(count++);
//            }
//        }).when(mockResultSet.getString("TABLE_NAME"));
//
//        when(mockConnectionFactory.getConnection().prepareStatement(GET_MYSQL_TABLE_SCHEMA1)).thenReturn(preparedStatement1);
//        when(mockConnectionFactory.getConnection().prepareStatement(GET_MYSQL_TABLE_SCHEMA2)).thenReturn(preparedStatement2);
//        when(mockConnectionFactory.getConnection().prepareStatement(GET_MYSQL_TABLE_SCHEMA3)).thenReturn(preparedStatement3);
//        when(preparedStatement1.executeQuery()).thenReturn(mockResultSet1);
//        when(preparedStatement2.executeQuery()).thenReturn(mockResultSet2);
//        when(preparedStatement3.executeQuery()).thenReturn(mockResultSet3);
//    }
//
//    @Test
//    public void test_getDatabaseSchema() throws Exception {
//
//    }
//}
