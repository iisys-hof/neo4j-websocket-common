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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * List wrapper class around a JSON array for easy access and conversion.
 * All added objects are converted into the internal JSON format, all retrieved
 * objects are wrapped accordingly.
 * Currently lacks deletion and proper insertion functionality.
 */
public class JSONList implements List<Object>
{
    private final JSONArray fArray;

    /**
     * Creates an empty JSONArray inside a wrapper.
     */
    public JSONList()
    {
        fArray = new JSONArray();
    }

    /**
     * Wraps the given JSONArray in a list.
     * The given list may not be null.
     *
     * @param array JSON array to wrap.
     */
    public JSONList(JSONArray array)
    {
        if(array == null)
        {
            throw new RuntimeException("JSON array was null");
        }

        fArray = array;
    }

    /**
     * Creates a new JSON array, converting and adding all objects in the given
     * collection.
     * The collection given must not be null.
     *
     * @param coll collection to base this list on
     */
    public JSONList(Collection<? extends Object> coll)
    {
        fArray = new JSONArray();

        for(Object o : coll)
        {
            fArray.put(toInternal(o));
        }
    }

    /**
     * Creates a wrapped JSON array from the given JSON String.
     *
     * @param json JSON to parse
     * @throws JSONException if parsing fails
     */
    public JSONList(String json) throws JSONException
    {
        fArray = new JSONArray(json);
    }

    /**
     * @return internal JSON array
     */
    public JSONArray getJson()
    {
        return fArray;
    }

    @Override
    public boolean add(Object arg0)
    {
        fArray.put(toInternal(arg0));
        return true;
    }

    @Override
    public void add(int arg0, Object arg1)
    {
        try
        {
            //TODO: move all objects that come after
            fArray.put(arg0, toInternal(arg1));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addAll(Collection<? extends Object> arg0)
    {
        for(Object o : arg0)
        {
            fArray.put(toInternal(o));
        }
        return true;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends Object> arg1)
    {
        for(Object o : arg1)
        {
            //TODO: move all objects that come after
            try
            {
                fArray.put(arg0, toInternal(o));
                ++arg0;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void clear()
    {
        //impossible
    }

    @Override
    public boolean contains(Object arg0)
    {
        boolean contains = false;
        final int size = fArray.length();

        for(int i = 0; i < size; ++i)
        {
            try
            {
                if(arg0.equals(fArray.get(i)))
                {
                    contains = true;
                    break;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return contains;
    }

    @Override
    public boolean containsAll(Collection<?> arg0)
    {
        final int argSize = arg0.size();
        final int size = fArray.length();
        int hits = 0;

        for(int i = 0; i < size; ++i)
        {
            try
            {
                if(arg0.contains(fArray.get(i)))
                {
                    ++hits;

                    if(hits == argSize)
                    {
                        break;
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return (hits == argSize);
    }

    @Override
    public Object get(int arg0)
    {
        try
        {
            return fromInternal(fArray.get(arg0));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int indexOf(Object arg0)
    {
        final int size = fArray.length();
        int i = 0;

        for(; i < size; ++i)
        {
            try
            {
                if(arg0.equals(fArray.get(i)))
                {
                    break;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if(i < size)
        {
            return i;
        }
        else
        {
            return -1;
        }
    }

    @Override
    public boolean isEmpty()
    {
        return (fArray.length() == 0);
    }

    @Override
    public Iterator<Object> iterator()
    {
        final List<Object> itList = new LinkedList<Object>();
        final int size = fArray.length();

        for(int i = 0; i < size; ++i)
        {
            try
            {
                itList.add(fromInternal(fArray.get(i)));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return itList.iterator();
    }

    @Override
    public int lastIndexOf(Object arg0)
    {
        final int size = fArray.length();
        int i = size;

        for(; i >= 0; --i)
        {
            try
            {
                if(arg0.equals(fArray.get(i)))
                {
                    break;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if(i > 0)
        {
            return i;
        }
        else
        {
            return -1;
        }
    }

    @Override
    public ListIterator<Object> listIterator()
    {
        final List<Object> itList = new LinkedList<Object>();
        final int size = fArray.length();

        for(int i = 0; i < size; ++i)
        {
            try
            {
                itList.add(fromInternal(fArray.get(i)));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return itList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int arg0)
    {
        final List<Object> itList = new LinkedList<Object>();
        final int size = fArray.length();

        for(int i = 0; i < size; ++i)
        {
            try
            {
                itList.add(fromInternal(fArray.get(i)));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return itList.listIterator(arg0);
    }

    @Override
    public boolean remove(Object arg0)
    {
        //impossible with simple methods
        //TODO: move all entries coming after backwards
        return false;
    }

    @Override
    public Object remove(int arg0)
    {
        //impossible with simple methods
        //TODO: move all entries coming after backwards
        return null;
    }

    @Override
    public boolean removeAll(Collection<?> arg0)
    {
        //impossible with simple methods
        //TODO: move all entries coming after backwards
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> arg0)
    {
        //impossible with simple methods
        //TODO: move all entries coming after backwards
        return false;
    }

    @Override
    public Object set(int arg0, Object arg1)
    {
        try
        {
            return fArray.put(arg0, toInternal(arg1));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int size()
    {
        return fArray.length();
    }

    @Override
    public List<Object> subList(int arg0, final int arg1)
    {
        final List<Object> subList = new ArrayList<Object>();

        for(; arg0 < arg1; ++arg0)
        {
            subList.add(fromInternal(fArray.opt(arg0)));
        }

        return subList;
    }

    @Override
    public Object[] toArray()
    {
        final int size = fArray.length();
        final Object[] array = new Object[size];

        for(int i = 0; i < size; ++i)
        {
            array[i] = fromInternal(fArray.opt(i));
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(final T[] array)
    {
        final int size = array.length;

        for(int i = 0; i < size; ++i)
        {
            array[i] = (T) fromInternal(fArray.opt(i));
        }

        return (T[])array;
    }

    @SuppressWarnings("unchecked")
    private Object toInternal(Object o)
    {
        Object result = o;

        if(o instanceof Map<?, ?>)
        {
            if(o instanceof JSONMap)
            {
                result = ((JSONMap)o).getJson();
            }
            else
            {
                try
                {
                    result = new JSONMap((Map<String, Object>)o).getJson();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if(o instanceof List<?>)
        {
            if(o instanceof JSONList)
            {
                result = ((JSONList)o).getJson();
            }
            else
            {
                result = new JSONList((List<Object>)o).getJson();
            }
        }

        return result;
    }

    private Object fromInternal(Object o)
    {
        Object result = o;

        if(o instanceof JSONArray)
        {
            result = new JSONList((JSONArray)o);
        }
        else if(o instanceof JSONObject)
        {
            result = new JSONMap((JSONObject)o);
        }
        else if(JSONObject.NULL.equals(o))
        {
            result = null;
        }

        return result;
    }
}
