/*
 * Copyright 2011 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.noweco.webmail.lotus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.cache.CachedByteBuffer;

/**
 * Lotus mail are not always well-formed. This class transforms ill-formed mail
 * to well-formed mail.
 *
 * @author Gael Lalire
 */
public class LotusMessageInputStream extends InputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotusMessageInputStream.class);

    private static final String DATE_PREFIX = "Date:";

    private static final String NEW_LINE = "\r\n";

    private static final byte[] ASCII_NEW_LINE = new byte[] { '\r', '\n' };

    private static final String RECEIVED = "received:";

    private ASCIILineInputStream delegate;

    private boolean headers = true;

    private InputStream beforeHeadersInputStream;

    private File tmp = File.createTempFile("lmr", ".buf");

    private CachedByteBuffer byteBuffer = new CachedByteBuffer(tmp);

    private long position = 0;

    public LotusMessageInputStream(final String id, final InputStream contentReader) throws IOException {
        delegate = new ASCIILineInputStream(contentReader);
        String line = delegate.readLine();
        LOGGER.trace("read line: {}", line);

        String dateLine = null;
        boolean receiveHeader = false;
        while (line != null && line.length() != 0) {
            if (line.length() > RECEIVED.length() && line.substring(0, RECEIVED.length()).toLowerCase().equals(RECEIVED)) {
                receiveHeader = true;
            }
            if (line.startsWith(DATE_PREFIX)) {
                try {
                    dateLine = LotusDateTransformer.convertNotesToRfc822(line);
                    LOGGER.debug("Convert Date header : {} for {}", dateLine, id);
                    byteBuffer.write(dateLine.getBytes("US-ASCII"));
                    byteBuffer.write(ASCII_NEW_LINE);
                } catch (ParseException e) {
                    dateLine = line;
                    LOGGER.trace("Not a lotus date", e);
                    byteBuffer.write(line.getBytes("US-ASCII"));
                    byteBuffer.write(ASCII_NEW_LINE);
                }
            } else {
                byteBuffer.write(line.getBytes("US-ASCII"));
                byteBuffer.write(ASCII_NEW_LINE);
            }
            line = delegate.readLine();
            LOGGER.trace("read line: {}", line);
        }
        if (!receiveHeader && dateLine != null) {
            String receivedHeader = "Received: by noweco; " + dateLine.substring(DATE_PREFIX.length()) + NEW_LINE;
            LOGGER.debug("Insert generated Received header : {} for {}", receivedHeader, id);
            beforeHeadersInputStream = new ByteArrayInputStream(receivedHeader.getBytes("US-ASCII"));
        }
        if (line != null) {
            // empty line
            byteBuffer.write(ASCII_NEW_LINE);
        }
    }

    @Override
    public int read(final byte[] cbuf, final int off, final int len) throws IOException {
        if (byteBuffer == null) {
            throw new IOException("Stream closed");
        }
        if (headers) {
            // read the added header
            if (beforeHeadersInputStream != null) {
                int read = beforeHeadersInputStream.read(cbuf, off, len);
                if (read != -1) {
                    return read;
                } else {
                    beforeHeadersInputStream = null;
                }
            }
            // read the header
            int read = byteBuffer.read(position, cbuf, off, len);
            if (read != -1) {
                position += read;
                return read;
            } else {
                headers = false;
                position = 0;
                byteBuffer.setLength(0);
            }
        }
        return delegate.read(cbuf, off, len);
    }

    @Override
    protected void finalize() throws Throwable {
        if (byteBuffer != null) {
            byteBuffer.detachFile();
            tmp.delete();
        }
    }

    @Override
    public void close() throws IOException {
        if (byteBuffer != null) {
            byteBuffer.detachFile();
            tmp.delete();
        }
        byteBuffer = null;
        delegate.close();
    }

    @Override
    public int read() throws IOException {
        byte[] cbuf = new byte[1];
        int r = read(cbuf, 0, 1);
        if (r == 1) {
            return cbuf[0] & 0xff;
        }
        return r;
    }

}
