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

package com.googlecode.noweco.core;

import java.io.IOException;
import java.io.InputStream;

import com.googlecode.noweco.pop.spi.Pop3Message;
import com.googlecode.noweco.webmail.WebmailMessage;

/**
 *
 * @author Gael Lalire
 */
public class WebmailPop3Message implements Pop3Message {

    private WebmailMessage webmailMessage;

    private int id;

    public WebmailPop3Message(final int id, final WebmailMessage webmailMessage) {
        this.id = id;
        this.webmailMessage = webmailMessage;
    }

    public int getId() {
        return id;
    }

    public long getSize() throws IOException {
        return webmailMessage.getSize();
    }

    public InputStream getContent() throws IOException {
        return webmailMessage.getContent();
    }

    public String getUID() {
        return webmailMessage.getUniqueID();
    }

}
