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
import java.io.Serializable;

import com.googlecode.noweco.webmail.WebmailMessage;

/**
 *
 * @author Gael Lalire
 */
public class CachedMessage implements WebmailMessage, Serializable {

    private static final long serialVersionUID = 8245316141066328829L;

    public void setDelegate(final WebmailMessage delegate) throws IOException {
        content = new CachedInputStream(new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return delegate.getContent();
            }
        }, cachedByteBuffer);
    }

    private String uniqueID;

    private Long size;

    private transient CachedInputStream content;

    private CachedByteBuffer cachedByteBuffer;

    private int id;

    public int getId() {
        return id;
    }

    public CachedMessage(final WebmailMessage delegate, final File data, final int id) throws IOException {
        uniqueID = delegate.getUniqueID();
        this.id = id;
        cachedByteBuffer = new CachedByteBuffer(data);
        content = new CachedInputStream(new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return delegate.getContent();
            }
        }, cachedByteBuffer);
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public synchronized long getSize() throws IOException {
        if (size == null) {
            long csize = 0;
            byte[] buff = new byte[2048];
            InputStream newReader = content.newInputStream();
            try {
                int read = newReader.read(buff);
                while (read != -1) {
                    csize += read;
                    read = newReader.read(buff);
                }
            } finally {
                newReader.close();
            }
            size = csize;
        }
        return size.longValue();
    }

    public synchronized InputStream getContent() throws IOException {
        return content.newInputStream();
    }

    public void delete() throws IOException {
        cachedByteBuffer.delete();
    }

    public void shutdown() throws IOException {
        cachedByteBuffer.shutdown();
    }

}
