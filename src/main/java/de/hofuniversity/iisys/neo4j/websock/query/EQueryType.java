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
package de.hofuniversity.iisys.neo4j.websock.query;

import java.util.HashMap;
import java.util.Map;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Enumeration of general types of queries, each with a code for size-efficient
 * encoding and decoding.
 */
public enum EQueryType
{
    PING(WebsockConstants.PING),
    PONG(WebsockConstants.PONG),

    /**
     * Either a successful query with no return value or another successfully
     * executed command.
     */
    SUCCESS(WebsockConstants.SUCCESS),
    ERROR(WebsockConstants.ERROR),

    /**
     * If activated, used for initial login at the server.
     */
    AUTHENTICATION(WebsockConstants.AUTHENTICATION),

    /**
     * Remote configuration message, changes settings on server side for own
     * connection.
     */
    CONFIGURATION(WebsockConstants.CONFIGURATION),

    /**
     * Directly execute a Cypher statement.
     */
    DIRECT_CYPHER(WebsockConstants.DIRECT_CYPHER),

    /**
     * Call a stored Cypher statement or native routine with a given name and
     * using parameters on the server.
     */
    PROCEDURE_CALL(WebsockConstants.STORED_PROCEDURE),

    /**
     * Deletes the stored procedure with the given name.
     */
    DELETE_PROCEDURE(WebsockConstants.DELETE_PROCEDURE),

    /**
     * Stores a provided Cypher statement as a stored procedure under the given
     * name.
     */
    STORE_PROCEDURE(WebsockConstants.STORE_PROCEDURE),

    /**
     * Result values from a called procedure or executed statement.
     */
    RESULT(WebsockConstants.RESULT);

    //map of type codes for easy decoding
    private static final Map<String, EQueryType> fTypesByCode
        = new HashMap<String, EQueryType>();

    static
    {
        for(EQueryType type : EQueryType.values())
        {
            fTypesByCode.put(type.getCode(), type);
        }
    }

    private final String fCode;

    /**
     * @param code received query type code
     * @return enumeration constant for the code given or null
     */
    public static EQueryType getTypeFor(String code)
    {
        return fTypesByCode.get(code);
    }

    private EQueryType(String code)
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
