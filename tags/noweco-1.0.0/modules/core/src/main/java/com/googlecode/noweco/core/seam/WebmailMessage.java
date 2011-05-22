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

package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.googlecode.noweco.core.pop.spi.Message;

/**
 *
 * @author Gael Lalire
 */
public class WebmailMessage implements Message {

    private com.googlecode.noweco.core.webmail.Message webmailMessage;

    private int id;

    public WebmailMessage(final int id, final com.googlecode.noweco.core.webmail.Message webmailMessage) {
        this.id = id;
        this.webmailMessage = webmailMessage;
    }

    public int getId() {
        return id;
    }

    public int getSize() throws IOException {
        return webmailMessage.getSize();
    }

    public Reader getContent() throws IOException {
        return new StringReader(webmailMessage.getContent());
    }

    public Reader getHeaders() throws IOException {
        return new StringReader(webmailMessage.getHeader());
    }

    public String getUID() {
        return webmailMessage.getUniqueID();
    }

}
