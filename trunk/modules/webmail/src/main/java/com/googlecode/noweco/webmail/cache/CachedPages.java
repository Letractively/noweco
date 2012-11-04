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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.noweco.webmail.WebmailMessage;
import com.googlecode.noweco.webmail.WebmailPages;

/**
 * @author Gael Lalire
 */
public class CachedPages implements WebmailPages {

    private CachedWebmailConnection cachedWebmailConnection;

    private WebmailPages delegate;

    private List<String> uids;

    public CachedPages(final CachedWebmailConnection cachedWebmailConnection, final WebmailPages delegate) {
        Map<String, CachedMessage> messagesByUID = cachedWebmailConnection.getMessagesByUID();
        synchronized (messagesByUID) {
            uids = new ArrayList<String>(messagesByUID.keySet());
        }
        this.cachedWebmailConnection = cachedWebmailConnection;
        this.delegate = delegate;
    }

    public boolean hasNextPage() throws IOException {
        boolean hasNext = delegate.hasNextPage();
        if (!hasNext) {
            cachedWebmailConnection.removeFromCache(uids);
        }
        return hasNext;
    }

    public List<? extends WebmailMessage> getNextPageMessages() throws IOException {
        if (!hasNextPage()) {
            throw new IOException("no more page");
        }
        List<? extends WebmailMessage> messages = cachedWebmailConnection.getMessages(delegate.getNextPageMessages());
        synchronized (this) {
            for (WebmailMessage message : messages) {
                // the cache must be kept
                uids.remove(message.getUniqueID());
            }
        }
        return messages;
    }

}
