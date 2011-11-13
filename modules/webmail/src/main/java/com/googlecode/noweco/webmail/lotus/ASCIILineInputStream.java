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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * @author Gael Lalire
 */
public class ASCIILineInputStream extends FilterInputStream {

    private byte[] line = new byte[1024];

    private int offset;

    private int length;

    private CharsetDecoder newDecoder;

    private CharBuffer out = CharBuffer.allocate(1024);

    private boolean crRead = false;

    public ASCIILineInputStream(final InputStream is) {
        super(is);
        newDecoder = Charset.forName("US-ASCII").newDecoder();
        newDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        newDecoder.onMalformedInput(CodingErrorAction.REPLACE);
        out.limit(0);
    }

    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();

        // already decoded
        while (out.hasRemaining()) {
            char c = out.get();
            offset++; // 1 because ASCII
            length--;
            if (c == '\r') {
                crRead = true;
                return sb.toString();
            } else if (c == '\n') {
                if (crRead) {
                    crRead = false;
                    continue;
                }
                return sb.toString();
            } else {
                crRead = false;
            }
            sb.append(c);
        }

        int read = in.read(line);
        if (read == -1) {
            if (sb.length() != 0) {
                return sb.toString();
            }
            return null;
        }
        offset = 0;
        length = read;
        while (read != -1) {
            out.clear();
            newDecoder.reset();
            newDecoder.decode(java.nio.ByteBuffer.wrap(line, 0, read), out, false);
            out.flip();
            while (out.hasRemaining()) {
                char c = out.get();
                offset++; // 1 because ASCII
                length--;
                if (c == '\r') {
                    crRead = true;
                    return sb.toString();
                } else if (c == '\n') {
                    if (crRead) {
                        crRead = false;
                        continue;
                    }
                    return sb.toString();
                } else {
                    crRead = false;
                }
                sb.append(c);
            }
            read = in.read(line);
        }
        out.clear();
        newDecoder.reset();
        newDecoder.decode(java.nio.ByteBuffer.wrap(line, 0, 0), out, true);
        out.flip();
        while (out.hasRemaining()) {
            char c = out.get();
            offset++; // 1 because ASCII
            length--;
            if (c == '\r') {
                crRead = true;
                return sb.toString();
            } else if (c == '\n') {
                if (crRead) {
                    crRead = false;
                    continue;
                }
                return sb.toString();
            } else {
                crRead = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (crRead && length != 0) {
            if (line[offset] == '\n') {
                offset++;
                length--;
            }
            crRead = false;
        }
        if (length != 0) {
            out.limit(0);
            newDecoder.reset();
            if (len <= length) {
                System.arraycopy(line, offset, b, off, len);
                length -= len;
                offset += len;
                out.clear();
                newDecoder.decode(java.nio.ByteBuffer.wrap(line, offset, length), out, false);
                return len;
            }
            int read = length;
            System.arraycopy(line, offset, b, off, read);
            offset = 0;
            length = 0;
            return read;
        } else {
            if (crRead) {
                int read = super.read();
                crRead = false;
                if (read == -1) {
                    return read;
                }
                if (read != '\n') {
                    b[off] = (byte) read;
                    return 1;
                }
            }
            return super.read(b, off, len);
        }
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
