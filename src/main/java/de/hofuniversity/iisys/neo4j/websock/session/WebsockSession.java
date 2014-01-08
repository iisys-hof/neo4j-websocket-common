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

import javax.websocket.Session;

/**
 * Object containing information about and options set for a websocket session.
 */
public class WebsockSession
{
    private final Session fSession;

    private boolean fAuthenticated = false;

    private String fRealm = WebsockConstants.DEFAULT_REALM;

    private String fFormat = WebsockConstants.JSON_FORMAT;

    private boolean fCompression = false;

    private boolean fStreaming = false;

    private boolean fBatching = false;
    private int fBatchSize = 0;

    /**
     * Creates a session object for the given websocket session.
     * The given session may not be null.
     *
     * @param session session to create an information object for
     */
    public WebsockSession(Session session)
    {
        if(session == null)
        {
            throw new RuntimeException("session was null");
        }

        fSession = session;
    }

    /**
     * @return whether the client is logged in
     */
    public boolean isAuthenticated()
    {
        return fAuthenticated;
    }

    /**
     * @param authenticated whether the client is logged in
     */
    public void setAuthenticated(boolean authenticated)
    {
        this.fAuthenticated = authenticated;
    }

    /**
     * @return name of the realm that belongs to the client or null
     */
    public String getRealm()
    {
        return fRealm;
    }

    /**
     * @param realm name of the realm that belongs to the client
     */
    public void setRealm(String realm)
    {
        this.fRealm = realm;
    }

    /**
     * @return desired message format
     */
    public String getFormat()
    {
        return fFormat;
    }

    /**
     * @param format new desired message format
     */
    public void setFormat(String format)
    {
        this.fFormat = format;
    }

    /**
     * @return whether compression is requested
     */
    public boolean getCompression()
    {
        return fCompression;
    }

    /**
     * @param compression whether compression is requested
     */
    public void setCompression(boolean compression)
    {
        this.fCompression = compression;
    }

    /**
     * @return whether streaming is requested
     */
    public boolean getStreaming()
    {
        return fStreaming;
    }

    /**
     * @param streaming whether streaming is requested
     */
    public void setStreaming(boolean streaming)
    {
        this.fStreaming = streaming;
    }

    /**
     * @return whether batching is requested
     */
    public boolean getBatching()
    {
        return fBatching;
    }

    /**
     * @param batching whether batching is requested
     */
    public void setBatching(boolean batching)
    {
        this.fBatching = batching;
    }

    /**
     * @return desired batch size
     */
    public int getBatchSize()
    {
        return fBatchSize;
    }

    /**
     * @param batchSize desired batch size
     */
    public void setBatchSize(int batchSize)
    {
        this.fBatchSize = batchSize;
    }

    /**
     * @return websocket session object
     */
    public Session getSession()
    {
        return fSession;
    }
}
