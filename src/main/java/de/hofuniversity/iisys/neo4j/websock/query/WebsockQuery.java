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
package de.hofuniversity.iisys.neo4j.websock.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic class for all queries sent over the websocket connection, having at
 * least a type and an ID for response identification.
 * Optionally, there can be a payload and named parameters, each being either
 * of primitive types, maps or lists.
 */
public class WebsockQuery
{
    private int fId;
    private EQueryType fType;

    private Object fPayload;
    private Map<String, Object> fParameters;

    /**
     * Creates an empty ping type query with ID 0.
     */
    public WebsockQuery()
    {
        this(0, EQueryType.PING);
    }

    /**
     * Creates an empty query with ID 0 and the given type.
     * The type may not be null.
     *
     * @param type query type
     */
    public WebsockQuery(EQueryType type)
    {
        this(0, type);
    }

    /**
     * Creates a query with the given ID and type.
     * The type may not be null.
     *
     * @param id query ID
     * @param type query type
     */
    public WebsockQuery(int id, EQueryType type)
    {
        if(type == null)
        {
            throw new RuntimeException("query type was null");
        }

        fId = id;
        fType = type;

        fParameters = new HashMap<String, Object>();
    }

    /**
     * @return internal parameter map
     */
    public Map<String, Object> getParameters()
    {
        return fParameters;
    }

    /**
     * Sets the internal parameter map, replacing the old one.
     * The map given may not be null.
     *
     * @param parameters new internal parameter map
     */
    public void setParameters(Map<String, Object> parameters)
    {
        if(parameters == null)
        {
            throw new NullPointerException("parameter map was null");
        }

        fParameters = parameters;
    }

    /**
     * @param key name of the parameter to retrieve
     * @return value of the parameter or null
     */
    public Object getParameter(String key)
    {
        return fParameters.get(key);
    }

    /**
     * Sets a named parameter to a certain value. Only primitive types, maps
     * and lists are valid values.
     *
     * @param key name of the parameter to set
     * @param value value of the parameter to set
     */
    public void setParameter(String key, Object value)
    {
        fParameters.put(key, value);
    }

    /**
     * @return ID of the websocket query
     */
    public int getId()
    {
        return fId;
    }

    /**
     * @param fId new ID for the query
     */
    public void setId(int id)
    {
        fId = id;
    }

    /**
     * @return the query's type
     */
    public EQueryType getType()
    {
        return fType;
    }

    /**
     * Sets a new type for this query which may not be null.
     *
     * @param type new type for the query
     */
    public void setType(EQueryType type)
    {
        if(type == null)
        {
            throw new NullPointerException("query type was null");
        }

        this.fType = type;
    }

    /**
     * @return payload of the query or null
     */
    public Object getPayload()
    {
        return fPayload;
    }

    /**
     * Sets the query's payload.
     * Only primitive types, maps and lists are valid values.
     *
     * @param payload new payload for the query
     */
    public void setPayload(Object payload)
    {
        this.fPayload = payload;
    }
}
