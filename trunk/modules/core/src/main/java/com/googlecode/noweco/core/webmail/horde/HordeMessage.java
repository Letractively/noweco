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

package com.googlecode.noweco.core.webmail.horde;

import java.io.IOException;

import com.googlecode.noweco.core.webmail.Message;

/**
 *
 * @author Gael Lalire
 */
public class HordeMessage implements Message {

    private int id;

    private int size;

    private String headers;

    private String content;

    private HordeWebmailConnection webmail;

    public HordeMessage(final HordeWebmailConnection webmail, final int id, final int size) {
        this.webmail = webmail;
        this.id = id;
        this.size = size;
        content = null;
    }

    public int getSize() {
        return size;
    }

    public String getHeader() throws IOException {
        if (headers == null) {
            if (content == null) {
                content = webmail.getContent(id);
            }
            int indexOf = content.indexOf("\r\n\r\n");
            if (indexOf == -1) {
                indexOf = content.indexOf("\n\n");
            }
            headers = content.substring(0, indexOf);
        }
        return headers;
    }

    public String getContent() throws IOException {
        if (content == null) {
            content = webmail.getContent(id);
        }
        return content;
    }

    @Override
    public String toString() {
        return getUniqueID();
    }

    public String getUniqueID() {
        return String.valueOf(id);
    }

}
