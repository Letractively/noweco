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

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Gael Lalire
 */
public class ASCIILineInputStream extends FilterInputStream {

    public ASCIILineInputStream(final InputStream is) {
        super(is);
    }

    /**
     * Skip line terminator.
     */
    public byte[] readLine() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read = in.read();
        if (read == -1) {
            return null;
        }
        boolean cr = false;
        do {
            if (cr) {
                if (read == '\n') {
                    // line readed
                    cr = false;
                    break;
                } else if (read != '\r') {
                    cr = false;
                } else {
                    // another CR
                    byteArrayOutputStream.write('\r');
                }
            } else {
                if (read == '\r') {
                    cr = true;
                }
            }
            if (!cr) {
                byteArrayOutputStream.write(read);
            }
            read = in.read();
        } while (read != -1);
        if (cr) {
            byteArrayOutputStream.write('\r');
        }
        return byteArrayOutputStream.toByteArray();
    }

}
