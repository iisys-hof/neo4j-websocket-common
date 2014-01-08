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
package de.hofuniversity.iisys.neo4j.websock.query.encoding.logging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.JSONException;
import org.json.JSONObject;

import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.util.JsonConverter;

/**
 * Query handler implementation encoding and decoding WebsockQueries as JSON.
 * When decoding, uses the JSON map and list wrapper classes.
 * Ideally, maps and lists in queries to be encoded are already JSON objects
 * in wrappers.
 */
public class LoggingTSafeJsonQueryHandler implements
    Encoder.Text<WebsockQuery>, Decoder.Text<WebsockQuery>
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


    private final Logger fLogger;
    private final boolean fDebug;

    private long fTotalBytesIn, fTotalBytesOut;

    public LoggingTSafeJsonQueryHandler()
    {
        fLogger = Logger.getLogger(this.getClass().getName());
        fDebug = (fLogger.getLevel() == Level.FINEST);
    }

    @Override
    public void destroy()
    {

    }

    @Override
    public void init(EndpointConfig arg0)
    {

    }

    @Override
    public String encode(final WebsockQuery query) throws EncodeException
    {
        String result = null;

        try
        {
            long time = System.nanoTime();
            final JSONObject obj = JsonConverter.toJson(query);
            result = obj.toString();
            time = System.nanoTime() - time;

            if(fDebug)
            {
                fTotalBytesOut += result.getBytes().length;
                fLogger.log(Level.FINEST, "encoded JSON message: "
                    + result.getBytes().length + " bytes\n"
                    + "total bytes sent: " + fTotalBytesOut);
            }

            //store query type
            final String type = query.getPayload().toString();
            synchronized(QUERY_TYPES)
            {
                QUERY_TYPES.put(query.getId(), type);
            }

            if(LOGGING_ENABLED)
            {
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
                    sizes.add(result.getBytes().length);
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
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            throw new EncodeException(query, "failed to encode JSON", e);
        }

        return result;
    }

    @Override
    public WebsockQuery decode(final String arg0) throws DecodeException
    {
        WebsockQuery query = null;

        try
        {
            long time = System.nanoTime();
            final JSONObject obj = new JSONObject(arg0);

            if(fDebug)
            {
                fTotalBytesIn += arg0.getBytes().length;
                fLogger.log(Level.FINEST, "received JSON message: "
                    + arg0.getBytes().length + " bytes\n"
                    + "total bytes received: " + fTotalBytesIn);
            }

            query = JsonConverter.fromJson(obj);

            time = System.nanoTime() - time;

            //retrieve type
            if(LOGGING_ENABLED)
            {
                String type = null;
                synchronized(QUERY_TYPES)
                {
                    type = QUERY_TYPES.get(query.getId());
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
                    sizes.add(arg0.getBytes().length);
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
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            throw new DecodeException(arg0, "failed to decode JSON", e);
        }

        return query;
    }

    @Override
    public boolean willDecode(String arg0)
    {
        boolean valid = true;

        //TODO: actually check whether it's a query
        try
        {
            new JSONObject(arg0);
        }
        catch (JSONException e)
        {
            valid = false;
        }

        return valid;
    }
}
