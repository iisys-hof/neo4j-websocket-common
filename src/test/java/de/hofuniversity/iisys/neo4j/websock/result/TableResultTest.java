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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Tests for the implementation of a table result.
 */
public class TableResultTest
{
    /**
     * Tests the instantiation of a new table result.
     */
    @Test
    public void newTableTest()
    {
        List<String> columns = new ArrayList<String>();
        columns.add("column 1");
        columns.add("column 2");
        columns.add("column 3");

        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row1 = new ArrayList<Object>();
        row1.add("value 1");
        row1.add("value 2");
        row1.add("value 3");

        List<Object> row2 = new ArrayList<Object>(row1);
        List<Object> row3 = new ArrayList<Object>(row1);
        List<Object> row4 = new ArrayList<Object>(row1);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        TableResult result = new TableResult(columns, rows);
        Assert.assertEquals(0, result.getFirst());
        Assert.assertEquals(-1, result.getMax());
        Assert.assertEquals(-1, result.getTotal());
        Assert.assertEquals(4, result.getSize());
        Assert.assertEquals(rows, result.getResults());
        Assert.assertEquals(columns, result.getColumns());

        Assert.assertEquals(0, result.getColumnIndex("column 1"));
        Assert.assertEquals(1, result.getColumnIndex("column 2"));
        Assert.assertEquals(2, result.getColumnIndex("column 3"));
    }

    /**
     * Tests the creation of a table result from a transferable map.
     */
    @Test
    public void fromMapTest()
    {
        //create expected map
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put(WebsockConstants.RESULT_TYPE,
            WebsockConstants.TABLE_RESULT);

        List<String> columns = new ArrayList<String>();
        columns.add("column 1");
        columns.add("column 2");
        columns.add("column 3");

        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> row1 = new ArrayList<Object>();
        row1.add("value 1");
        row1.add("value 2");
        row1.add("value 3");

        List<Object> row2 = new ArrayList<Object>(row1);
        List<Object> row3 = new ArrayList<Object>(row1);
        List<Object> row4 = new ArrayList<Object>(row1);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        resMap.put(WebsockConstants.TABLE_COLUMNS, columns);
        resMap.put(WebsockConstants.RESULT, rows);


        //decode
        TableResult result = new TableResult(columns, rows);
        Assert.assertEquals(0, result.getFirst());
        Assert.assertEquals(-1, result.getMax());
        Assert.assertEquals(-1, result.getTotal());
        Assert.assertEquals(4, result.getSize());
        Assert.assertEquals(rows, result.getResults());
        Assert.assertEquals(columns, result.getColumns());

        Assert.assertEquals(0, result.getColumnIndex("column 1"));
        Assert.assertEquals(1, result.getColumnIndex("column 2"));
        Assert.assertEquals(2, result.getColumnIndex("column 3"));
    }
}
