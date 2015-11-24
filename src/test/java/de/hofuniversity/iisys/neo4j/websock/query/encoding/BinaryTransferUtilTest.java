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

import javax.websocket.RemoteEndpoint.Basic;

import org.junit.Assert;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.queries.FakeWebsockSession;
import de.hofuniversity.iisys.neo4j.websock.queries.TestMessageHandler;
import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.BsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.DeflateBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.DeflateJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Test for the transfer utility decoding, encoding and sending binary
 * messages.
 */
public class BinaryTransferUtilTest
{
    /**
     * Tests the transfer of binary encoded messages.
     */
    @Test
    public void transferTest() throws Exception
    {
        //non thread safe
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            false);

        binary.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send with default format (uncompressed BSON)
        WebsockQuery query = new WebsockQuery(42, EQueryType.PROCEDURE_CALL);
        binary.sendMessage(query);

        //verify
        ByteBuffer bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PROCEDURE_CALL, query.getType());


        //thread safe
        binary = new BinaryTransferUtil(remote, handler, true);

        binary.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send with default format (uncompressed BSON)
        query = new WebsockQuery(42, EQueryType.PROCEDURE_CALL);
        binary.sendMessage(query);

        //verify
        bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PROCEDURE_CALL, query.getType());
    }

    /**
     * Tests the decoding of binary encoded messages.
     */
    @Test
    public void decodingTest() throws Exception
    {
        //non thread safe
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            false);

        binary.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send with default format (uncompressed BSON)
        WebsockQuery query = new WebsockQuery(42, EQueryType.PROCEDURE_CALL);
        binary.sendMessage(query);

        //verify
        ByteBuffer bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PROCEDURE_CALL, query.getType());


        //thread safe
        binary = new BinaryTransferUtil(remote, handler, true);

        binary.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.NO_COMPRESSION);

        //send with default format (uncompressed BSON)
        query = new WebsockQuery(42, EQueryType.PROCEDURE_CALL);
        binary.sendMessage(query);

        //verify
        bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PROCEDURE_CALL, query.getType());
    }

    /**
     * Tests switching formats at runtime with thread safe implementations.
     */
    @Test
    public void tSafeformatSwitchTest() throws Exception
    {
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            true);

        formatSwitchTest(binary, session);
    }

    /**
     * Tests switching formats at runtime with thread safe implementations.
     */
    @Test
    public void nonTSafeformatSwitchTest() throws Exception
    {
        TestMessageHandler handler = new TestMessageHandler();
        FakeWebsockSession session = new FakeWebsockSession();
        Basic remote = session.getBasicRemote();

        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            false);

        formatSwitchTest(binary, session);
    }

    private void formatSwitchTest(BinaryTransferUtil binary,
        FakeWebsockSession session) throws Exception
    {
        //compressed BSON
        binary.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.FASTEST_COMPRESSION);

        //send
        WebsockQuery query = new WebsockQuery(42, EQueryType.ERROR);
        binary.sendMessage(query);

        //verify
        ByteBuffer bin = session.getBinaryMessages().pop();
        query = new DeflateBsonQueryHandler(
            WebsockConstants.FASTEST_COMPRESSION).decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.ERROR, query.getType());



        binary.setFormat(WebsockConstants.BSON_FORMAT,
            WebsockConstants.BEST_COMPRESSION);

        //send
        query = new WebsockQuery(42, EQueryType.ERROR);
        binary.sendMessage(query);

        //verify
        bin = session.getBinaryMessages().pop();
        query = new DeflateBsonQueryHandler(
            WebsockConstants.BEST_COMPRESSION).decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.ERROR, query.getType());




        //compressed JSON
        binary.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.FASTEST_COMPRESSION);

        //send
        query = new WebsockQuery(42, EQueryType.SUCCESS);
        binary.sendMessage(query);

        //verify
        bin = session.getBinaryMessages().pop();
        query = new DeflateJsonQueryHandler(
            WebsockConstants.FASTEST_COMPRESSION).decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.SUCCESS, query.getType());




        binary.setFormat(WebsockConstants.JSON_FORMAT,
            WebsockConstants.BEST_COMPRESSION);

        //send
        query = new WebsockQuery(42, EQueryType.SUCCESS);
        binary.sendMessage(query);

        //verify
        bin = session.getBinaryMessages().pop();
        query = new DeflateJsonQueryHandler(
            WebsockConstants.BEST_COMPRESSION).decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.SUCCESS, query.getType());
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

        BinaryTransferUtil binary = new BinaryTransferUtil(remote, handler,
            true);

        //unsupported
        try
        {
            binary.setFormat(WebsockConstants.JSON_FORMAT,
                WebsockConstants.NO_COMPRESSION);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //should still be operational with default format
        WebsockQuery query = new WebsockQuery(42, EQueryType.PROCEDURE_CALL);
        binary.sendMessage(query);

        //verify
        ByteBuffer bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PROCEDURE_CALL, query.getType());

        //non-existent
        try
        {
            binary.setFormat("bogus", "format");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //should still be operational with default format
        query = new WebsockQuery(42, EQueryType.PROCEDURE_CALL);
        binary.sendMessage(query);

        //verify
        bin = session.getBinaryMessages().pop();
        query = new BsonQueryHandler().decode(bin);

        Assert.assertEquals(42, query.getId());
        Assert.assertEquals(EQueryType.PROCEDURE_CALL, query.getType());
    }
}
