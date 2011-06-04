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

import com.googlecode.noweco.webmail.Message;
import com.googlecode.noweco.webmail.Page;

/**
 *
 * @author Gael Lalire
 */
public class CachedPage implements Page {

    private Page delegate;

    private CachedWebmailConnection cachedWebmailConnection;

    private CachedPageIterator cachedPageIterator;

    public CachedPage(final CachedPageIterator cachedPageIterator, final Page delegate, final CachedWebmailConnection cachedWebmailConnection) {
        this.cachedPageIterator = cachedPageIterator;
        this.delegate = delegate;
        this.cachedWebmailConnection = cachedWebmailConnection;
    }

    private boolean send = false;

    public List<? extends Message> getMessages() throws IOException {
        List<? extends Message> messages = cachedWebmailConnection.getMessages(delegate.getMessages());
        synchronized (this) {
            if (!send) {
                List<String> uids = new ArrayList<String>();
                for (Message message : messages) {
                    uids.add(message.getUniqueID());
                }
                cachedPageIterator.addUIDs(uids);
                send = true;
            }
        }
        return messages;
    }

}
