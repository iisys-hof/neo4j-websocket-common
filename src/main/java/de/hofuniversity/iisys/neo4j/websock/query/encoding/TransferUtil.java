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
package de.hofuniversity.iisys.neo4j.websock.query.encoding;

import java.nio.ByteBuffer;

import de.hofuniversity.iisys.neo4j.websock.query.WebsockQuery;
import de.hofuniversity.iisys.neo4j.websock.session.WebsockConstants;

/**
 * Utility forwarding outgoing messages and format configuration to the
 * subordinate binary and text transfer utilities.
 */
public class TransferUtil implements Cloneable
{
    private final StringTransferUtil fStrings;
    private final BinaryTransferUtil fBinary;

    private String fFormat, fCompression;

    private boolean fBinaryTransfer;

    /**
     * Creates a transfer utility using the given text and binary transfer
     * utilities.
     * The given utilities must not be null.
     *
     * @param strings text transfer utility to use
     * @param binary binary transfer utility to use
     */
    public TransferUtil(StringTransferUtil strings, BinaryTransferUtil binary)
    {
        if(strings == null)
        {
            throw new NullPointerException("string transfer utility was null");
        }
        if(binary == null)
        {
            throw new NullPointerException("binary transfer utility was null");
        }

        fStrings = strings;
        fBinary = binary;
    }

    /**
     * Relays a message to send to a subordinate utility, determined by the
     * configure format.
     * The message must not be null.
     *
     * @param message message to send
     * @throws Exception if conversion or sending fail
     */
    public void sendMessage(final WebsockQuery message) throws Exception
    {
        if(fBinaryTransfer)
        {
            fBinary.sendMessage(message);
        }
        else
        {
            fStrings.sendMessage(message);
        }
    }

    /**
     * Converts the given message in String form into a WebsockQuery.
     *
     * @param message message in String form to convert
     * @return converted WebsockQuery
     * @throws Exception if conversion fails
     */
    public WebsockQuery convert(String message) throws Exception
    {
        return fStrings.convert(message);
    }

    /**
     * Converts the given message in binary form into a WebsockQuery.
     *
     * @param message message in binary form to convert
     * @return converted WebsockQuery
     * @throws Exception if conversion fails
     */
    public WebsockQuery convert(ByteBuffer message) throws Exception
    {
        return fBinary.convert(message);
    }

    /**
     * Sets the format to send messages in and the primary format to try
     * decoding incoming messages with.
     * Unknown formats are ignored and logged.
     *
     * @param format encoding to use for messages (bson/json)
     * @param compression which type of compression to use
     */
    public void setFormat(String format, String compression)
    {
        fFormat = format;
        fCompression = compression;

        if(WebsockConstants.JSON_FORMAT.equals(format)
            && WebsockConstants.NO_COMPRESSION.equals(compression))
        {
            fStrings.setFormat(format, compression);
            fBinaryTransfer = false;
        }
        else
        {
            fBinary.setFormat(format, compression);
            fBinaryTransfer = true;
        }
    }

    /**
     * @return independent transfer utility using the same connection and
     *  current configuration
     */
    public TransferUtil clone()
    {
        StringTransferUtil stUtil = fStrings.clone();
        BinaryTransferUtil btUtil = fBinary.clone();

        TransferUtil tu = new TransferUtil(stUtil, btUtil);

        tu.fBinaryTransfer = fBinaryTransfer;
        tu.fFormat = fFormat;
        tu.fCompression = fCompression;

        tu.setFormat(fFormat, fCompression);

        return tu;
    }
}
