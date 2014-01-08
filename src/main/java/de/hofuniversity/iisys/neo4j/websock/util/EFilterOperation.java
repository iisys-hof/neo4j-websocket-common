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

import java.util.HashMap;
import java.util.Map;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Enumeration of filter operations, each with a code for size-efficient
 * encoding and decoding.
 */
public enum EFilterOperation
{
    CONTAINS(WebsockConstants.CONTAINS_FILTER),
    EQUALS(WebsockConstants.EQUALS_FILTER),
    STARTS_WITH(WebsockConstants.STARTS_WITH_FILTER),
    ENDS_WITH(WebsockConstants.ENDS_WITH_FILTER),
    HAS_PROPERTY(WebsockConstants.HAS_PROPERTY_FILTER);

    //map of type codes for easy decoding
    private static final Map<String, EFilterOperation> fTypesByCode
        = new HashMap<String, EFilterOperation>();

    static
    {
        for(EFilterOperation type : EFilterOperation.values())
        {
            fTypesByCode.put(type.getCode(), type);
        }
    }

    /**
     * @param code received query type code
     * @return enumeration constant for the code given or null
     */
    public static EFilterOperation getTypeFor(String code)
    {
        return fTypesByCode.get(code);
    }

    private final String fCode;

    private EFilterOperation(String code)
    {
        fCode = code;
    }

    /**
     * @return code to encode this type with
     */
    public String getCode()
    {
        return fCode;
    }
}
