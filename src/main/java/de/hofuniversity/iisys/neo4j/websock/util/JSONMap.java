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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * List wrapper class around a JSON object for easy access and conversion.
 * All added objects are converted into the internal JSON format, all retrieved
 * objects are wrapped accordingly.
 */
public class JSONMap implements Map<String, Object>
{
    private final JSONObject fJson;

    /**
     * Creates an empty, wrapped JSON object.
     */
    public JSONMap()
    {
        fJson = new JSONObject();
    }

    /**
     * Wraps the given JSON object.
     * The given object may not be null.
     *
     * @param json JSON object to wrap
     */
    public JSONMap(JSONObject json)
    {
        if(json == null)
        {
            throw new RuntimeException("JSON object was null");
        }

        fJson = json;
    }

    /**
     * Creates a wrapped JSON object based on the given JSON String.
     *
     * @param json JSON String to base this map on
     * @throws Exception if parsing fails
     */
    public JSONMap(String json) throws Exception
    {
        fJson = new JSONObject(json);
    }

    /**
     * Creates a wrapped JSON object based on the converted entries of the
     * given map.
     * The given map must not be null.
     *
     * @param map map to base the JSON object on
     * @throws Exception if conversion fails
     */
    public JSONMap(Map<String, Object> map) throws Exception
    {
        fJson = new JSONObject();

        for(Entry<String, Object> mapE : map.entrySet())
        {
            fJson.put(mapE.getKey(), toInternal(mapE.getValue()));
        }
    }

    /**
     * @return internal JSON object
     */
    public JSONObject getJson()
    {
        return fJson;
    }

    @Override
    public void clear()
    {
        final List<String> keyList = new LinkedList<String>();

        final Iterator<?> iter = fJson.keys();
        while(iter.hasNext())
        {
            keyList.add(iter.next().toString());
        }

        for(String key : keyList)
        {
            fJson.remove(key);
        }
    }

    @Override
    public boolean containsKey(Object arg0)
    {
        return fJson.has(arg0.toString());
    }

    @Override
    public boolean containsValue(final Object arg0)
    {
        boolean contains = false;

        String key = null;
        final Iterator<?> iter = fJson.keys();
        while(iter.hasNext())
        {
            key = iter.next().toString();

            if(arg0.equals(fJson.opt(key)))
            {
                contains = true;
                break;
            }
        }

        return contains;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        final Set<Entry<String, Object>> entrySet =
            new HashSet<Entry<String, Object>>();
        final Iterator<?> iter = fJson.keys();

        while(iter.hasNext())
        {
            final String key = iter.next().toString();
            final Object value = fromInternal(fJson.opt(key));

            entrySet.add(new Entry<String, Object>(){

                @Override
                public String getKey()
                {
                    return key;
                }

                @Override
                public Object getValue()
                {
                    return value;
                }

                @Override
                public Object setValue(Object arg0)
                {
                    try
                    {
                        return fJson.put(key, arg0);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }

            });
        }

        return entrySet;
    }

    @Override
    public Object get(Object arg0)
    {
        return fromInternal(fJson.opt(arg0.toString()));
    }

    @Override
    public boolean isEmpty()
    {
        if(fJson.keys().hasNext())
        {
            return false;
        }
        return true;
    }

    @Override
    public Set<String> keySet()
    {
        final Set<String> keySet = new HashSet<String>();

        final Iterator<?> iter = fJson.keys();
        while(iter.hasNext())
        {
            keySet.add(iter.next().toString());
        }

        return keySet;
    }

    @Override
    public Object put(String arg0, Object arg1)
    {
        try
        {
            return fJson.put(arg0, toInternal(arg1));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> arg0)
    {
        for(Entry<? extends String, ? extends Object> mapE : arg0.entrySet())
        {
            try
            {
                fJson.put(mapE.getKey(), toInternal(mapE.getValue()));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object remove(Object arg0)
    {
        return fJson.remove(arg0.toString());
    }

    @Override
    public int size()
    {
        int size = 0;

        final Iterator<?> iter = fJson.keys();
        while(iter.hasNext())
        {
            iter.next();
            ++size;
        }

        return size;
    }

    @Override
    public Collection<Object> values()
    {
        final List<Object> values = new ArrayList<Object>();

        String key = null;
        final Iterator<?> iter = fJson.keys();
        while(iter.hasNext())
        {
            key = iter.next().toString();

            values.add(fromInternal(fJson.opt(key)));
        }

        return values;
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
