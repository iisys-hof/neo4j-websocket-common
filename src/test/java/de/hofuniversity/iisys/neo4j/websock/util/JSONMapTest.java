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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the map interface wrapper around JSON objects.
 */
public class JSONMapTest
{
    private static final String TEST_STRING = "aGweoF12 3p0ß45#äö ÄÖÜ;";
    private static final Integer TEST_INT = 123;
    private static final Long TEST_LONG = 456L;
    private static final Boolean TEST_BOOL = true;
    private static final String[] TEST_STRING_ARRAY = {"test", "string",
        "array"};
    private static final byte[] TEST_BYTE_ARRAY = {1, 2, 3, 4, 5, 6};


    private static final String TEST_STRING_NAME = "string 1";
    private static final String TEST_INT_NAME = "integer 1";
    private static final String TEST_LONG_NAME = "long 1";
    private static final String TEST_BOOL_NAME = "flag x";
    private static final String TEST_STRING_ARRAY_NAME = "string array";
    private static final String TEST_BYTE_ARRAY_NAME = "byte array";
    private static final String TEST_LIST_1_NAME = "list 1";
    private static final String TEST_LIST_2_NAME = "list 2";
    private static final String TEST_MAP_1_NAME = "map 1";
    private static final String TEST_MAP_2_NAME = "map 2";

    /**
     * Tests the storage and retrieval of primitive values and arrays.
     */
    @Test
    public void primitiveTest() throws Exception
    {
        //predefined JSON
        JSONObject json = new JSONObject();
        json.put(TEST_STRING_NAME, TEST_STRING);
        json.put(TEST_INT_NAME, TEST_INT);
        json.put(TEST_LONG_NAME, TEST_LONG);
        json.put(TEST_BOOL_NAME, TEST_BOOL);
        json.put(TEST_STRING_ARRAY_NAME, TEST_STRING_ARRAY);
        json.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);

        JSONMap map1 = new JSONMap(json);

        Assert.assertEquals(TEST_STRING, map1.get(TEST_STRING_NAME));
        Assert.assertEquals(TEST_INT, map1.get(TEST_INT_NAME));
        Assert.assertEquals(TEST_LONG, map1.get(TEST_LONG_NAME));
        Assert.assertEquals(TEST_BOOL, map1.get(TEST_BOOL_NAME));
        Assert.assertEquals(TEST_STRING_ARRAY,
            map1.get(TEST_STRING_ARRAY_NAME));
        Assert.assertEquals(TEST_BYTE_ARRAY, map1.get(TEST_BYTE_ARRAY_NAME));

        //internal JSON
        JSONMap map2 = new JSONMap();
        map2.put(TEST_STRING_NAME, TEST_STRING);
        map2.put(TEST_INT_NAME, TEST_INT);
        map2.put(TEST_LONG_NAME, TEST_LONG);
        map2.put(TEST_BOOL_NAME, TEST_BOOL);
        map2.put(TEST_STRING_ARRAY_NAME, TEST_STRING_ARRAY);
        map2.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);

        json = map2.getJson();

        Assert.assertEquals(TEST_STRING, map1.get(TEST_STRING_NAME));
        Assert.assertEquals(TEST_INT, map1.get(TEST_INT_NAME));
        Assert.assertEquals(TEST_LONG, map1.get(TEST_LONG_NAME));
        Assert.assertEquals(TEST_BOOL, map1.get(TEST_BOOL_NAME));
        Assert.assertEquals(TEST_STRING_ARRAY,
            map1.get(TEST_STRING_ARRAY_NAME));
        Assert.assertEquals(TEST_BYTE_ARRAY, map1.get(TEST_BYTE_ARRAY_NAME));

        //iteration
        Assert.assertEquals(6, map1.size());
        Assert.assertEquals(6, map2.size());

    }

    /**
     * Tests storage and retrieval of generic and compatible JSON lists.
     */
    @Test
    public void listTest() throws Exception
    {
        JSONMap jsonMap = new JSONMap();
        JSONObject json = jsonMap.getJson();

        //generic list
        List<Object> list1 = new ArrayList<Object>();
        list1.add(TEST_STRING);
        list1.add(TEST_INT);
        list1.add(TEST_LONG);
        list1.add(TEST_BOOL);
        list1.add(TEST_STRING_ARRAY);
        list1.add(TEST_BYTE_ARRAY);

        jsonMap.put(TEST_LIST_1_NAME, list1);

        //internal JSON
        JSONList list2 = new JSONList();
        list2.add(TEST_STRING);
        list2.add(TEST_INT);
        list2.add(TEST_LONG);
        list2.add(TEST_BOOL);
        list2.add(TEST_STRING_ARRAY);
        list2.add(TEST_BYTE_ARRAY);

        jsonMap.put(TEST_LIST_2_NAME, list2);

        //check
        //generic list
        JSONList jList1 = (JSONList) jsonMap.get(TEST_LIST_1_NAME);
        JSONArray array1 = json.getJSONArray(TEST_LIST_1_NAME);
        Assert.assertTrue(jList1.getJson() == array1);

        Assert.assertEquals(TEST_STRING, jList1.get(0));
        Assert.assertEquals(TEST_INT, jList1.get(1));
        Assert.assertEquals(TEST_LONG, jList1.get(2));
        Assert.assertEquals(TEST_BOOL, jList1.get(3));
        Assert.assertEquals(TEST_STRING_ARRAY, jList1.get(4));
        Assert.assertEquals(TEST_BYTE_ARRAY, jList1.get(5));

        //JSON list
        JSONList jList2 = (JSONList) jsonMap.get(TEST_LIST_2_NAME);
        JSONArray array2 = json.getJSONArray(TEST_LIST_2_NAME);
        Assert.assertTrue(jList2.getJson() == array2);
        Assert.assertTrue(list2.getJson() == array2);

        Assert.assertEquals(TEST_STRING, jList2.get(0));
        Assert.assertEquals(TEST_INT, jList2.get(1));
        Assert.assertEquals(TEST_LONG, jList2.get(2));
        Assert.assertEquals(TEST_BOOL, jList2.get(3));
        Assert.assertEquals(TEST_STRING_ARRAY, jList2.get(4));
        Assert.assertEquals(TEST_BYTE_ARRAY, jList2.get(5));
    }

    /**
     * Tests storage and retrieval of generic and compatible JSON maps.
     */
    @Test
    public void mapTest() throws Exception
    {
        JSONMap jsonMap = new JSONMap();
        JSONObject json = jsonMap.getJson();

        //generic map
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put(TEST_STRING_NAME, TEST_STRING);
        map1.put(TEST_INT_NAME, TEST_INT);
        map1.put(TEST_LONG_NAME, TEST_LONG);
        map1.put(TEST_BOOL_NAME, TEST_BOOL);
        map1.put(TEST_STRING_ARRAY_NAME, TEST_STRING_ARRAY);
        map1.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);

        jsonMap.put(TEST_MAP_1_NAME, map1);

        //JSON map
        JSONMap map2 = new JSONMap();
        map2.put(TEST_STRING_NAME, TEST_STRING);
        map2.put(TEST_INT_NAME, TEST_INT);
        map2.put(TEST_LONG_NAME, TEST_LONG);
        map2.put(TEST_BOOL_NAME, TEST_BOOL);
        map2.put(TEST_STRING_ARRAY_NAME, TEST_STRING_ARRAY);
        map2.put(TEST_BYTE_ARRAY_NAME, TEST_BYTE_ARRAY);

        jsonMap.put(TEST_MAP_2_NAME, map2);

        //check
        //generic map
        JSONMap jMap1 = (JSONMap) jsonMap.get(TEST_MAP_1_NAME);
        JSONObject object1 = json.getJSONObject(TEST_MAP_1_NAME);
        Assert.assertTrue(object1 == jMap1.getJson());

        Assert.assertEquals(TEST_STRING, jMap1.get(TEST_STRING_NAME));
        Assert.assertEquals(TEST_INT, jMap1.get(TEST_INT_NAME));
        Assert.assertEquals(TEST_LONG, jMap1.get(TEST_LONG_NAME));
        Assert.assertEquals(TEST_BOOL, jMap1.get(TEST_BOOL_NAME));
        Assert.assertEquals(TEST_STRING_ARRAY,
            jMap1.get(TEST_STRING_ARRAY_NAME));
        Assert.assertEquals(TEST_BYTE_ARRAY, jMap1.get(TEST_BYTE_ARRAY_NAME));

        //JSON map
        JSONMap jMap2 = (JSONMap) jsonMap.get(TEST_MAP_2_NAME);
        JSONObject object2 = json.getJSONObject(TEST_MAP_2_NAME);
        Assert.assertTrue(object2 == jMap2.getJson());
        Assert.assertTrue(map2.getJson() == object2);

        Assert.assertEquals(TEST_STRING, jMap2.get(TEST_STRING_NAME));
        Assert.assertEquals(TEST_INT, jMap2.get(TEST_INT_NAME));
        Assert.assertEquals(TEST_LONG, jMap2.get(TEST_LONG_NAME));
        Assert.assertEquals(TEST_BOOL, jMap2.get(TEST_BOOL_NAME));
        Assert.assertEquals(TEST_STRING_ARRAY,
            jMap2.get(TEST_STRING_ARRAY_NAME));
        Assert.assertEquals(TEST_BYTE_ARRAY, jMap2.get(TEST_BYTE_ARRAY_NAME));
    }
}
