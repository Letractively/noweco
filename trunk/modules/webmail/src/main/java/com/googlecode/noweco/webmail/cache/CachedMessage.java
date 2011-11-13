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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.googlecode.noweco.webmail.Message;

/**
 *
 * @author Gael Lalire
 */
public class CachedMessage implements Message, Serializable {

    private static final long serialVersionUID = 8245316141066328829L;

    public void setDelegate(final Message delegate) throws IOException {
        content = new CachedInputStream(new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return delegate.getContent();
            }
        }, data);
    }

    private String uniqueID;

    private Integer size;

    private transient CachedInputStream content;

    private File data;

    public CachedMessage(final Message delegate, final File data) throws IOException {
        uniqueID = delegate.getUniqueID();
        this.data = data;
        content = new CachedInputStream(new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return delegate.getContent();
            }
        }, data);
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public synchronized long getSize() throws IOException {
        if (size == null) {
            int csize = 0;
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
        return size.intValue();
    }

    public synchronized InputStream getContent() throws IOException {
        return content.newInputStream();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        String result = null;
//        if (content != null) {
//            result = content.get();
//        }
        out.writeObject(result);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String result = (String) in.readObject();
//        if (result != null) {
//            content = new SoftReference<String>(result);
//        }
    }

}
