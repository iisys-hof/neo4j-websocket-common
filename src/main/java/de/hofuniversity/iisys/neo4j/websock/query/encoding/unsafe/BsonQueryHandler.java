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
package de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe;

import java.nio.ByteBuffer;
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
 * Optimized non-thread-safe version.
 */
public class BsonQueryHandler implements Encoder.Binary<WebsockQuery>,
    Decoder.Binary<WebsockQuery>
{
    private final BSONEncoder fEncoder;
    private final BSONDecoder fDecoder;
    private final Logger fLogger;
    private final boolean fDebug;

    private long fTotalBytesIn, fTotalBytesOut;

    public BsonQueryHandler()
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
        final BSONObject obj = BsonConverter.toBson(query);

        //encoder is not thread safe
        final byte[] data = fEncoder.encode(obj);
        fEncoder.done();

        if(fDebug)
        {
            fTotalBytesOut += data.length;
            fLogger.log(Level.FINEST, "encoded BSON message: "
                + data.length + " bytes\n"
                + "total bytes sent: " + fTotalBytesOut);
        }

        return ByteBuffer.wrap(data);
    }

    @Override
    public WebsockQuery decode(ByteBuffer buff) throws DecodeException
    {
        WebsockQuery query = null;

        try
        {
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
