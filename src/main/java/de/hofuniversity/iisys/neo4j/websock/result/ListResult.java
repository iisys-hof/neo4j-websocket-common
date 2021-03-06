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

import java.util.List;
import java.util.Map;

/**
 * Result set containing a list of objects.
 */
public class ListResult extends AResultSet<List<?>>
{
    /**
     * Creates a result set containing the given list as its results.
     *
     * @param list list of retrieved objects
     */
    public ListResult(List<?> list)
    {
        super(EResultType.LIST, list);
    }

    /**
     * Creates a list result, decoding it from the given map if possible.
     *
     * @param map received result set map
     */
    public ListResult(Map<String, Object> map)
    {
        super(map);
    }

    @Override
    public int getSize()
    {
        return fResults.size();
    }
}
