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
package de.hofuniversity.iisys.neo4j.websock.query.encoding.logging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class LoggingStringTransferUtil implements MessageHandler.Whole<String>,
    Cloneable
{
    public static final Map<String, List<Integer>> LOGGED_IN_SIZES =
        new HashMap<String, List<Integer>>();
    public static final Map<String, List<Integer>> LOGGED_OUT_SIZES =
        new HashMap<String, List<Integer>>();
    public static final Map<String, List<Long>> LOGGED_IN_TIMES =
        new HashMap<String, List<Long>>();
    public static final Map<String, List<Long>> LOGGED_OUT_TIMES =
        new HashMap<String, List<Long>>();

    public static boolean LOGGING_ENABLED = false;

    public static final Map<Integer, String> QUERY_TYPES =
        new HashMap<Integer, String>();

    private final Basic fRemote;
    private final IMessageHandler fHandler;

    /**
     * Creates a text transfer utility, sending data over the given basic
     * remote, forwarding incoming text data to the given message handler.
     *
     * @param remote remote to send data with
     * @param handler handler to forward incoming data to
     */
    public LoggingStringTransferUtil(Basic remote, IMessageHandler handler)
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
        long time = System.nanoTime();
        JSONObject json = new JSONObject(message);
        WebsockQuery query = JsonConverter.fromJson(json);
        time = System.nanoTime() - time;

        //retrieve type
        if(LOGGING_ENABLED)
        {
            //store query type
            final String type = query.getPayload().toString();
            synchronized(QUERY_TYPES)
            {
                QUERY_TYPES.put(query.getId(), type);
            }

            //store size of query
            List<Integer> sizes = null;
            synchronized(LOGGED_IN_SIZES)
            {
                sizes = LOGGED_IN_SIZES.get(type);

                if(sizes == null)
                {
                    sizes = new LinkedList<Integer>();
                    LOGGED_IN_SIZES.put(type, sizes);
                }
            }

            synchronized(sizes)
            {
                sizes.add(message.getBytes().length);
            }

            //store time taken
            List<Long> times = null;
            synchronized(LOGGED_IN_TIMES)
            {
                times = LOGGED_IN_TIMES.get(type);

                if(times == null)
                {
                    times = new LinkedList<Long>();
                    LOGGED_IN_TIMES.put(type, times);
                }
            }

            synchronized(times)
            {
                times.add(time);
            }
        }

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
        long time = System.nanoTime();
        JSONObject json = JsonConverter.toJson(message);
        String str = json.toString();
        time = System.nanoTime() - time;

        if(LOGGING_ENABLED)
        {
            String type = null;
            synchronized(QUERY_TYPES)
            {
                type = QUERY_TYPES.get(message.getId());
            }

            //store size of query
            List<Integer> sizes = null;
            synchronized(LOGGED_OUT_SIZES)
            {
                sizes = LOGGED_OUT_SIZES.get(type);

                if(sizes == null)
                {
                    sizes = new LinkedList<Integer>();
                    LOGGED_OUT_SIZES.put(type, sizes);
                }
            }

            synchronized(sizes)
            {
                sizes.add(str.getBytes().length);
            }

            //store time taken
            List<Long> times = null;
            synchronized(LOGGED_OUT_TIMES)
            {
                times = LOGGED_OUT_TIMES.get(type);

                if(times == null)
                {
                    times = new LinkedList<Long>();
                    LOGGED_OUT_TIMES.put(type, times);
                }
            }

            synchronized(times)
            {
                times.add(time);
            }
        }

        fRemote.sendText(str);
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
    public LoggingStringTransferUtil clone()
    {
        return new LoggingStringTransferUtil(fRemote, fHandler);
    }
}
