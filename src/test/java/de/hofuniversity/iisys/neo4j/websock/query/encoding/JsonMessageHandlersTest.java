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
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeDeflateJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.DeflateJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Tests for all JSON-based query handlers.
 */
public class JsonMessageHandlersTest
{
    /**
     * Tests the uncompressed thread safe JSON message handler.
     */
    @Test
    public void uncompTSafeTest() throws Exception
    {
        TSafeJsonQueryHandler handler = new TSafeJsonQueryHandler();
        reextractText(handler, handler);
    }

    /**
     * Tests the compressing non thread safe JSON message handler.
     */
    @Test
    public void compressedTest() throws Exception
    {
        DeflateJsonQueryHandler handler = new DeflateJsonQueryHandler(
            WebsockConstants.FASTEST_COMPRESSION);
        reextractBin(handler, handler);

        handler = new DeflateJsonQueryHandler(
            WebsockConstants.BEST_COMPRESSION);
        reextractBin(handler, handler);
    }

    /**
     * Tests the compressing thread safe JSON message handler.
     */
    @Test
    public void compressedTSafeTest() throws Exception
    {
        TSafeDeflateJsonQueryHandler handler =
            new TSafeDeflateJsonQueryHandler(
                WebsockConstants.FASTEST_COMPRESSION);
        reextractBin(handler, handler);

        handler = new TSafeDeflateJsonQueryHandler(
                WebsockConstants.BEST_COMPRESSION);
        reextractBin(handler, handler);
    }

    private void reextractText(Encoder.Text<WebsockQuery> encoder,
        Decoder.Text<WebsockQuery> decoder) throws Exception
    {
        //set some test values
        WebsockQuery query = setValues();

        //encode and decode again
        String text = encoder.encode(query);
        query = decoder.decode(text);

        //check
        checkValues(query);
    }

    private void reextractBin(Encoder.Binary<WebsockQuery> encoder,
        Decoder.Binary<WebsockQuery> decoder) throws Exception
    {
        //set some test values
        WebsockQuery query = setValues();

        //encode and decode again
        ByteBuffer binary = encoder.encode(query);
        query = decoder.decode(binary);

        //check
        checkValues(query);
    }

    private WebsockQuery setValues()
    {
        WebsockQuery query = new WebsockQuery(42, EQueryType.STORE_PROCEDURE);
        query.setParameter("param1", "value1");
        query.setParameter("param2", 2);
        query.setParameter("param3", Long.MAX_VALUE);

        List<Object> param4 = new ArrayList<Object>();
        param4.add("value1");
        param4.add(2);
        param4.add(Long.MIN_VALUE);
        param4.add(42.42f);
        query.setParameter("param4", param4);

        query.setParameter("param5", 42.42f);
        query.setParameter("param6", 42.42);

        query.setPayload(query.getParameters());

        return query;
    }

    @SuppressWarnings("unchecked")
    private void checkValues(WebsockQuery query)
    {
        Assert.assertEquals("value1", query.getParameter("param1"));
        Assert.assertEquals(2, query.getParameter("param2"));
        Assert.assertEquals(Long.MAX_VALUE, query.getParameter("param3"));

        List<Object> param4 = (List<Object>) query.getParameter("param4");
        Assert.assertEquals("value1", param4.get(0));
        Assert.assertEquals(2, param4.get(1));
        Assert.assertEquals(Long.MIN_VALUE, param4.get(2));
        Assert.assertEquals(42.42f, (Double)param4.get(3), 0.001);

        Assert.assertEquals(42.42f,
            (Double)query.getParameter("param5"), 0.001);
        Assert.assertEquals(42.42,
            (Double)query.getParameter("param6"), 0.001);


        Map<String, Object> payload = (Map<String, Object>) query.getPayload();
        Assert.assertEquals("value1", payload.get("param1"));
        Assert.assertEquals(2, payload.get("param2"));
        Assert.assertEquals(Long.MAX_VALUE, payload.get("param3"));

        param4 = (List<Object>) payload.get("param4");
        Assert.assertEquals("value1", param4.get(0));
        Assert.assertEquals(2, param4.get(1));
        Assert.assertEquals(Long.MIN_VALUE, param4.get(2));
        Assert.assertEquals(42.42f, (Double)param4.get(3), 0.001);

        Assert.assertEquals(42.42f,
            (Double)payload.get("param5"), 0.001);
        Assert.assertEquals(42.42,
            (Double)payload.get("param6"), 0.001);
    }
}
