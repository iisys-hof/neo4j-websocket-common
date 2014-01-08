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

import java.nio.ByteBuffer;

import javax.websocket.RemoteEndpoint.Basic;

import org.junit.Assert;

import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.queries.FakeWebsockSession;
import de.hofuniversity.iisys.neo4j.websock.queries.TestMessageHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.BsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Tests in- and outbound functionality of the generic transfer utility.
 */
public class TransferUtilTest
{
    /**
     * Tests the transfer of generic messages.
     */
    @Test
    public void transferTest() throws Exception
    {
        //binary
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        StringTransferUtil strings = new StringTransferUtil(remote, handler);
        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            true);
        TransferUtil util = new TransferUtil(strings, binary);

        util.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send
        WebsockQuery query = new WebsockQuery(42, EQueryType.CONFIGURATION);
        util.sendMessage(query);

        //verify
        ByteBuffer bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.CONFIGURATION, query.getType());

        //text
        handler = new TestMessageHandler();
        session = new FakeWebsockSession();
        remote = session.getBasicRemote();

        strings = new StringTransferUtil(remote, handler);
        binary = new BinaryTransferUtil(remote, handler, true);
        util = new TransferUtil(strings, binary);

        util.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send
        query = new WebsockQuery(42, EQueryType.CONFIGURATION);
        util.sendMessage(query);

        //verify
        String text = session.getTextMessages().pop();
        query = new TSafeJsonQueryHandler().decode(text);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.CONFIGURATION, query.getType());
    }

    /**
     * Tests the decoding of different formats.
     */
    @Test
    public void decodingTest() throws Exception
    {
        //binary
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        StringTransferUtil strings = new StringTransferUtil(remote, handler);
        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            true);
        TransferUtil util = new TransferUtil(strings, binary);

        util.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //encode
        WebsockQuery query = new WebsockQuery(42, EQueryType.AUTHENTICATION);
        ByteBuffer bin = new BsonQueryHandler().encode(query);

        //decode and verify
        query = util.convert(bin);
        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.AUTHENTICATION, query.getType());

        //text
        handler = new TestMessageHandler();
        session = new FakeWebsockSession();
        remote = session.getBasicRemote();

        strings = new StringTransferUtil(remote, handler);
        binary = new BinaryTransferUtil(remote, handler, true);
        util = new TransferUtil(strings, binary);

        util.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //encode
        query = new WebsockQuery(42, EQueryType.AUTHENTICATION);
        String text = new TSafeJsonQueryHandler().encode(query);

        //decode and verify
        query = util.convert(text);
        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.AUTHENTICATION, query.getType());
    }

    /**
     * Tests switching formats at runtime.
     */
    @Test
    public void formatSwitchTest() throws Exception
    {
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        StringTransferUtil strings = new StringTransferUtil(remote, handler);
        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            true);
        TransferUtil util = new TransferUtil(strings, binary);

        //initialize with JSON format
        util.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send
        WebsockQuery query = new WebsockQuery(42, EQueryType.CONFIGURATION);
        util.sendMessage(query);

        //verify
        String text = session.getTextMessages().pop();
        query = new TSafeJsonQueryHandler().decode(text);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.CONFIGURATION, query.getType());


        //switch to BSON
        util.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send
        query = new WebsockQuery(42, EQueryType.CONFIGURATION);
        util.sendMessage(query);

        //verify
        ByteBuffer bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.CONFIGURATION, query.getType());
    }
}
