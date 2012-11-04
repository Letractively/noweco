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

package com.googlecode.noweco.webmail.cache;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gael Lalire
 */
public class CachedInputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedInputStream.class);

    private ByteBuffer characterBuffer;

    private long inPosition;

    private InputStreamFactory inF;

    private InputStream in;

    private Object inMutex = new Object();

    private int readers = 0;

    public CachedInputStream(final InputStream in, final CachedByteBuffer restored) {
        this.inF = new InputStreamFactory() {

            private boolean first = true;

            public InputStream createInputStream() throws IOException {
                if (first) {
                    first = false;
                    return in;
                } else {
                    throw new IOException("Unable to recreate input stream");
                }
            }
        };
        this.characterBuffer = restored;
        inPosition = restored.getLength();
    }

    public CachedInputStream(final InputStreamFactory inF, final CachedByteBuffer restored) {
        this.inF = inF;
        this.characterBuffer = restored;
        inPosition = restored.getLength();
    }

    private int readIn(final byte[] cbuf, final int off, final int len) throws IOException {
        try {
            if (inF == null) {
                return -1;
            }
            if (in == null) {
                in = inF.createInputStream();
                in.skip(inPosition);
            }
            int read = in.read(cbuf, off, len);
            if (read >= 0) {
                inPosition += read;
                synchronized (characterBuffer) {
                    characterBuffer.write(cbuf, off, read);
                }
            } else {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Close failed", e);
                }
                in = null;
                inF = null;
            }
            return read;
        } catch (IOException e) {
            in = null;
            throw e;
        }
    }

    public InputStream newInputStream() {
        return new InputStream() {

            private long position = 0;

            {
                synchronized (inMutex) {
                    readers++;
                }
            }

            @Override
            public int read(final byte[] cbuf, final int off, final int len) throws IOException {
                if (position == -1) {
                    throw new IOException("Stream closed");
                }
                int nlen = len;
                boolean fromBuffer = false;
                int read = -1;
                synchronized (inMutex) {
                    if (inPosition == position) {
                        read = readIn(cbuf, off, nlen);
                    } else {
                        fromBuffer = true;
                    }
                }
                // position < inPosition
                if (fromBuffer) {
                    if (nlen > inPosition - position) {
                        nlen = (int) (inPosition - position);
                    }
                    synchronized (characterBuffer) {
                        read = characterBuffer.read(position, cbuf, off, nlen);
                    }
                }
                if (read != -1) {
                    position += read;
                }
                return read;
            }

            @Override
            protected void finalize() {
                if (position != -1) {
                    // not close but GC
                    synchronized (inMutex) {
                        readers--;
                        if (readers == 0 && in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                LOGGER.warn("Close failed", e);
                            }
                            in = null;
                        }
                    }
                }
            }

            @Override
            public void close() {
                synchronized (inMutex) {
                    readers--;
                    if (readers == 0 && in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            LOGGER.warn("Close failed", e);
                        }
                        in = null;
                    }
                }
                position = -1;
            }

            @Override
            public int read() throws IOException {
                byte[] cbuf = new byte[1];
                int read = read(cbuf, 0, 1);
                if (read == 1) {
                    return cbuf[0] & 0xff;
                }
                return read;
            }
        };
    }

}
