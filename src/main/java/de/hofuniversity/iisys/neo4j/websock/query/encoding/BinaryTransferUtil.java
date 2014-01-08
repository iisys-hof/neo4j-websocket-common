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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint.Basic;

import de.hofuniversity.iisys.neo4j.websock.query.IMessageHandler;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeDeflateBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.safe.TSafeDeflateJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.DeflateBsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.DeflateJsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe.BsonQueryHandler;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Utility receiving binary data and forwarding it to a message handler and
 * encoding and sending messages using the configured format.
 */
public class BinaryTransferUtil implements MessageHandler.Whole<ByteBuffer>,
    Cloneable
{
    private final Logger fLogger;

    private final Basic fRemote;
    private final IMessageHandler fHandler;

    private final List<Decoder.Binary<WebsockQuery>> fDecoders;

    private final boolean fThreadSafe;

    //primary decoder to try first
    private Decoder.Binary<WebsockQuery> fDecoder;

    private Encoder.Binary<WebsockQuery> fEncoder;

    /**
     * Creates a binary transfer utility, sending data over the given basic
     * remote, forwarding incoming binary data to the given message handler.
     * The implementations used can either be thread-safe if they need to be
     * accessed from multiple threads or optimized non-thread-safe routines.
     * None of the parameters may be null.
     *
     * @param remote remote to send data with
     * @param handler handler to forward incoming data to
     * @param tSafe whether a thread-safe implementation is needed
     */
    public BinaryTransferUtil(Basic remote, IMessageHandler handler,
        boolean tSafe)
    {
        if(remote == null)
        {
            throw new NullPointerException("websocket remote was null");
        }
        if(handler == null)
        {
            throw new NullPointerException("query handler was null");
        }

        fLogger = Logger.getLogger(this.getClass().getName());

        fRemote = remote;
        fHandler = handler;

        fThreadSafe = tSafe;

        fDecoders = getAllDecoders();

        if(fThreadSafe)
        {
            fDecoder = new TSafeBsonQueryHandler();
            fEncoder = new TSafeBsonQueryHandler();
        }
        else
        {
            fDecoder = new BsonQueryHandler();
            fEncoder = new BsonQueryHandler();
        }
    }

    private List<Decoder.Binary<WebsockQuery>> getAllDecoders()
    {
        final List<Decoder.Binary<WebsockQuery>> decoders =
            new ArrayList<Decoder.Binary<WebsockQuery>>();

        if(fThreadSafe)
        {
            decoders.add(new TSafeBsonQueryHandler());
            decoders.add(new TSafeDeflateBsonQueryHandler());
            decoders.add(new TSafeDeflateJsonQueryHandler());
        }
        else
        {
            decoders.add(new BsonQueryHandler());
            decoders.add(new DeflateBsonQueryHandler());
            decoders.add(new DeflateJsonQueryHandler());
        }

        return decoders;
    }

    @Override
    public void onMessage(ByteBuffer buffer)
    {
        fHandler.onMessage(buffer);
    }

    /**
     * Converts binary data into a WebsockQuery. First, the converter for the
     * configured format is used, then all others are tried. If all converters
     * fail, a RuntimeException is thrown.
     *
     * @param buffer binary data to convert
     * @return converted WebsockQuery
     */
    public WebsockQuery convert(final ByteBuffer buffer)
    {
        //check currently configured format first
        WebsockQuery query = null;

        //try primary decoder
        try
        {
            query = fDecoder.decode(buffer);
        }
        catch (DecodeException e)
        {
            e.printStackTrace();
            fLogger.log(Level.WARNING, "primary decoder failed to decode", e);
        }

        //try other decoders
        if(query == null)
        {
            for(Decoder.Binary<WebsockQuery> decoder : fDecoders)
            {
                try
                {
                    query = decoder.decode(buffer);
                    break;
                }
                catch (DecodeException e)
                {
                    //very probable, no action
                }
            }
        }

        //throw Exception on failure to decode
        if(query == null)
        {
            fLogger.log(Level.SEVERE, "failed to decode message");
            throw new RuntimeException("failed to decode message");
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
        ByteBuffer buffer = fEncoder.encode(message);
        fRemote.sendBinary(buffer);
    }

    /**
     * Sets the format to send messages in and the primary format to try
     * decoding incoming messages with.
     * Unknown formats are ignored and logged.
     *
     * @param format encoding to use for messages (bson/json)
     * @param compression which type of compression to use
     */
    public void setFormat(final String format, final String compression)
    {
        boolean set = false;

        if(fThreadSafe)
        {
            set = setThreadSafe(format, compression);
        }
        else
        {
            set = setNonThreadSafe(format, compression);
        }

        if(!set)
        {
            fLogger.log(Level.SEVERE, "invalid format: " + format + " / "
                + compression);
        }
    }

    private boolean setThreadSafe(final String format,
        final String compression)
    {
        boolean set = false;

        if(WebsockConstants.BSON_FORMAT.equals(format))
        {
            if(WebsockConstants.NO_COMPRESSION.equals(compression))
            {
                fEncoder = new TSafeBsonQueryHandler();
                fDecoder = new TSafeBsonQueryHandler();
                set = true;
            }
            else
            {
                fEncoder = new TSafeDeflateBsonQueryHandler(compression);
                fDecoder = new TSafeDeflateBsonQueryHandler(compression);
                set = true;
            }
        }
        else if(WebsockConstants.JSON_FORMAT.equals(format)
            && !WebsockConstants.NO_COMPRESSION.equals(compression))
        {
            fEncoder = new TSafeDeflateJsonQueryHandler(compression);
            fDecoder = new TSafeDeflateJsonQueryHandler(compression);
            set = true;
        }

        return set;
    }

    private boolean setNonThreadSafe(final String format,
        final String compression)
    {
        boolean set = false;

        if(WebsockConstants.BSON_FORMAT.equals(format))
        {
            if(WebsockConstants.NO_COMPRESSION.equals(compression))
            {
                fEncoder = new BsonQueryHandler();
                fDecoder = new BsonQueryHandler();
                set = true;
            }
            else
            {
                fEncoder = new DeflateBsonQueryHandler(compression);
                fDecoder = new DeflateBsonQueryHandler(compression);
                set = true;
            }
        }
        else if(WebsockConstants.JSON_FORMAT.equals(format)
            && !WebsockConstants.NO_COMPRESSION.equals(compression))
        {
            fEncoder = new DeflateJsonQueryHandler(compression);
            fDecoder = new DeflateJsonQueryHandler(compression);
            set = true;
        }

        return set;
    }

    /**
     * @return independent transfer utility using the same connection and
     *  current configuration
     */
    public BinaryTransferUtil clone()
    {
        return new BinaryTransferUtil(fRemote, fHandler, fThreadSafe);
    }
}
