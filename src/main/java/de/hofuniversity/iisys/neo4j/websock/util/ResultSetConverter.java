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
package de.hofuniversity.iisys.neo4j.websock.util;

import java.util.Map;

import de.hofuniversity.iisys.neo4j.websock.result.AResultSet;
import de.hofuniversity.iisys.neo4j.websock.result.EResultType;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
import de.hofuniversity.iisys.neo4j.websock.result.TableResult;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Converter, transforming result sets into transferable maps and vice versa.
 */
public class ResultSetConverter
{
    /**
     * Converts the given result set by setting the appropriate values in the
     * given map.
     * Both parameters must not be null.
     *
     * @param result result set to convert
     * @param map map to base the result on
     * @return converted result set
     */
    public static Map<String, Object> toMap(final AResultSet<?> result,
        final Map<String, Object> map)
    {
        final EResultType type = result.getType();
        map.put(WebsockConstants.RESULT_TYPE, type.getCode());

        if(result.getFirst() > 0)
        {
            map.put(WebsockConstants.SUBSET_START, result.getFirst());
        }

        if(result.getMax() > 0)
        {
            map.put(WebsockConstants.SUBSET_SIZE, result.getMax());
        }

        if(type != EResultType.SINGLE
            && result.getTotal() >= 0)
        {
            map.put(WebsockConstants.TOTAL_RESULTS, result.getTotal());
        }

        map.put(WebsockConstants.RESULT, result.getResults());

        if(type == EResultType.TABLE)
        {
            map.put(WebsockConstants.TABLE_COLUMNS,
                ((TableResult)result).getColumns());
        }

        return map;
    }

    /**
     * Converts a received map into a result set.
     * The given map must not be null.
     *
     * @param map map to convert
     * @return converted result set
     */
    public static AResultSet<?> toResultSet(final Map<String, Object> map)
    {
        if(map == null)
        {
            return null;
        }

        AResultSet<?> set = null;

        String typeString = map.get(WebsockConstants.RESULT_TYPE).toString();
        EResultType type = EResultType.getTypeFor(typeString);

        switch(type)
        {
            case LIST:
                set = new ListResult(map);
                break;

            case SINGLE:
                set = new SingleResult(map, true);
                break;

            case TABLE:
                set = new TableResult(map);
                break;
        }


        return set;
    }
}
