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
package de.hofuniversity.iisys.neo4j.websock.queries;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;

import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.util.ResultSetConverter;

/**
 * Fake session implementation providing a fake remote endpoint to receive and
 * deliver messages locally.
 */
public class FakeWebsockSession implements Session
{
    private final LinkedList<WebsockQuery> fResponses;
    private final LinkedList<ByteBuffer> fBinaryMessages;
    private final LinkedList<String> fTextMessages;

    private final Basic fBasicRemote;

    /**
     * Creates a new empty fake websocket session with a basic remote storing
     * responses in a local list.
     */
    public FakeWebsockSession()
    {
        fResponses = new LinkedList<WebsockQuery>();
        fBinaryMessages = new LinkedList<ByteBuffer>();
        fTextMessages = new LinkedList<String>();

        fBasicRemote = new FakeBasicRemote();
    }

    /**
     * @return list of responses
     */
    public LinkedList<WebsockQuery> getResponses()
    {
        return fResponses;
    }

    /**
     * @return list of sent binary messages
     */
    public LinkedList<ByteBuffer> getBinaryMessages()
    {
        return fBinaryMessages;
    }

    /**
     * @return list of sent text messages
     */
    public LinkedList<String> getTextMessages()
    {
        return fTextMessages;
    }

    @Override
    public void addMessageHandler(MessageHandler handler)
        throws IllegalStateException {}

    @Override
    public void close() throws IOException {}

    @Override
    public void close(CloseReason closeReason) throws IOException {}

    @Override
    public Async getAsyncRemote()
    {
        return null;
    }

    @Override
    public Basic getBasicRemote()
    {
        return fBasicRemote;
    }

    @Override
    public WebSocketContainer getContainer()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return null;
    }

    @Override
    public int getMaxBinaryMessageBufferSize()
    {
        return 0;
    }

    @Override
    public long getMaxIdleTimeout()
    {
        return 0;
    }

    @Override
    public int getMaxTextMessageBufferSize()
    {
        return 0;
    }

    @Override
    public Set<MessageHandler> getMessageHandlers()
    {
        return null;
    }

    @Override
    public List<Extension> getNegotiatedExtensions()
    {
        return null;
    }

    @Override
    public String getNegotiatedSubprotocol()
    {
        return null;
    }

    @Override
    public Set<Session> getOpenSessions()
    {
        return null;
    }

    @Override
    public Map<String, String> getPathParameters()
    {
        return null;
    }

    @Override
    public String getProtocolVersion()
    {
        return null;
    }

    @Override
    public String getQueryString()
    {
        return null;
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap()
    {
        return null;
    }

    @Override
    public URI getRequestURI()
    {
        return null;
    }

    @Override
    public Principal getUserPrincipal()
    {
        return null;
    }

    @Override
    public Map<String, Object> getUserProperties()
    {
        return null;
    }

    @Override
    public boolean isOpen()
    {
        return true;
    }

    @Override
    public boolean isSecure()
    {
        return true;
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {}

    @Override
    public void setMaxBinaryMessageBufferSize(int length) {}

    @Override
    public void setMaxIdleTimeout(long milliseconds) {}

    @Override
    public void setMaxTextMessageBufferSize(int length) {}

    /**
     * Fake basic remote, storing outgoing messages locally.
     */
    public class FakeBasicRemote implements Basic
    {
        @Override
        public void flushBatch() throws IOException {}

        @Override
        public boolean getBatchingAllowed()
        {
            return false;
        }

        @Override
        public void sendPing(ByteBuffer applicationData)
            throws IOException, IllegalArgumentException {}

        @Override
        public void sendPong(ByteBuffer applicationData)
            throws IOException, IllegalArgumentException {}

        @Override
        public void setBatchingAllowed(boolean allowed)
            throws IOException {}

        @Override
        public OutputStream getSendStream() throws IOException
        {
            return null;
        }

        @Override
        public Writer getSendWriter() throws IOException
        {
            return null;
        }

        @Override
        public void sendBinary(ByteBuffer data) throws IOException
        {
            fBinaryMessages.add(data);
        }

        @Override
        public void sendBinary(ByteBuffer partialByte,
            boolean isLast) throws IOException {}

        @Override
        public void sendObject(Object data) throws IOException,
            EncodeException
        {
            WebsockQuery query = (WebsockQuery) data;

            if(query.getType() == EQueryType.PING)
            {
                fResponses.add(new WebsockQuery(query.getId(),
                    EQueryType.PONG));
            }
            else if(query.getType() == EQueryType.DIRECT_CYPHER)
            {
                SingleResult result = new SingleResult(
                    new HashMap<String, Object>());
                Map<String, Object> payload =
                    ResultSetConverter.toMap(result,
                    new HashMap<String, Object>());
                WebsockQuery response = new WebsockQuery(
                    query.getId(), EQueryType.RESULT);
                response.setPayload(payload);
                fResponses.add(response);
            }
            else
            {
                fResponses.add(query);
            }
        }

        @Override
        public void sendText(String text) throws IOException
        {
            try
            {
                fTextMessages.add(text);
                sendObject(new TSafeJsonQueryHandler().decode(
                    text));
            }
            catch (EncodeException e)
            {
                e.printStackTrace();
            }
            catch (DecodeException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void sendText(String partialMessage, boolean isLast)
            throws IOException {}
    }
}
