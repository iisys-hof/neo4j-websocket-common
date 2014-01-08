/*
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
 */
package de.hofuniversity.iisys.neo4j.websock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.result.AResultSet;
import de.hofuniversity.iisys.neo4j.websock.result.EResultType;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.result.TableResult;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Test for the converter transforming transferable maps and lists into more
 * convenient result sets and vice versa.
 */
public class ResultSetConverterTest
{
    private static final Integer TEST_INT_1 = 123;
    private static final Integer TEST_INT_2 = 321;
    private static final String[] TEST_STRING_ARRAY = {"test", "string",
        "array"};

    /**
     * Test for the conversion from a generic map into a usable result set.
     */
    @Test
    public void fromMapTest()
    {
        //single
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(WebsockConstants.RESULT_TYPE, EResultType.SINGLE.getCode());

        Map<String, Object> resMap = new HashMap<String, Object>();
        map.put(WebsockConstants.RESULT, resMap);

        AResultSet<?> set = ResultSetConverter.toResultSet(map);
        Assert.assertTrue(set instanceof SingleResult);

        Assert.assertEquals(0, set.getFirst());
        Assert.assertEquals(-1, set.getMax());
        Assert.assertEquals(1, set.getSize());
        Assert.assertEquals(-1, set.getTotal());
        Assert.assertEquals(EResultType.SINGLE, set.getType());

        //list
        map = new HashMap<String, Object>();

        map.put(WebsockConstants.SUBSET_START, TEST_INT_1);
        map.put(WebsockConstants.SUBSET_SIZE, TEST_INT_2);
        map.put(WebsockConstants.TOTAL_RESULTS, TEST_INT_1
            + TEST_INT_2);

        List<Map<String, Object>> resList =
            new ArrayList<Map<String, Object>>();
        resList.add(new HashMap<String, Object>());
        resList.add(new HashMap<String, Object>());
        resList.add(new HashMap<String, Object>());
        map.put(WebsockConstants.RESULT, resList);

        map.put(WebsockConstants.RESULT_TYPE, EResultType.LIST.getCode());

        set = ResultSetConverter.toResultSet(map);
        Assert.assertTrue(set instanceof ListResult);

        Assert.assertEquals((int)TEST_INT_1, set.getFirst());
        Assert.assertEquals((int)TEST_INT_2, set.getMax());
        Assert.assertEquals(3, set.getSize());
        Assert.assertEquals(TEST_INT_1 + TEST_INT_2, set.getTotal());
        Assert.assertEquals(EResultType.LIST, set.getType());

        //table
        map = new HashMap<String, Object>();
        map.put(WebsockConstants.RESULT_TYPE, EResultType.TABLE.getCode());

        List<String> columns = new ArrayList<String>();
        columns.add(TEST_STRING_ARRAY[0]);
        columns.add(TEST_STRING_ARRAY[1]);
        columns.add(TEST_STRING_ARRAY[2]);
        map.put(WebsockConstants.TABLE_COLUMNS, columns);

        List<List<?>> resLists = new ArrayList<List<?>>();
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        map.put(WebsockConstants.RESULT, resLists);

        set = ResultSetConverter.toResultSet(map);
        Assert.assertTrue(set instanceof TableResult);

        Assert.assertEquals(4, set.getSize());
        Assert.assertEquals(EResultType.TABLE, set.getType());
        Assert.assertTrue(((TableResult)set).getColumns() == columns);
        Assert.assertEquals(2, ((TableResult)set).getColumnIndex(
            TEST_STRING_ARRAY[2]));
    }

    /**
     * Test for the conversion of a result set into a transferable map.
     */
    @Test
    public void fromSetTest()
    {
        //single
        Map<String, Object> resMap = new HashMap<String, Object>();
        AResultSet<?> set = new SingleResult(resMap);

        Map<String, Object> map = ResultSetConverter.toMap(set,
            new HashMap<String, Object>());

        Assert.assertEquals(EResultType.SINGLE.getCode(),
            map.get(WebsockConstants.RESULT_TYPE));
        Assert.assertEquals(resMap, map.get(WebsockConstants.RESULT));

        //list
        List<Map<String, Object>> resList =
            new ArrayList<Map<String, Object>>();

        resList.add(new HashMap<String, Object>());
        resList.add(new HashMap<String, Object>());
        resList.add(new HashMap<String, Object>());

        set = new ListResult(resList);

        set.setFirst(TEST_INT_1);
        set.setMax(TEST_INT_2);
        set.setTotal(TEST_INT_1 + TEST_INT_2);

        map = ResultSetConverter.toMap(set,
            new HashMap<String, Object>());

        Assert.assertEquals(EResultType.LIST.getCode(),
            map.get(WebsockConstants.RESULT_TYPE));

        Assert.assertEquals(TEST_INT_1,
            map.get(WebsockConstants.SUBSET_START));
        Assert.assertEquals(TEST_INT_2,
            map.get(WebsockConstants.SUBSET_SIZE));
        Assert.assertEquals(TEST_INT_1 + TEST_INT_2,
            map.get(WebsockConstants.TOTAL_RESULTS));

        Assert.assertEquals(resList, map.get(WebsockConstants.RESULT));

        //table
        List<String> columns = new ArrayList<String>();
        List<List<Object>> resLists = new ArrayList<List<Object>>();
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        set = new TableResult(columns, resLists);


        map = ResultSetConverter.toMap(set,
            new HashMap<String, Object>());

        Assert.assertEquals(EResultType.TABLE.getCode(),
            map.get(WebsockConstants.RESULT_TYPE));
        Assert.assertEquals(resLists, map.get(WebsockConstants.RESULT));
        Assert.assertEquals(columns, map.get(WebsockConstants.TABLE_COLUMNS));
    }

    /**
     * Test for the full conversion from set to map and back.
     */
    @Test
    public void selfTest()
    {
        //single
        Map<String, Object> resMap = new HashMap<String, Object>();
        AResultSet<?> set = new SingleResult(resMap);

        Map<String, Object> map = ResultSetConverter.toMap(set,
            new HashMap<String, Object>());
        set = ResultSetConverter.toResultSet(map);

        Assert.assertTrue(set instanceof SingleResult);
        Assert.assertEquals(EResultType.SINGLE, set.getType());
        Assert.assertTrue(resMap == set.getResults());

        //list
        List<Map<String, Object>> resList =
            new ArrayList<Map<String, Object>>();

        resList.add(new HashMap<String, Object>());
        resList.add(new HashMap<String, Object>());
        resList.add(new HashMap<String, Object>());

        set = new ListResult(resList);

        set.setFirst(TEST_INT_1);
        set.setMax(TEST_INT_2);
        set.setTotal(TEST_INT_1 + TEST_INT_2);

        map = ResultSetConverter.toMap(set,
            new HashMap<String, Object>());
        set = ResultSetConverter.toResultSet(map);

        Assert.assertTrue(set instanceof ListResult);

        Assert.assertEquals((int)TEST_INT_1, set.getFirst());
        Assert.assertEquals((int)TEST_INT_2, set.getMax());
        Assert.assertEquals(3, set.getSize());
        Assert.assertEquals(TEST_INT_1 + TEST_INT_2, set.getTotal());
        Assert.assertEquals(EResultType.LIST, set.getType());
        Assert.assertTrue(resList == set.getResults());

        //table
        List<String> columns = new ArrayList<String>();
        columns.add(TEST_STRING_ARRAY[0]);
        columns.add(TEST_STRING_ARRAY[1]);
        columns.add(TEST_STRING_ARRAY[2]);
        List<List<Object>> resLists = new ArrayList<List<Object>>();
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        resLists.add(new ArrayList<Object>());
        set = new TableResult(columns, resLists);


        map = ResultSetConverter.toMap(set,
            new HashMap<String, Object>());
        set = ResultSetConverter.toResultSet(map);

        Assert.assertTrue(set instanceof TableResult);

        Assert.assertEquals(4, set.getSize());
        Assert.assertEquals(EResultType.TABLE, set.getType());
        Assert.assertTrue(((TableResult)set).getColumns() == columns);
        Assert.assertEquals(2, ((TableResult)set).getColumnIndex(
            TEST_STRING_ARRAY[2]));
        Assert.assertTrue(resLists == set.getResults());
    }
}
