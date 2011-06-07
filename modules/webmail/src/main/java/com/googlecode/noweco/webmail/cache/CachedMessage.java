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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.noweco.webmail.Message;

/**
 *
 * @author Gael Lalire
 */
public class CachedMessage implements Message, Serializable {

    private static final long serialVersionUID = 8245316141066328829L;

    private transient Message delegate;

    public void setDelegate(final Message delegate) {
        this.delegate = delegate;
    }

    private String uniqueID;

    private Integer size;

    private Integer headerIndex;

    private transient SoftReference<String> content;

    public CachedMessage(final Message delegate) {
        this.delegate = delegate;
        uniqueID = delegate.getUniqueID();
    }

    public String getUniqueID() {
        return uniqueID;
    }

    private static final Pattern END_OF_HEADERS = Pattern.compile("(?:\r\n|\n){2}");

    private String fill() throws IOException {
        String result = null;
        if (content != null) {
            result = content.get();
        }
        if (result == null) {
            // TODO serialize content just before it is GC, and restore it here
            result = delegate.getContent();
        } else {
            return result;
        }
        if (content == null) {
            content = new SoftReference<String>(result);
            size = result.length();
            Matcher matcher = END_OF_HEADERS.matcher(result);
            if (matcher.find()) {
                headerIndex = matcher.start();
            } else {
                headerIndex = size;
            }
        } else {
            content = new SoftReference<String>(result);
        }
        return result;
    }

    public synchronized int getSize() throws IOException {
        if (size == null) {
            fill();
        }
        return size.intValue();
    }

    public synchronized String getHeader() throws IOException {
        return fill().substring(0, headerIndex);
    }

    public synchronized String getContent() throws IOException {
        return fill();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        String result = null;
        if (content != null) {
            result = content.get();
        }
        out.writeObject(result);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String result = (String) in.readObject();
        if (result != null) {
            content = new SoftReference<String>(result);
        }
    }

}
