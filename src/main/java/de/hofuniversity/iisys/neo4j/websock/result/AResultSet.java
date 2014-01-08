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
package de.hofuniversity.iisys.neo4j.websock.result;

import java.util.Map;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Abstract class for a potentially paginated result, containing a result
 * value, the starting index, maximum results that were to be retrieved and
 * the total number of results that could be retrieved.
 *
 * @param <T> type of the result value
 */
public abstract class AResultSet<T>
{
    /**
     * Object containing the results returned.
     */
    protected final T fResults;

    private final EResultType fType;

    private int fFirst = 0;
    private int fMax = -1;
    private int fTotal = -1;

    /**
     * Creates a result set with the given type, containing the given result
     * object.
     * The type may not be null.
     *
     * @param type type of the result set
     * @param results object containing the results returned
     */
    public AResultSet(EResultType type, T results)
    {
        if(type == null)
        {
            throw new RuntimeException("result type was null");
        }

        fResults = results;
        fType = type;
    }

    /**
     * Creates a result set, decoding it from a received map.
     *
     * @param map
     */
    @SuppressWarnings("unchecked")
    public AResultSet(Map<String, Object> map)
    {
        String typeCode = map.get(WebsockConstants.RESULT_TYPE).toString();
        fType = EResultType.getTypeFor(typeCode);

        fResults = (T) map.get(WebsockConstants.RESULT);

        Object first = map.get(WebsockConstants.SUBSET_START);
        if(first != null)
        {
            fFirst = (Integer)first;
        }

        Object max = map.get(WebsockConstants.SUBSET_SIZE);
        if(max != null)
        {
            fMax = (Integer)max;
        }

        Object total = map.get(WebsockConstants.TOTAL_RESULTS);
        if(total != null)
        {
            fTotal = (Integer)total;
        }
    }

    /**
     * @return index of the first result (of all results) retrieved
     */
    public int getFirst()
    {
        return fFirst;
    }

    /**
     * @param first index of the first result retrieved
     */
    public void setFirst(int first)
    {
        fFirst = first;
    }

    /**
     * @return maximum number of results that were to be retrieved
     */
    public int getMax()
    {
        return fMax;
    }

    /**
     * @param max maximum number of results that were to be retrieved
     */
    public void setMax(int max)
    {
        fMax = max;
    }

    /**
     * @return total number of results that could be retrieved
     */
    public int getTotal()
    {
        return fTotal;
    }

    /**
     * @param total total number of results that could be retrieved
     */
    public void setTotal(int total)
    {
        fTotal = total;
    }

    /**
     * @return type of this result
     */
    public EResultType getType()
    {
        return fType;
    }

    /**
     * @return result object or null
     */
    public T getResults()
    {
        return fResults;
    }

    /**
     * @return number of results contained in the result set
     */
    public abstract int getSize();
}
