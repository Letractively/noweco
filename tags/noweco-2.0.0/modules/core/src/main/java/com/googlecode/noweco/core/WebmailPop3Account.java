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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.googlecode.noweco.pop.spi.Pop3Account;
import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.WebmailMessage;
import com.googlecode.noweco.webmail.WebmailPages;

/**
 *
 * @author Gael Lalire
 */
public class WebmailPop3Account implements Pop3Account {

    private WebmailConnection webmailConnection;

    private List<WebmailPop3Message> webmailMessages = new ArrayList<WebmailPop3Message>();

    public WebmailPop3Account(final WebmailConnection webmailConnection) throws IOException {
        this.webmailConnection = webmailConnection;
    }

    public List<WebmailPop3Message> getMessages() throws IOException {
        int id = 1;
        WebmailPages pages = webmailConnection.getPages();
        while (pages.hasNextPage()) {
            for (WebmailMessage message : pages.getNextPageMessages()) {
                webmailMessages.add(new WebmailPop3Message(id, message));
                id++;
            }
        }
        return webmailMessages;
    }

    public void delete(final List<String> uids) throws IOException {
        webmailConnection.delete(new HashSet<String>(uids));
    }

    public void close() throws IOException {
        webmailConnection.close();
    }

}
