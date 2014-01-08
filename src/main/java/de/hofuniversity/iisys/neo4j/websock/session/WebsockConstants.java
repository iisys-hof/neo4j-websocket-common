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
package de.hofuniversity.iisys.neo4j.websock.session;

/**
 * Collection of constants denoting configuration options and the communication
 * protocol.
 */
public class WebsockConstants
{
    //session options

    //options to choose which format messages will have
    public static final String FORMAT_OPTION = "format";
    public static final String JSON_FORMAT = "json";
    public static final String BSON_FORMAT = "bson";

    //whether communication should be compressed
    public static final String COMPRESS_OPTION = "compression";

    public static final String NO_COMPRESSION = "none";
    public static final String BEST_COMPRESSION = "best";
    public static final String FASTEST_COMPRESSION = "fastest";

    //whether streaming is supported
    public static final String STREAM_OPTION = "streaming";

    //whether results should be batched
    public static final String BATCH = "batching";

    //batch flush size
    public static final String BATCH_SIZE = "batch_size";

    //default realm
    public static final String DEFAULT_REALM = "default";


    //protocol

    //message type
    public static final String QUERY_ID = "q";
    public static final String QUERY_TYPE = "t";
    public static final String PAYLOAD = "l";

    //connection test
    public static final String PING = "i";
    public static final String PONG = "o";

    //success
    public static final String SUCCESS = "y";

    //error
    public static final String ERROR = "err";

    //authentication
    public static final String AUTHENTICATION = "a";
    public static final String USERNAME = "u";
    public static final String PASSWORD = "p";

    //session configuration
    public static final String CONFIGURATION = "c";

    //requests
    public static final String DIRECT_CYPHER = "d";

    public static final String STORED_PROCEDURE = "s";

    public static final String PARAMETERS = "p";
    public static final String RESULT = "r";

    //stored procedures
    public static final String DELETE_PROCEDURE = "e";
    public static final String STORE_PROCEDURE = "n";
    public static final String PROCEDURE_NAME = "n";

    //properties

    //result
    public static final String RESULT_TYPE = "rt";
    public static final String SINGLE_RESULT = "s";
    public static final String LIST_RESULT = "l";
    public static final String TABLE_RESULT = "t";
    public static final String TABLE_COLUMNS = "col";

    //subset query/result
    public static final String SUBSET_START = "s_s";
    public static final String SUBSET_SIZE = "s_n";
    public static final String TOTAL_RESULTS = "tot";

    //query options
    public static final String SORT_ORDER = "so";
    public static final String SORT_FIELD = "sf";
    public static final String ASCENDING = "a";
    public static final String DESCENDING = "d";

    public static final String FILTER_FIELD = "ff";
    public static final String FILTER_VALUE = "fv";
    public static final String FILTER_OPERATION = "fo";
    public static final String CONTAINS_FILTER = "c";
    public static final String EQUALS_FILTER = "e";
    public static final String STARTS_WITH_FILTER = "sw";
    public static final String ENDS_WITH_FILTER = "ew";
    public static final String HAS_PROPERTY_FILTER = "h";

    //pass the options map to a procedure
    public static final String OPTIONS_MAP = "$OPTIONS";
}
