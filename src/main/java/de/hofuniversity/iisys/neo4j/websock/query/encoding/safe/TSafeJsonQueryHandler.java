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
public class TSafeJsonQueryHandler implements Encoder.Text<WebsockQuery>,
    Decoder.Text<WebsockQuery>
{
    private final Logger fLogger;
    private final boolean fDebug;

    private long fTotalBytesIn, fTotalBytesOut;

    public TSafeJsonQueryHandler()
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
            final JSONObject obj = JsonConverter.toJson(query);
            result = obj.toString();

            if(fDebug)
            {
                fTotalBytesOut += result.getBytes().length;
                fLogger.log(Level.FINEST, "encoded JSON message: "
                    + result.getBytes().length + " bytes\n"
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
    public WebsockQuery decode(String arg0) throws DecodeException
    {
        WebsockQuery query = null;

        try
        {
            final JSONObject obj = new JSONObject(arg0);

            if(fDebug)
            {
                fTotalBytesIn += arg0.getBytes().length;
                fLogger.log(Level.FINEST, "received JSON message: "
                    + arg0.getBytes().length + " bytes\n"
                    + "total bytes received: " + fTotalBytesIn);
            }

            query = JsonConverter.fromJson(obj);
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
