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

import java.nio.ByteBuffer;
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

import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;

import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.util.BsonConverter;

/**
 * Query handler implementation encoding and decoding WebsockQueries as BSON.
 * When decoding, uses the provided BSON map and list classes.
 * Ideally, maps and lists in queries to be encoded are already BSON objects.
 */
public class LoggingBsonQueryHandler implements Encoder.Binary<WebsockQuery>,
    Decoder.Binary<WebsockQuery>
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

    private final BSONEncoder fEncoder;
    private final BSONDecoder fDecoder;
    private final Logger fLogger;
    private final boolean fDebug;

    private long fTotalBytesIn, fTotalBytesOut;

    public LoggingBsonQueryHandler()
    {
        fEncoder = new BasicBSONEncoder();
        fDecoder = new BasicBSONDecoder();
        fLogger = Logger.getLogger(this.getClass().getName());
        fDebug = (fLogger.getLevel() == Level.FINEST);
    }

    @Override
    public void destroy()
    {

    }

    @Override
    public void init(EndpointConfig config)
    {

    }

    @Override
    public ByteBuffer encode(final WebsockQuery query) throws EncodeException
    {
        long time = System.nanoTime();
        final BSONObject obj = BsonConverter.toBson(query);

        //encoder is not thread safe
        final byte[] data = fEncoder.encode(obj);
        fEncoder.done();

        time = System.nanoTime() - time;

        if(fDebug)
        {
            fTotalBytesOut += data.length;
            fLogger.log(Level.FINEST, "encoded BSON message: "
                + data.length + " bytes\n"
                + "total bytes sent: " + fTotalBytesOut);
        }

        if(LOGGING_ENABLED)
        {
            //retrieve type
            String type = null;
            synchronized(QUERY_TYPES)
            {
                type = QUERY_TYPES.get(query.getId());
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
                sizes.add(data.length);
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

        //encoder is not thread safe
        return ByteBuffer.wrap(data);
    }

    @Override
    public WebsockQuery decode(ByteBuffer buff) throws DecodeException
    {
        WebsockQuery query = null;

        try
        {
            long time = System.nanoTime();
            final byte[] data = buff.array();
            final BSONObject obj = fDecoder.readObject(data);

            if(fDebug)
            {
                fTotalBytesIn += data.length;
                fLogger.log(Level.FINEST, "received BSON message: "
                    + data.length + " bytes\n"
                    + "total bytes received: " + fTotalBytesIn);
            }

            query = BsonConverter.fromBson(obj);
            time = System.nanoTime() - time;

            //store query type
            final String type = query.getPayload().toString();
            synchronized(QUERY_TYPES)
            {
                QUERY_TYPES.put(query.getId(), type);
            }

            //store size of query
            if(LOGGING_ENABLED)
            {
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
                    sizes.add(data.length);
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
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DecodeException(buff, "failed to decode BSON", e);
        }


        return query;
    }

    @Override
    public boolean willDecode(ByteBuffer buff)
    {
        boolean valid = true;

        //TODO: actually check whether it's a query
        try
        {
            fDecoder.readObject(buff.array());
        }
        catch(Exception e)
        {
            valid = false;
        }

        return valid;
    }
}
