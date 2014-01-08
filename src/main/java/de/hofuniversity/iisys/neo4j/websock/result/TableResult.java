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

import java.util.List;
import java.util.Map;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Result set containing a table of objects in one or more named columns.
 * The columns' values are at the same indices as their names in the
 * column list name.
 */
public class TableResult extends AResultSet<List<List<Object>>>
{
    private final List<String> fColumns;

    /**
     * Creates a table result with the given column names and table values.
     * The map of columns may not be null.
     *
     * @param columns ordered list of column names
     * @param results list of table rows
     */
    public TableResult(List<String> columns, List<List<Object>> results)
    {
        super(EResultType.TABLE, results);

        if(columns == null)
        {
            throw new RuntimeException("list of column names was null");
        }

        fColumns = columns;
    }

    /**
     * Creates a table result, decoding it from the given map if possible.
     *
     * @param map received result set map
     */
    @SuppressWarnings("unchecked")
    public TableResult(Map<String, Object> map)
    {
        super(map);
        fColumns = (List<String>)map.get(WebsockConstants.TABLE_COLUMNS);
    }

    @Override
    public int getSize()
    {
        return fResults.size();
    }

    /**
     * @return ordered list of columns
     */
    public List<String> getColumns()
    {
        return fColumns;
    }

    /**
     * @param columns new column names
     */
    public void setColumns(List<String> columns)
    {
        fColumns.clear();
        fColumns.addAll(columns);
    }

    /**
     * Returns the index of a named column in all rows or -1 if it doesn't
     * exist.
     *
     * @param column name of the column
     * @return index of the column or null
     */
    public int getColumnIndex(String column)
    {
        return fColumns.indexOf(column);
    }
}
