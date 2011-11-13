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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Gael Lalire
 */
public class CachedInputStream {

    private ByteBuffer characterBuffer;

    private long inPosition;

    private InputStreamFactory inF;

    private InputStream in;

    private Object inMutex = new Object();

    public CachedInputStream(final InputStreamFactory inF, final CachedByteBuffer restored) {
        this.inF = inF;
        this.characterBuffer = restored;
        inPosition = restored.getLength();
    }

    public CachedInputStream(final InputStreamFactory inF, final File data) throws IOException {
        this.inF = inF;
        characterBuffer = new CachedByteBuffer(data);
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
                in.close();
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
            public void close() throws IOException {
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
