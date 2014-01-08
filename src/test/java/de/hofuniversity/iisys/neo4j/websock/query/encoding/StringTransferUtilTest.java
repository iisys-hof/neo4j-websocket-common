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
package de.hofuniversity.iisys.neo4j.websock.query.encoding;

import javax.websocket.RemoteEndpoint.Basic;

import org.junit.Assert;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.queries.FakeWebsockSession;
import de.hofuniversity.iisys.neo4j.websock.queries.TestMessageHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Tests the encoding and decoding functionality of the String transfer
 * utility.
 */
public class StringTransferUtilTest
{
    /**
     * Tests the uncompressed transfer of text encoded messages.
     */
    @Test
    public void basicTransferTest() throws Exception
    {
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        StringTransferUtil strings = new StringTransferUtil(remote, handler);

        strings.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send
        WebsockQuery query = new WebsockQuery(42, EQueryType.PING);
        strings.sendMessage(query);

        //verify
        String text = session.getTextMessages().pop();
        query = new TSafeJsonQueryHandler().decode(text);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PING, query.getType());
    }

    /**
     * Tests the decoding of text encoded messages.
     */
    @Test
    public void decodingTest() throws Exception
    {
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        StringTransferUtil strings = new StringTransferUtil(remote, handler);

        strings.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //encode
        WebsockQuery query = new WebsockQuery(42, EQueryType.PONG);
        String text = new TSafeJsonQueryHandler().encode(query);

        //decode and verify
        query = strings.convert(text);
        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PONG, query.getType());
    }

    /**
     * Tests a switch to an unsupported format.
     */
    @Test
    public void unsupportedTest() throws Exception
    {
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        StringTransferUtil strings = new StringTransferUtil(remote, handler);

        try
        {
            strings.setFormat(WebsockConstants.BSON_FORMAT,
                WebsockConstants.BEST_COMPRESSION);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //should still be operational with default format
        //send
        WebsockQuery query = new WebsockQuery(42, EQueryType.PING);
        strings.sendMessage(query);

        //verify
        String text = session.getTextMessages().pop();
        query = new TSafeJsonQueryHandler().decode(text);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PING, query.getType());
    }
}
