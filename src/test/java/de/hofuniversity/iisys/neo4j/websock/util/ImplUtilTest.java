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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.junit.Assert;

import org.junit.Test;

/**
 * Tests for the implementation utility, instantiating lists and maps.
 */
public class ImplUtilTest
{
    /**
     * Tests the instantiation of neutral formats.
     */
    @Test
    public void neutralTest()
    {
        ImplUtil impl = new ImplUtil(ArrayList.class, HashMap.class);

        List<?> list = impl.newList();
        Map<?, ?> map = impl.newMap();

        Assert.assertEquals(ArrayList.class, list.getClass());
        Assert.assertEquals(HashMap.class, map.getClass());

        //alternate implementations
        impl = new ImplUtil(LinkedList.class, TreeMap.class);

        list = impl.newList();
        map = impl.newMap();

        Assert.assertEquals(LinkedList.class, list.getClass());
        Assert.assertEquals(TreeMap.class, map.getClass());
    }

    /**
     * Tests the instantiation of BSON wrapper formats.
     */
    @Test
    public void bsonTest()
    {
        ImplUtil impl = new ImplUtil(BasicBSONList.class, BasicBSONObject.class);

        List<?> list = impl.newList();
        Map<?, ?> map = impl.newMap();

        Assert.assertEquals(BasicBSONList.class, list.getClass());
        Assert.assertEquals(BasicBSONObject.class, map.getClass());
    }

    /**
     * Tests the instantiation of JSON wrapper formats.
     */
    @Test
    public void jsonTest()
    {
        ImplUtil impl = new ImplUtil(JSONList.class, JSONMap.class);

        List<?> list = impl.newList();
        Map<?, ?> map = impl.newMap();

        Assert.assertEquals(JSONList.class, list.getClass());
        Assert.assertEquals(JSONMap.class, map.getClass());
    }

    /**
     * Tests faulty instantiations.
     */
    @Test
    public void faultyTest()
    {
        //no classes
        boolean fail = false;
        try
        {
            ImplUtil impl = new ImplUtil(null, null);

            impl.newList();
            impl.newMap();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail = true;
        }
        Assert.assertTrue(fail);
    }
}
