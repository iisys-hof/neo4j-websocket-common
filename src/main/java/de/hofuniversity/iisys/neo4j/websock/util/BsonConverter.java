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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import de.hofuniversity.iisys.neo4j.websock.query.EQueryType;
import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Converter that can convert generic maps and lists into BSON objects and vice
 * versa.
 * Detects incoming objects that are already in the BSON format and lets them
 * pass through.
 */
public class BsonConverter
{
    /**
     * Converts an incoming BSON object into a usable websocket query object.
     * Uses the native BSON objects as map and list implementations where
     * possible.
     * The object given must not be null.
     *
     * @param bson BSON object to convert
     * @return converted websocket query
     */
    @SuppressWarnings("unchecked")
    public static WebsockQuery fromBson(final BSONObject bson)
    {
        final WebsockQuery query = new WebsockQuery();

        //basic attributes
        query.setId((Integer)bson.get(WebsockConstants.QUERY_ID));
        String typeString = bson.get(WebsockConstants.QUERY_TYPE).toString();
        query.setType(EQueryType.getTypeFor(typeString));

        //parameters
        final Object paramObj = bson.get(WebsockConstants.PARAMETERS);
        if(paramObj != null)
        {
            query.setParameters((Map<String, Object>)paramObj);
        }

        //payload
        final Object payload = bson.get(WebsockConstants.PAYLOAD);
        if(payload != null)
        {
            query.setPayload(payload);
        }

        return query;
    }

    private static Map<String, Object> convertToMap(final Object o)
    {
        final BSONObject bson = (BSONObject)o;
        final Map<String, Object> map = new HashMap<String, Object>();

        Object obj = null;
        for(String key : bson.keySet())
        {
            obj = bson.get(key);

            if(obj instanceof BasicBSONList)
            {
                obj = convertToList(obj);
            }
            else if(obj instanceof BSONObject)
            {
                obj = convertToMap(obj);
            }

            map.put(key, obj);
        }

        return map;
    }

    private static List<Object> convertToList(final Object o)
    {
        final BasicBSONList bson = (BasicBSONList)o;
        final List<Object> list = new ArrayList<Object>(bson.size());

        for(Object obj : bson)
        {
            if(obj instanceof BasicBSONList)
            {
                obj = convertToList(obj);
            }
            else if(obj instanceof BSONObject)
            {
                obj = convertToMap(obj);
            }

            list.add(obj);
        }

        return list;
    }

    /**
     * Converts a generic websocket query into BSON.
     * Detects maps and lists that are already in the proper format and lets
     * them pass through.
     * The query given must not be null.
     *
     * @param query query to convert
     * @return query converted to BSON
     */
    @SuppressWarnings("unchecked")
    public static BSONObject toBson(final WebsockQuery query)
    {
        final BSONObject bson = new BasicBSONObject();

        //basic attributes
        bson.put(WebsockConstants.QUERY_ID, query.getId());
        bson.put(WebsockConstants.QUERY_TYPE, query.getType().getCode());

        //parameters
        final Map<String, Object> params = query.getParameters();
        if(params != null && !params.isEmpty())
        {
            if(!(params instanceof BasicBSONObject))
            {
                bson.put(WebsockConstants.PARAMETERS, convertFromMap(params));
            }
            else
            {
                bson.put(WebsockConstants.PARAMETERS, params);
            }
        }

        //payload
        Object payload = query.getPayload();
        if(payload != null)
        {
            if(!(payload instanceof BasicBSONObject)
                && payload instanceof Map)
            {
                payload = convertFromMap((Map<String, ?>)payload);
            }
            else if(!(payload instanceof BasicBSONList)
                && payload instanceof List)
            {
                payload = convertFromList((List<?>)payload);
            }

            bson.put(WebsockConstants.PAYLOAD, payload);
        }

        return bson;
    }

    @SuppressWarnings("unchecked")
    private static BSONObject convertFromMap(final Map<String, ?> map)
    {
        final BasicBSONObject bson = new BasicBSONObject(map.size());

        Object o = null;
        for(Entry<String, ?> mapE : map.entrySet())
        {
            o = mapE.getValue();

            if(!(o instanceof BasicBSONObject)
                && o instanceof Map)
            {
                o = convertFromMap((Map<String, ?>)o);
            }
            else if(!(o instanceof BasicBSONList)
                && o instanceof List)
            {
                o = convertFromList((List<?>)o);
            }

            bson.put(mapE.getKey(), o);
        }

        return bson;
    }

    @SuppressWarnings("unchecked")
    private static BSONObject convertFromList(final List<?> list)
    {
        final BasicBSONList bson = new BasicBSONList();

        for(Object o : list)
        {
            if(!(o instanceof BasicBSONObject)
                && o instanceof Map)
            {
                o = convertFromMap((Map<String, ?>)o);
            }
            else if(!(o instanceof BasicBSONList)
                && o instanceof List)
            {
                o = convertFromList((List<?>)o);
            }

            bson.add(o);
        }

        return bson;
    }
}
