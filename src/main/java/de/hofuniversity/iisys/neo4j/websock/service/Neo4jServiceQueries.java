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
package de.hofuniversity.iisys.neo4j.websock.service;

/**
 * Collection of constants, naming parameters, methods and query names for
 * the server's Neo4j-related service routines.
 */
public class Neo4jServiceQueries
{
    //ID service
    public static final String TYPE = "type";

    public static final String GET_UID_QUERY = "getUniqueId";
    public static final String GET_UID_METHOD = "requestId";

    //index service
    public static final String INDEX = "index";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String NODE_ID = "nodeId";

    public static final String CREATE_INDEX_ENTRY_QUERY = "newIndexEntry";
    public static final String CREATE_INDEX_ENTRY_METHOD = "createIndexEntry";

    public static final String DELETE_INDEX_ENTRY_QUERY = "deleteIndexEntry";
    public static final String DELETE_INDEX_ENTRY_METHOD = "deleteIndexEntry";

    public static final String CLEAR_INDEX_QUERY = "clearIndex";
    public static final String CLEAR_INDEX_METHOD = "clearIndex";
}
