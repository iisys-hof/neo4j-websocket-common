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

import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint.Basic;

import org.json.JSONObject;

import de.hofuniversity.iisys.neo4j.websock.query.IMessageHandler;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.util.JsonConverter;

/**
 * Utility receiving text data and forwarding it to a message handler and
 * encoding and sending messages using the configured format.
 */
public class StringTransferUtil implements MessageHandler.Whole<String>,
    Cloneable
{
    private final Basic fRemote;
    private final IMessageHandler fHandler;

    /**
     * Creates a text transfer utility, sending data over the given basic
     * remote, forwarding incoming text data to the given message handler.
     *
     * @param remote remote to send data with
     * @param handler handler to forward incoming data to
     */
    public StringTransferUtil(Basic remote, IMessageHandler handler)
    {
        if(remote == null)
        {
            throw new NullPointerException("websocket remote was null");
        }
        if(handler == null)
        {
            throw new NullPointerException("query handler was null");
        }

        fRemote = remote;
        fHandler = handler;
    }

    @Override
    public void onMessage(final String message)
    {
        fHandler.onMessage(message);
    }

    /**
     * Converts text data into a WebsockQuery. First, the converter for the
     * configured format is used, then all others are tried. If all converters
     * fail, a RuntimeException is thrown.
     *
     * @param message text data to convert
     * @return converted WebsockQuery
     */
    public WebsockQuery convert(final String message) throws Exception
    {
        //currently, only uncompressed JSON is supported
        JSONObject json = new JSONObject(message);
        WebsockQuery query = JsonConverter.fromJson(json);

        return query;
    }

    /**
     * Converts a WebsockQuery to the configured format and sends it.
     *
     * @param message message to send
     * @throws Exception if conversion or sending fail
     */
    public void sendMessage(final WebsockQuery message) throws Exception
    {
        JSONObject json = JsonConverter.toJson(message);
        fRemote.sendText(json.toString());
    }

    /**
     * Sets the format to send messages in and the primary format to try
     * decoding incoming messages with.
     * Unknown formats are ignored and logged.
     *
     * @param format encoding to use for messages (bson/json)
     * @param compression which type of compression to use
     */
    public void setFormat(String format, String compression)
    {
        //currently, only uncompressed JSON is supported
    }

    /**
     * @return independent transfer utility using the same connection and
     *  current configuration
     */
    public StringTransferUtil clone()
    {
        return new StringTransferUtil(fRemote, fHandler);
    }
}
