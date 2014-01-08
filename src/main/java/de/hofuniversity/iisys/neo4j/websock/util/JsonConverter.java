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
package de.hofuniversity.iisys.neo4j.websock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Converter that can convert generic maps and lists into JSON objects and vice
 * versa.
 * Detects incoming objects that are already in the JSON format in wrappers and
 * lets them pass through.
 */
public class JsonConverter
{
    /**
     * Converts an incoming JSON object into a usable websocket query object.
     * Uses the JSON wrapper objects as map and list implementations where
     * possible.
     *
     * @param json JSON object to convert
     * @return converted websocket query
     */
    public static WebsockQuery fromJson(final JSONObject json)
        throws JSONException
    {
        final WebsockQuery query = new WebsockQuery();

        //basic attributes
        query.setId((Integer)json.opt(WebsockConstants.QUERY_ID));
        String typeString = json.opt(WebsockConstants.QUERY_TYPE).toString();
        query.setType(EQueryType.getTypeFor(typeString));

        //parameters
        final Object paramObj = json.opt(WebsockConstants.PARAMETERS);
        if(paramObj != null)
        {
            query.setParameters(new JSONMap((JSONObject) paramObj));
        }

        //payload
        Object payload = json.opt(WebsockConstants.PAYLOAD);
        if(payload != null)
        {
            if(payload instanceof JSONArray)
            {
                payload = new JSONList((JSONArray) payload);
            }
            else if(payload instanceof JSONObject)
            {
                payload = new JSONMap((JSONObject) payload);
            }

            query.setPayload(payload);
        }

        return query;
    }

    private static Map<String, Object> convertToMap(final Object o)
        throws JSONException
    {
        final JSONObject json = (JSONObject)o;
        final Map<String, Object> map = new HashMap<String, Object>();

        Iterator<?> keyIter = json.keys();
        String key = null;
        Object obj = null;
        while(keyIter.hasNext())
        {
            key = keyIter.next().toString();

            obj = json.opt(key);

            if(obj instanceof JSONArray)
            {
                obj = convertToList(obj);
            }
            else if(obj instanceof JSONObject)
            {
                obj = convertToMap(obj);
            }

            map.put(key, obj);
        }

        return map;
    }

    private static List<Object> convertToList(final Object o)
        throws JSONException
    {
        final JSONArray json = (JSONArray)o;
        final int size = json.length();
        final List<Object> list = new ArrayList<Object>(size);

        Object obj = null;
        for(int i = 0; i < size; ++i)
        {
            obj = json.opt(i);

            if(obj instanceof JSONArray)
            {
                obj = convertToList(obj);
            }
            else if(obj instanceof JSONObject)
            {
                obj = convertToMap(obj);
            }

            list.add(obj);
        }

        return list;
    }

    /**
     * Converts a generic websocket query into JSON.
     * Detects maps and lists that are already in the proper wrapped format and
     * lets them pass through.
     * The query given must not be null.
     *
     * @param query query to convert
     * @return query converted to JSON
     */
    @SuppressWarnings("unchecked")
    public static JSONObject toJson(final WebsockQuery query) throws JSONException
    {
        final JSONObject json = new JSONObject();

        //basic attributes
        json.put(WebsockConstants.QUERY_ID, query.getId());
        json.put(WebsockConstants.QUERY_TYPE, query.getType().getCode());

        //parameters
        Map<String, Object> params = query.getParameters();
        if(params != null && !params.isEmpty())
        {
            if(params instanceof JSONMap)
            {
                json.put(WebsockConstants.PARAMETERS,
                    ((JSONMap)params).getJson());
            }
            else
            {
                json.put(WebsockConstants.PARAMETERS, convertFromMap(params));
            }
        }

        //payload
        Object payload = query.getPayload();
        if(payload != null)
        {
            if(payload instanceof Map)
            {
                if(payload instanceof JSONMap)
                {
                    payload = ((JSONMap)payload).getJson();
                }
                else
                {
                    payload = convertFromMap((Map<String, ?>)payload);
                }
            }
            else if(payload instanceof List)
            {
                if(payload instanceof JSONList)
                {
                    payload = ((JSONList)payload).getJson();
                }
                else
                {
                    payload = convertFromList((List<?>)payload);
                }
            }

            json.put(WebsockConstants.PAYLOAD, payload);
        }

        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject convertFromMap(final Map<String, ?> map)
        throws JSONException
    {
        final JSONObject json = new JSONObject(map.size());

        Object o = null;
        for(Entry<String, ?> mapE : map.entrySet())
        {
            o = mapE.getValue();

            if(o instanceof Map)
            {
                if(o instanceof JSONMap)
                {
                    o = ((JSONMap)o).getJson();
                }
                else
                {
                    o = convertFromMap((Map<String, ?>)o);
                }
            }
            else if(o instanceof List)
            {
                if(o instanceof JSONList)
                {
                    o = ((JSONList)o).getJson();
                }
                else
                {
                    o = convertFromList((List<?>)o);
                }
            }

            json.put(mapE.getKey(), o);
        }

        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray convertFromList(final List<?> list)
        throws JSONException
    {
        final JSONArray json = new JSONArray();

        for(Object o : list)
        {
            if(o instanceof Map)
            {
                if(o instanceof JSONMap)
                {
                    o = ((JSONMap)o).getJson();
                }
                else
                {
                    o = convertFromMap((Map<String, ?>)o);
                }
            }
            else if(o instanceof List)
            {
                if(o instanceof JSONList)
                {
                    o = ((JSONList)o).getJson();
                }
                else
                {
                    o = convertFromList((List<?>)o);
                }
            }

            json.put(o);
        }

        return json;
    }
}
