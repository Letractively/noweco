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

package com.googlecode.noweco.pop;

import java.io.IOException;
import java.io.InputStream;

/**
 * All '\r\n.' are replaced by '\r\n..' Add a . after
 *
 * @author Gael Lalire
 */
public class Pop3InputStreamFilter extends InputStream {

    /*
     * state : 0 -> all 1 -> '\r' 2 -> '\r\n' 3 -> '\r\n.'
     */
    private int state = 0;

    private boolean headers = true;

    private InputStream in;

    private Integer linesAfterHeader;

    private int bodyLine = 0;

    private boolean eolTerminated = false;

    public Pop3InputStreamFilter(final InputStream inputStream) {
        this(inputStream, null);
    }

    public Pop3InputStreamFilter(final InputStream inputStream, final Integer linesAfterHeader) {
        this.in = inputStream;
        this.linesAfterHeader = linesAfterHeader;
    }

    @Override
    public int read() throws IOException {
        if (state == -1) {
            return -1;
        } else if (state == 3) {
            // duplicate '.'
            state = 0;
            return '.';
        }
        int read = in.read();
        if (read == -1) {
            if (state == 2) {
                eolTerminated = true;
            }
            state = -1;
            return -1;
        }
        switch (state) {
        case 0:
            if (read == '\r') {
                state = 1;
            }
            break;
        case 1:
            if (read == '\n') {
                if (!headers) {
                    bodyLine++;
                    if (linesAfterHeader != null && bodyLine >= linesAfterHeader) {
                        eolTerminated = true;
                        state = -1;
                    } else {
                        state = 2;
                    }
                } else {
                    state = 2;
                }
            } else {
                if (headers) {
                    state = -1;
                    throw new IOException("headers of a mail must be CRLF terminated");
                }
                if (read == '\r') {
                    state = 1;
                } else {
                    state = 0;
                }
            }
            break;
        case 2:
            if (read == '.') {
                state = 3;
            } else if (read == '\r') {
                if (headers) {
                    state = 4;
                } else {
                    state = 1;
                }
            } else {
                state = 0;
            }
            break;
        case 4:
            // end of headers
            if (read != '\n') {
                state = -1;
                throw new IOException("headers of a mail must be CRLF terminated");
            }
            headers = false;
            if (linesAfterHeader != null && bodyLine >= linesAfterHeader) {
                eolTerminated = true;
                state = -1;
            } else {
                state = 2;
            }
            break;
        default:
            throw new IllegalStateException("Invalid state");
        }
        return read;
    }

    public boolean isEOLTerminated() throws IOException {
        if (state != -1) {
            throw new IOException("Not yet terminated");
        }
        return eolTerminated;
    }

}
