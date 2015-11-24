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
package de.hofuniversity.iisys.neo4j.websock.query.encoding.safe;

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

import org.json.JSONException;
import org.json.JSONObject;

import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;
import de.hofuniversity.iisys.neo4j.websock.util.JsonConverter;

/**
 * Query handler implementation encoding and decoding WebsockQueries as JSON.
 * When decoding, uses the JSON map and list wrapper classes.
 * Ideally, maps and lists in queries to be encoded are already JSON objects
 * in wrappers.
 */
public class TSafeDeflateJsonQueryHandler implements Encoder.Binary<WebsockQuery>,
    Decoder.Binary<WebsockQuery>
{
    private static final int DEFAULT_COMPRESSION_LEVEL = Deflater.BEST_SPEED;
    private static final int BUFFER_SIZE = 1024;

    private final Logger fLogger;
    private final boolean fDebug;
    private final int fCompression;

    private long fTotalBytesIn, fTotalBytesOut;

    public TSafeDeflateJsonQueryHandler()
    {
        fLogger = Logger.getLogger(this.getClass().getName());
        fDebug = (fLogger.getLevel() == Level.FINEST);
        fCompression = DEFAULT_COMPRESSION_LEVEL;
    }

    public TSafeDeflateJsonQueryHandler(final String compression)
    {
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
    public void init(EndpointConfig arg0)
    {

    }

    private ByteBuffer fuse(List<byte[]> buffers, final int length)
    {
        //fuses the buffers into a single array of the target length
        final ByteBuffer bb = ByteBuffer.allocate(length);

        for(byte[] buffer : buffers)
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

        return bb;
    }

    @Override
    public ByteBuffer encode(final WebsockQuery query) throws EncodeException
    {
        ByteBuffer result = null;

        try
        {
            final JSONObject obj = JsonConverter.toJson(query);
            byte[] data = obj.toString().getBytes();

            //compress
            final Deflater deflater = new Deflater(fCompression, true);
            deflater.setInput(data);

            int read = 0;
            int totalSize = 0;
            final List<byte[]> buffers = new LinkedList<byte[]>();

            final byte[] buffer = new byte[BUFFER_SIZE];
            read = deflater.deflate(buffer, 0, BUFFER_SIZE, Deflater.SYNC_FLUSH);
            while(read > 0)
            {
                totalSize += read;
                buffers.add(Arrays.copyOf(buffer, read));
                read = deflater.deflate(buffer, 0, BUFFER_SIZE, Deflater.SYNC_FLUSH);
            }

            result = fuse(buffers, totalSize);

            if(fDebug)
            {
                fTotalBytesOut += totalSize;
                fLogger.log(Level.FINEST, "encoded compressed JSON message: "
                    + totalSize + " bytes\n"
                    + "total bytes sent: " + fTotalBytesOut);
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
    public WebsockQuery decode(ByteBuffer buff) throws DecodeException
    {
        WebsockQuery query = null;

        try
        {
            //decompress
            final byte[] incoming = buff.array();
            final Inflater inflater = new Inflater(true);
            inflater.setInput(incoming);

            int read = 0;
            int totalSize = 0;
            final List<byte[]> buffers = new LinkedList<byte[]>();

            final byte[] buffer = new byte[BUFFER_SIZE];
            read = inflater.inflate(buffer);
            while(read > 0)
            {
                totalSize += read;
                buffers.add(Arrays.copyOf(buffer, read));
                read = inflater.inflate(buffer);
            }

            final byte[] data = fuse(buffers, totalSize).array();
            final JSONObject obj = new JSONObject(new String(data));

            if(fDebug)
            {
                fTotalBytesIn += incoming.length;
                fLogger.log(Level.FINEST, "received compressed JSON message: "
                    + incoming.length + " bytes\n"
                    + "total bytes received: " + fTotalBytesIn);
            }

            query = JsonConverter.fromJson(obj);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DecodeException(buff, "failed to decode JSON", e);
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
            final Inflater inflater = new Inflater(true);
            inflater.setInput(buff.array());

            int read = 0;
            int totalSize = 0;
            final List<byte[]> buffers = new LinkedList<byte[]>();

            byte[] buffer = new byte[BUFFER_SIZE];
            read = inflater.inflate(buffer);
            while(read > 0)
            {
                totalSize += read;
                buffers.add(buffer);
                buffer = new byte[BUFFER_SIZE];
                read = inflater.inflate(buffer);
            }

            final byte[] data = fuse(buffers, totalSize).array();
            new JSONObject(new String(data));
        }
        catch (Exception e)
        {
            valid = false;
        }

        return valid;
    }
}
