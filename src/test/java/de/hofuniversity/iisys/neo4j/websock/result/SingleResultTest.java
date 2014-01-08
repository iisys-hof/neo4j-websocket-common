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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Tests for the implementation of a single result.
 */
public class SingleResultTest
{
    /**
     * Tests the instantiation of a new single result.
     */
    @Test
    public void newResultTest()
    {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("some", "value");
        payload.put("another", "entry");
        payload.put("more", "data");

        SingleResult result = new SingleResult(payload);
        Assert.assertEquals(0, result.getFirst());
        Assert.assertEquals(-1, result.getMax());
        Assert.assertEquals(-1, result.getTotal());
        Assert.assertEquals(1, result.getSize());
        Assert.assertEquals(payload, result.getResults());
    }

    /**
     * Tests the creation of a single result from a transferable map.
     */
    @Test
    public void fromMapTest()
    {
        //create expected map
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put(WebsockConstants.RESULT_TYPE,
            WebsockConstants.SINGLE_RESULT);

        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("some", "value");
        payload.put("another", "entry");
        payload.put("more", "data");

        resMap.put(WebsockConstants.RESULT, payload);

        //decode
        SingleResult result = new SingleResult(resMap, true);
        Assert.assertEquals(0, result.getFirst());
        Assert.assertEquals(-1, result.getMax());
        Assert.assertEquals(-1, result.getTotal());
        Assert.assertEquals(1, result.getSize());
        Assert.assertEquals(payload, result.getResults());
    }
}
