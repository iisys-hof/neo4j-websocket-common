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

import org.junit.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Test for the converter transforming websocket queries into JSON and vice
 * versa.
 */
public class JsonConverterTest
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
     * Tests conversion of generic maps, lists and primitive values into JSON.
     */
    @Test
    public void testGenericEncode() throws Exception
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

        //encode and check
        JSONObject json = JsonConverter.toJson(query);

        Assert.assertEquals(TEST_ID_1, json.get(WebsockConstants.QUERY_ID));
        Assert.assertEquals(TEST_TYPE_1.getCode(),
            json.get(WebsockConstants.QUERY_TYPE));

        //parameters
        JSONObject parameters = json.getJSONObject(
            WebsockConstants.PARAMETERS);

        Assert.assertEquals(TEST_STRING_1, parameters.get(TEST_STRING_1_NAME));
        Assert.assertEquals(TEST_BOOL, parameters.getBoolean(TEST_BOOL_NAME));

        JSONArray list1 = parameters.getJSONArray(TEST_LIST_1_NAME);
        Assert.assertEquals(TEST_INT_1, list1.get(0));
        Assert.assertEquals(TEST_LONG_1, list1.get(1));

        //payload
        JSONObject payload = json.getJSONObject(WebsockConstants.PAYLOAD);

        Assert.assertEquals(TEST_STRING_1, payload.get(TEST_STRING_1_NAME));

        JSONArray list2 = payload.getJSONArray(TEST_LIST_2_NAME);
        JSONObject pMap = list2.getJSONObject(0);

        Assert.assertArrayEquals(TEST_BYTE_ARRAY,
            (byte[]) pMap.get(TEST_BYTE_ARRAY_NAME));
    }

    /**
     * Tests the encoding of maps and lists that are already wrapped JSON
     * objctes using pass-through encoding.
     */
    @Test
    public void testJsonEncode() throws Exception
    {
        WebsockQuery query = new WebsockQuery(TEST_ID_1, TEST_TYPE_1);

        //parameters
        JSONMap testParamMap = new JSONMap();

        JSONList testParamList = new JSONList();
        testParamList.add(TEST_INT_1);
        testParamList.add(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        query.setParameters(testParamMap);

        //payload
        JSONMap testPayloadMap = new JSONMap();

        JSONMap testMap = new JSONMap();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        JSONList testList2 = new JSONList();
        testList2.add(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        query.setPayload(testPayloadMap);

        //encode and check
        JSONObject json = JsonConverter.toJson(query);

        Assert.assertEquals(TEST_ID_1, json.get(WebsockConstants.QUERY_ID));
        Assert.assertEquals(TEST_TYPE_1.getCode(),
            json.get(WebsockConstants.QUERY_TYPE));

        //parameters
        JSONObject parameters = json.getJSONObject(WebsockConstants.PARAMETERS);

        Assert.assertTrue(parameters == testParamMap.getJson());

        JSONArray list = parameters.getJSONArray(TEST_LIST_1_NAME);

        Assert.assertTrue(list == testParamList.getJson());

        //payload
        JSONObject payload = json.getJSONObject(WebsockConstants.PAYLOAD);

        Assert.assertTrue(payload == testPayloadMap.getJson());

        JSONArray pList = payload.getJSONArray(TEST_LIST_2_NAME);

        Assert.assertTrue(pList == testList2.getJson());
    }

    /**
     * Tests the decoding of websocket queries in JSON format using the
     * appropriate wrappers.
     */
    @Test
    public void testDecode() throws Exception
    {
        //basic query object
        JSONObject json = new JSONObject();
        json.put(WebsockConstants.QUERY_ID, TEST_ID_1);
        json.put(WebsockConstants.QUERY_TYPE, TEST_TYPE_1.getCode());

        //parameters
        JSONObject testParamMap = new JSONObject();

        JSONArray testParamList = new JSONArray();
        testParamList.put(TEST_INT_1);
        testParamList.put(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        json.put(WebsockConstants.PARAMETERS, testParamMap);

        //payload
        JSONObject testPayloadMap = new JSONObject();

        JSONObject testMap = new JSONObject();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        JSONArray testList2 = new JSONArray();
        testList2.put(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        json.put(WebsockConstants.PAYLOAD, testPayloadMap);

        //decode and check
        WebsockQuery query = JsonConverter.fromJson(json);
        Assert.assertEquals(TEST_ID_1, query.getId());
        Assert.assertEquals(TEST_TYPE_1, query.getType());

        //parameters
        JSONMap parameters = (JSONMap) query.getParameters();
        Assert.assertTrue(parameters.getJson() == testParamMap);

        JSONList listParam = (JSONList) parameters.get(
            TEST_LIST_1_NAME);
        Assert.assertTrue(listParam.getJson() == testParamList);

        //payload
        JSONMap payload = (JSONMap) query.getPayload();
        Assert.assertTrue(payload.getJson() == testPayloadMap);

        JSONList list2 = (JSONList) payload.get(TEST_LIST_2_NAME);
        Assert.assertTrue(testList2 == list2.getJson());
    }

    /**
     * Test for successive encoding and decoding of a query
     */
    @Test
    public void converterSelfTest() throws Exception
    {
        WebsockQuery query = new WebsockQuery(TEST_ID_1, TEST_TYPE_1);

        //parameters
        JSONMap testParamMap = new JSONMap();

        JSONList testParamList = new JSONList();
        testParamList.add(TEST_INT_1);
        testParamList.add(TEST_LONG_1);

        testParamMap.put(TEST_LIST_1_NAME, testParamList);

        testParamMap.put(TEST_STRING_1_NAME, TEST_STRING_1);
        testParamMap.put(TEST_BOOL_NAME, TEST_BOOL);

        query.setParameters(testParamMap);

        //payload
        JSONMap testPayloadMap = new JSONMap();

        JSONMap testMap = new JSONMap();
        testMap.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);
        JSONList testList2 = new JSONList();
        testList2.add(testMap);
        testPayloadMap.put(TEST_LIST_2_NAME, testList2);

        testPayloadMap.put(TEST_STRING_1_NAME, TEST_STRING_1);

        query.setPayload(testPayloadMap);

        //encode, decode and check
        JSONObject json = JsonConverter.toJson(query);
        query = JsonConverter.fromJson(json);

        //parameters
        JSONMap parameters = (JSONMap) query.getParameters();
        Assert.assertTrue(parameters.getJson() == testParamMap.getJson());

        JSONList listParam = (JSONList) parameters.get(
            TEST_LIST_1_NAME);
        Assert.assertTrue(listParam.getJson() == testParamList.getJson());

        //payload
        JSONMap payload = (JSONMap) query.getPayload();
        Assert.assertTrue(payload.getJson() == testPayloadMap.getJson());

        JSONList list2 = (JSONList) payload.get(TEST_LIST_2_NAME);
        Assert.assertTrue(testList2.getJson() == list2.getJson());
    }
}
