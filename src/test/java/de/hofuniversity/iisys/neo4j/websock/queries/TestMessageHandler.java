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

import java.nio.ByteBuffer;
import java.util.LinkedList;

import de.hofuniversity.iisys.neo4j.websock.query.IMessageHandler;

/**
 * Message handler storing incoming data internally.
 */
public class TestMessageHandler implements IMessageHandler
{
    private final LinkedList<ByteBuffer> fBinaryMessages;
    private final LinkedList<String> fTextMessages;

    /**
     * Creates a message handler internally storing received data.
     */
    public TestMessageHandler()
    {
        fBinaryMessages = new LinkedList<ByteBuffer>();
        fTextMessages = new LinkedList<String>();
    }

    @Override
    public void onMessage(ByteBuffer message)
    {
        fBinaryMessages.add(message);
    }

    @Override
    public void onMessage(String message)
    {
        fTextMessages.add(message);
    }

    @Override
    public void dispose()
    {
        fBinaryMessages.clear();
        fTextMessages.clear();
    }

    /**
     * @return internal list of binary messages
     */
    public LinkedList<ByteBuffer> getBinaryMessages()
    {
        return fBinaryMessages;
    }

    /**
     * @return internal list of text messages
     */
    public LinkedList<String> getTextMessages()
    {
        return fTextMessages;
    }
}
