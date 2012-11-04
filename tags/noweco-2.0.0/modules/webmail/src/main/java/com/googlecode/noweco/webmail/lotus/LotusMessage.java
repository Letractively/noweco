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

import java.io.IOException;
import java.io.InputStream;

import com.googlecode.noweco.webmail.WebmailMessage;

/**
 *
 * @author Gael Lalire
 */
public class LotusMessage implements WebmailMessage {

    private String id;

    private LotusWebmailConnection webmail;

    public LotusMessage(final LotusWebmailConnection webmail, final String id) {
        this.webmail = webmail;
        this.id = id;
    }

    public long getSize() throws IOException {
        // HEAD method on the web page do not work, because the message may be
        // transformed
        byte[] buff = new byte[1024];
        long size = 0;
        LotusMessageInputStream is = fetch();
        try {
            int read = is.read(buff);
            while (read != -1) {
                size += read;
                read = is.read(buff);
            }
        } finally {
            is.close();
        }
        return size;
    }

    private LotusMessageInputStream fetch() throws IOException {
        return new LotusMessageInputStream(id, webmail.getContent(id));
    }

    public InputStream getContent() throws IOException {
        return fetch();
    }

    @Override
    public String toString() {
        return getUniqueID();
    }

    public String getUniqueID() {
        return id;
    }

}
