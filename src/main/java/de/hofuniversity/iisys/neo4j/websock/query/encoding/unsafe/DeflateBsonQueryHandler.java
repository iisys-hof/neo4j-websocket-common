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
package de.hofuniversity.iisys.neo4j.websock.query.encoding.unsafe;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;
import de.hofuniversity.iisys.neo4j.websock.util.BsonConverter;

/**
 * Query handler implementation encoding and decoding WebsockQueries as BSON
 * compressed using the deflate algorithm.
 * When decoding, uses the provided BSON map and list classes.
 * Ideally, maps and lists in queries to be encoded are already BSON objects.
 * Optimized non-thread-safe version.
 */
public class DeflateBsonQueryHandler implements Encoder.Binary<WebsockQuery>,
    Decoder.Binary<WebsockQuery>
{
    private static final int DEFAULT_COMPRESSION_LEVEL = Deflater.BEST_SPEED;
    private static final int BUFFER_SIZE = 1024*1024;

    private final BSONEncoder fEncoder;
    private final BSONDecoder fDecoder;

    final byte[] fBuffer = new byte[BUFFER_SIZE];
    final List<byte[]> fBuffers = new LinkedList<byte[]>();

    final Inflater fInflater;

    private final Logger fLogger;
    private final boolean fDebug;
    private final int fCompression;

    private long fTotalBytesIn, fTotalBytesOut;

    public DeflateBsonQueryHandler()
    {
        fEncoder = new BasicBSONEncoder();
        fDecoder = new BasicBSONDecoder();

        fInflater = new Inflater(true);

        fLogger = Logger.getLogger(this.getClass().getName());
        fDebug = (fLogger.getLevel() == Level.FINEST);
        fCompression = DEFAULT_COMPRESSION_LEVEL;
    }

    public DeflateBsonQueryHandler(final String compression)
    {
        fEncoder = new BasicBSONEncoder();
        fDecoder = new BasicBSONDecoder();

        fInflater = new Inflater(true);

        fLogger = Logger.getLogger(this.getClass().getName());
        fDebug = (fLogger.getLevel() == Level.FINEST);

        int tmpComp = DEFAULT_COMPRESSION_LEVEL;

        if(WebsockConstants.FASTEST_COMPRESSION.equals(compression))
        {
            tmpComp = Deflater.BEST_SPEED;
        }
        else if(WebsockConstants.BEST_COMPRESSION.equals(compression))
        {
            tmpComp = Deflater.BEST_COMPRESSION;
        }
        else
        {
            fLogger.log(Level.WARNING, "unknown compression level '"
                + compression + "'; using default.");
        }

        fCompression = tmpComp;
    }

    @Override
    public void destroy()
    {

    }

    @Override
    public void init(EndpointConfig config)
    {

    }

    private ByteBuffer fuse(final int length)
    {
        //fuses the buffers into a single array of the target length
        final ByteBuffer bb = ByteBuffer.allocate(length);

        for(byte[] buffer : fBuffers)
        {
            if(buffer.length > length - bb.position())
            {
                bb.put(buffer, 0, length - bb.position());
            }
            else
            {
                bb.put(buffer);
            }
        }

        //important
        bb.flip();
        fBuffers.clear();

        return bb;
    }

    @Override
    public ByteBuffer encode(final WebsockQuery query) throws EncodeException
    {
        final BSONObject obj = BsonConverter.toBson(query);

        //convert to BSON
        //encoder is not thread safe
        byte[] data = fEncoder.encode(obj);

        //compress
        final Deflater deflater = new Deflater(fCompression, true);
        deflater.setInput(data);
        deflater.finish();

        int totalSize = 0;

        int read = deflater.deflate(fBuffer, 0, BUFFER_SIZE,
            Deflater.SYNC_FLUSH);
        while(true)
        {
            totalSize += read;

            if(deflater.finished())
            {
                //if finished, directly add buffer
                fBuffers.add(fBuffer);
                break;
            }
            else
            {
                //make a copy, reuse buffer
                fBuffers.add(Arrays.copyOf(fBuffer, read));
                read = deflater.deflate(fBuffer, 0, BUFFER_SIZE,
                    Deflater.SYNC_FLUSH);
            }
        }

        deflater.end();

        //log total data converted
        if(fDebug)
        {
            fTotalBytesOut += totalSize;
            fLogger.log(Level.FINEST, "encoded, compressed BSON message: "
                + totalSize + " bytes\n"
                + "total bytes sent: " + fTotalBytesOut);
        }

        return fuse(totalSize);
    }

    @Override
    public WebsockQuery decode(ByteBuffer buff) throws DecodeException
    {
        WebsockQuery query = null;

        try
        {
            //decompress
            final byte[] incoming = buff.array();
            fInflater.setInput(incoming);

            int totalSize = 0;

            int read = fInflater.inflate(fBuffer);
            while(read > 0)
            {
                totalSize += read;
                fBuffers.add(Arrays.copyOf(fBuffer, read));
                read = fInflater.inflate(fBuffer);
            }
            //TODO: directly add final slice?
            //      showed negative impact on performance

            final byte[] data = fuse(totalSize).array();
            final BSONObject obj = fDecoder.readObject(data);

            if(fDebug)
            {
                fTotalBytesIn += incoming.length;
                fLogger.log(Level.FINEST, "received compressed BSON message: "
                    + incoming.length + " bytes\n"
                    + "total bytes received: " + fTotalBytesIn);
            }

            query = BsonConverter.fromBson(obj);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DecodeException(buff, "failed to decode compressed BSON",
                e);
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
            //decompress
            fInflater.setInput(buff.array());

            int totalSize = 0;

            int read = fInflater.inflate(fBuffer);
            while(read > 0)
            {
                totalSize += read;
                fBuffers.add(Arrays.copyOf(fBuffer, read));
                read = fInflater.inflate(fBuffer);
            }
            //TODO: directly add final slice?
            //      showed negative impact on performance

            final byte[] data = fuse(totalSize).array();
            fDecoder.readObject(data);
        }
        catch(Exception e)
        {
            valid = false;
        }

        return valid;
    }
}
