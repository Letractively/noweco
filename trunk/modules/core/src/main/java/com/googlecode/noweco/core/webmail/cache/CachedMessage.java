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

package com.googlecode.noweco.core.webmail.cache;

import java.io.IOException;
import java.io.Serializable;

import com.googlecode.noweco.core.webmail.Message;

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

    private String header;

    private String content;

    public CachedMessage(final Message delegate) {
        this.delegate = delegate;
        uniqueID = delegate.getUniqueID();
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int getSize() throws IOException {
        if (size == null) {
            size = delegate.getSize();
        }
        return size.intValue();
    }

    public String getHeader() throws IOException {
        if (header == null) {
            header = delegate.getHeader();
        }
        return header;
    }

    public String getContent() throws IOException {
        if (content == null) {
            content = delegate.getContent();
        }
        return content;
    }

}
