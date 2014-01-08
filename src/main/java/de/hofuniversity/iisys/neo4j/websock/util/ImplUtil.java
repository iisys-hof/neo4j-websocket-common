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
package de.hofuniversity.iisys.neo4j.websock.util;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility encapsulating the instantiation of lists and maps form variable
 * implementations.
 */
public class ImplUtil
{
    @SuppressWarnings("rawtypes")
    private final Class<? extends List> fListClass;
    @SuppressWarnings("rawtypes")
    private final Class<? extends Map> fMapClass;

    /**
     * Creates a list and map creation utility instantiating the given
     * implementations.
     * None of the parameters may be null.
     *
     * @param listClass list implementation to use
     * @param mapClass map implementation to use
     */
    @SuppressWarnings("rawtypes")
    public ImplUtil(Class<? extends List> listClass,
        Class<? extends Map> mapClass)
    {
        if(listClass == null)
        {
            throw new NullPointerException("list implementation was null");
        }
        if(mapClass == null)
        {
            throw new NullPointerException("map implementation was null");
        }

        fListClass = listClass;
        fMapClass = mapClass;
    }

    /**
     * Creates a new list instance of the configured implementation.
     * Throws a RuntimeException if instantiation fails.
     *
     * @return new list instance
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> newList()
    {
        try
        {
            return (List<T>) fListClass.newInstance();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                "could not instantiate list", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new map instance of the configured implementation.
     * Throws a RuntimeException if instantiation fails.
     *
     * @return new list instance
     */
    @SuppressWarnings("unchecked")
    public <S, T> Map<S, T> newMap()
    {
        try
        {
            return (Map<S, T>) fMapClass.newInstance();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                "could not instantiate map", e);
            throw new RuntimeException(e);
        }
    }
}
