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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.junit.Assert;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Test for the converter transforming websocket queries into BSON and vice
 * versa.
 */
public class BsonConverterTest
{
    private static final int TEST_ID_1 = 42;

    private static final EQueryType TEST_TYPE_1 = EQueryType.PING;

    private static final String TEST_STRING_1 = "aGweoF12 3p0ß45#äö ÄÖÜ;";
    private static final Integer TEST_INT_1 = 123;
    private static final Long TEST_LONG_1 = 456L;
    private static final Boolean TEST_BOOL = true;
    private static final byte[] TEST_BYTE_ARRAY = {1, 2, 3, 4, 5, 6};

    private static final String TEST_STRING_1_NAME = "string 1";
    private static final String TEST_BOOL_NAME = "flag x";
    private static final String TEST_BYTE_ARRAY_NAME = "byte array";

    private static final String TEST_LIST_1_NAME = "list 1";
    private static final String TEST_LIST_2_NAME = "list 2";

    /**
     * Tests conversion of generic maps, lists and primitive values into BSON.
     */
    @Test
    public void testGenericEncode()
    {
        WebsockQuery query = new WebsockQuery(TEST_ID_1, TEST_TYPE_1);

        //parameters
        Map<String, Object> testParamMap = new HashMap<String, Object>();

        List<Object> testParamList = new ArrayList<Object>();
        testParamList.add(TEST_INT_1);
        testParamList.add(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        query.setParameters(testParamMap);

        //payload
        Map<String, Object> testPayloadMap = new HashMap<String, Object>();

        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        List<Object> testList2 = new LinkedList<Object>();
        testList2.add(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        query.setPayload(testPayloadMap);

        //conversion and check
        BSONObject result = BsonConverter.toBson(query);
        Assert.assertEquals(TEST_ID_1, result.get(WebsockConstants.QUERY_ID));
        Assert.assertEquals(TEST_TYPE_1.getCode(),
            result.get(WebsockConstants.QUERY_TYPE));

        //parameters
        BSONObject parameters = (BSONObject) result.get(
            WebsockConstants.PARAMETERS);

        Assert.assertEquals(TEST_STRING_1, parameters.get(TEST_STRING_1_NAME));
        Assert.assertEquals(TEST_BOOL, parameters.get(TEST_BOOL_NAME));

        BasicBSONList list = (BasicBSONList) parameters.get(TEST_LIST_1_NAME);

        Assert.assertEquals(TEST_INT_1, list.get(0));
        Assert.assertEquals(TEST_LONG_1, list.get(1));

        //payload
        BSONObject payload = (BSONObject) result.get(WebsockConstants.PAYLOAD);

        BasicBSONList pList = (BasicBSONList) payload.get(TEST_LIST_2_NAME);
        BSONObject pMap = (BSONObject) pList.get(0);
        Assert.assertArrayEquals(TEST_BYTE_ARRAY,
            (byte[]) pMap.get(TEST_BYTE_ARRAY_NAME));

        Assert.assertEquals(TEST_STRING_1, payload.get(TEST_STRING_1_NAME));
    }

    /**
     * Tests the pass-through conversion of maps and lists that are already in
     * the right BSON format.
     */
    @Test
    public void testBsonEncode()
    {
        WebsockQuery query = new WebsockQuery(TEST_ID_1, TEST_TYPE_1);

        //parameters
        BasicBSONObject testParamMap = new BasicBSONObject();

        List<Object> testParamList = new BasicBSONList();
        testParamList.add(TEST_INT_1);
        testParamList.add(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        query.setParameters(testParamMap);

        //payload
        BasicBSONObject testPayloadMap = new BasicBSONObject();

        Map<String, Object> testMap = new BasicBSONObject();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        List<Object> testList2 = new BasicBSONList();
        testList2.add(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        query.setPayload(testPayloadMap);

        //conversion and check
        BSONObject result = BsonConverter.toBson(query);
        Assert.assertEquals(TEST_ID_1, result.get(WebsockConstants.QUERY_ID));
        Assert.assertEquals(TEST_TYPE_1.getCode(),
            result.get(WebsockConstants.QUERY_TYPE));

        //parameters
        BSONObject parameters = (BSONObject) result.get(
            WebsockConstants.PARAMETERS);
        Assert.assertTrue(parameters == testParamMap);

        BasicBSONList listParam = (BasicBSONList) parameters.get(
            TEST_LIST_1_NAME);
        Assert.assertTrue(listParam == testParamList);

        //payload
        BSONObject payload = (BSONObject) result.get(WebsockConstants.PAYLOAD);
        Assert.assertEquals(payload, testPayloadMap);

        BasicBSONList list2 = (BasicBSONList) payload.get(TEST_LIST_2_NAME);
        Assert.assertEquals(testList2, list2);
    }

    /**
     * Tests the decoding of websocket queries in BSON format with
     * pass-through.
     */
    @Test
    public void testDecode()
    {
        //basic query object
        BasicBSONObject bson = new BasicBSONObject();
        bson.put(WebsockConstants.QUERY_ID, TEST_ID_1);
        bson.put(WebsockConstants.QUERY_TYPE, TEST_TYPE_1.getCode());

        //parameters
        BasicBSONObject testParamMap = new BasicBSONObject();

        List<Object> testParamList = new BasicBSONList();
        testParamList.add(TEST_INT_1);
        testParamList.add(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        bson.put(WebsockConstants.PARAMETERS, testParamMap);

        //payload
        BasicBSONObject testPayloadMap = new BasicBSONObject();

        Map<String, Object> testMap = new BasicBSONObject();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        List<Object> testList2 = new BasicBSONList();
        testList2.add(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        bson.put(WebsockConstants.PAYLOAD, testPayloadMap);

        //decode and check
        WebsockQuery query = BsonConverter.fromBson(bson);
        Assert.assertEquals(TEST_ID_1, query.getId());
        Assert.assertEquals(TEST_TYPE_1, query.getType());

        //parameters
        BSONObject parameters = (BSONObject) query.getParameters();
        Assert.assertTrue(parameters == testParamMap);

        BasicBSONList listParam = (BasicBSONList) parameters.get(
            TEST_LIST_1_NAME);
        Assert.assertTrue(listParam == testParamList);

        //payload
        BSONObject payload = (BSONObject) query.getPayload();
        Assert.assertTrue(payload == testPayloadMap);

        BasicBSONList list2 = (BasicBSONList) payload.get(TEST_LIST_2_NAME);
        Assert.assertTrue(testList2 == list2);
    }

    /**
     * Test for successive encoding and decoding of a query
     */
    @Test
    public void converterSelfTest()
    {
        WebsockQuery query = new WebsockQuery(TEST_ID_1, TEST_TYPE_1);

        //parameters
        Map<String, Object> testParamMap = new HashMap<String, Object>();

        List<Object> testParamList = new ArrayList<Object>();
        testParamList.add(TEST_INT_1);
        testParamList.add(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        query.setParameters(testParamMap);

        //payload
        Map<String, Object> testPayloadMap = new HashMap<String, Object>();

        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        List<Object> testList2 = new LinkedList<Object>();
        testList2.add(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        query.setPayload(testPayloadMap);

        //encode, decode and check
        BSONObject bson = BsonConverter.toBson(query);
        query = BsonConverter.fromBson(bson);

        Assert.assertEquals(TEST_ID_1, query.getId());
        Assert.assertEquals(TEST_TYPE_1, query.getType());

        //parameters
        BSONObject parameters = (BSONObject) query.getParameters();

        Assert.assertEquals(TEST_STRING_1, parameters.get(TEST_STRING_1_NAME));
        Assert.assertEquals(TEST_BOOL, parameters.get(TEST_BOOL_NAME));

        BasicBSONList list = (BasicBSONList) parameters.get(TEST_LIST_1_NAME);

        Assert.assertEquals(TEST_INT_1, list.get(0));
        Assert.assertEquals(TEST_LONG_1, list.get(1));

        //payload
        BSONObject payload = (BSONObject) query.getPayload();

        BasicBSONList pList = (BasicBSONList) payload.get(TEST_LIST_2_NAME);
        BSONObject pMap = (BSONObject) pList.get(0);
        Assert.assertArrayEquals(TEST_BYTE_ARRAY,
            (byte[]) pMap.get(TEST_BYTE_ARRAY_NAME));

        Assert.assertEquals(TEST_STRING_1, payload.get(TEST_STRING_1_NAME));
    }
}
