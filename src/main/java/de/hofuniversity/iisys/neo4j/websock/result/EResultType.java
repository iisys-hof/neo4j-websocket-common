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
package de.hofuniversity.iisys.neo4j.websock.result;

import java.util.HashMap;
import java.util.Map;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Enumeration of all result types, each with a code for size-efficient
 * encoding and decoding.
 */
public enum EResultType
{
    /**
     * Single result, a single map result.
     */
    SINGLE(WebsockConstants.SINGLE_RESULT),

    /**
     * List result, list of maps or primitive objects.
     */
    LIST(WebsockConstants.LIST_RESULT),

    /**
     * Table result with one or more named columns.
     */
    TABLE(WebsockConstants.TABLE_RESULT);

  //map of type codes for easy decoding
    private static final Map<String, EResultType> fTypesByCode
        = new HashMap<String, EResultType>();

    static
    {
        for(EResultType type : EResultType.values())
        {
            fTypesByCode.put(type.getCode(), type);
        }
    }

    /**
     * @param code received query type code
     * @return enumeration constant for the code given or null
     */
    public static EResultType getTypeFor(String code)
    {
        return fTypesByCode.get(code);
    }

    private final String fCode;

    private EResultType(String code)
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
