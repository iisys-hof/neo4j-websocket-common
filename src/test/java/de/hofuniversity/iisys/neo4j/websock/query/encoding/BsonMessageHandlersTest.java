/*
 *  Copyright 2015 Institute of Information Systems, Hof University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package de.hofuniversity.iisys.neo4j.websock.query.encoding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.Decoder;
import javax.websocket.Encoder;

import org.junit.Assert;

import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeDeflateBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.BsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.DeflateBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Tests for all BSON-based query handlers.
 */
public class BsonMessageHandlersTest
{
    /**
     * Tests the uncompressed non thread safe BSON message handler.
     */
    @Test
    public void uncompTest() throws Exception
    {
        BsonQueryHandler handler = new BsonQueryHandler();
        reextract(handler, handler);
    }

    /**
     * Tests the uncompressed thread safe BSON message handler.
     */
    @Test
    public void uncompTSafeTest() throws Exception
    {
        TSafeBsonQueryHandler handler = new TSafeBsonQueryHandler();
        reextract(handler, handler);
    }

    /**
     * Tests the compressing non thread safe BSON message handler.
     */
    @Test
    public void compressedTest() throws Exception
    {
        DeflateBsonQueryHandler handler = new DeflateBsonQueryHandler(
            WebsockConstants.FASTEST_COMPRESSION);
        reextract(handler, handler);

        handler = new DeflateBsonQueryHandler(
            WebsockConstants.BEST_COMPRESSION);
        reextract(handler, handler);
    }

    /**
     * Tests the compressing thread safe BSON message handler.
     */
    @Test
    public void compressedTSafeTest() throws Exception
    {
        TSafeDeflateBsonQueryHandler handler =
            new TSafeDeflateBsonQueryHandler(
            WebsockConstants.FASTEST_COMPRESSION);
        reextract(handler, handler);

        handler = new TSafeDeflateBsonQueryHandler(
            WebsockConstants.BEST_COMPRESSION);
        reextract(handler, handler);
    }

    @SuppressWarnings("unchecked")
    private void reextract(Encoder.Binary<WebsockQuery> encoder,
        Decoder.Binary<WebsockQuery> decoder) throws Exception
    {
        //set some test values
        WebsockQuery query = new WebsockQuery(42, EQueryType.STORE_PROCEDURE);
        query.setParameter("param1", "value1");
        query.setParameter("param2", 2);
        query.setParameter("param3", Long.MAX_VALUE);

        List<Object> param4 = new ArrayList<Object>();
        param4.add("value1");
        param4.add(2);
        param4.add(Long.MIN_VALUE);
        param4.add(Float.MIN_VALUE);
        query.setParameter("param4", param4);

        query.setParameter("param5", Float.MAX_VALUE);
        query.setParameter("param6", Double.MIN_VALUE);

        query.setPayload(query.getParameters());


        //encode and decode again
        ByteBuffer binary = encoder.encode(query);
        query = decoder.decode(binary);

        //check
        Assert.assertEquals("value1", query.getParameter("param1"));
        Assert.assertEquals(2, query.getParameter("param2"));
        Assert.assertEquals(Long.MAX_VALUE, query.getParameter("param3"));

        param4 = (List<Object>) query.getParameter("param4");
        Assert.assertEquals("value1", param4.get(0));
        Assert.assertEquals(2, param4.get(1));
        Assert.assertEquals(Long.MIN_VALUE, param4.get(2));
        Assert.assertEquals(Float.MIN_VALUE, (Double)param4.get(3), 0.001);

        Assert.assertEquals(Float.MAX_VALUE,
            (Double)query.getParameter("param5"), 0.001);
        Assert.assertEquals(Double.MIN_VALUE,
            (Double)query.getParameter("param6"), 0.001);


        Map<String, Object> payload = (Map<String, Object>) query.getPayload();
        Assert.assertEquals("value1", payload.get("param1"));
        Assert.assertEquals(2, payload.get("param2"));
        Assert.assertEquals(Long.MAX_VALUE, payload.get("param3"));

        param4 = (List<Object>) payload.get("param4");
        Assert.assertEquals("value1", param4.get(0));
        Assert.assertEquals(2, param4.get(1));
        Assert.assertEquals(Long.MIN_VALUE, param4.get(2));
        Assert.assertEquals(Float.MIN_VALUE, (Double)param4.get(3), 0.001);

        Assert.assertEquals(Float.MAX_VALUE,
            (Double)payload.get("param5"), 0.001);
        Assert.assertEquals(Double.MIN_VALUE,
            (Double)payload.get("param6"), 0.001);
    }
}
