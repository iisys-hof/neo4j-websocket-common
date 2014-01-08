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
package de.hofuniversity.iisys.neo4j.websock.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Test for the implementation of a list result.
 */
public class ListResultTest
{
    /**
     * Tests the instantiation of a new list result.
     */
    @Test
    public void newTableTest()
    {
        List<Map<String, Object>> resList =
            new ArrayList<Map<String, Object>>();

        Map<String, Object> res1 = new HashMap<String, Object>();
        res1.put("some", "value");
        resList.add(res1);

        Map<String, Object> res2 = new HashMap<String, Object>();
        res2.put("another", "entry");
        resList.add(res2);

        Map<String, Object> res3 = new HashMap<String, Object>();
        res3.put("more", "data");
        resList.add(res3);


        ListResult result = new ListResult(resList);
        Assert.assertEquals(0, result.getFirst());
        Assert.assertEquals(-1, result.getMax());
        Assert.assertEquals(-1, result.getTotal());
        Assert.assertEquals(3, result.getSize());
        Assert.assertEquals(resList, result.getResults());
    }

    /**
     * Tests the creation of a list result from a transferable map.
     */
    @Test
    public void fromMapTest()
    {
        //create expected map
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put(WebsockConstants.RESULT_TYPE, WebsockConstants.LIST_RESULT);

        List<Map<String, Object>> resList =
            new ArrayList<Map<String, Object>>();

        Map<String, Object> res1 = new HashMap<String, Object>();
        res1.put("some", "value");
        resList.add(res1);

        Map<String, Object> res2 = new HashMap<String, Object>();
        res2.put("another", "entry");
        resList.add(res2);

        Map<String, Object> res3 = new HashMap<String, Object>();
        res3.put("more", "data");
        resList.add(res3);

        resMap.put(WebsockConstants.RESULT, resList);

        //decode
        ListResult result = new ListResult(resMap);
        Assert.assertEquals(0, result.getFirst());
        Assert.assertEquals(-1, result.getMax());
        Assert.assertEquals(-1, result.getTotal());
        Assert.assertEquals(3, result.getSize());
        Assert.assertEquals(resList, result.getResults());
    }
}
