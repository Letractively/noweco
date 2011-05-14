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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;
import com.googlecode.noweco.core.webmail.WebmailConnection;

/**
 *
 * @author Gael Lalire
 */
public class CachedWebmailConnection implements WebmailConnection, Serializable {

    private static final long serialVersionUID = 7636772578295623800L;

    private Map<String, CachedMessage> messagesByUID = new HashMap<String, CachedMessage>();

    private transient WebmailConnection delegate;

    public void setDelegate(final WebmailConnection delegate) {
        this.delegate = delegate;
    }

    private String password;

    public CachedWebmailConnection(final WebmailConnection delegate, final String password) {
        this.password = password;
        this.delegate = delegate;
    }

    public String getPassword() {
        return password;
    }

    public List<? extends Message> getMessages(final List<? extends Message> messages) {
        List<Message> result = new ArrayList<Message>(messages.size());

        for (Message message : messages) {
            String uniqueID = message.getUniqueID();
            CachedMessage cachedMessage = messagesByUID.get(uniqueID);
            if (cachedMessage == null) {
                cachedMessage = new CachedMessage(message);
                messagesByUID.put(uniqueID, cachedMessage);
            } else {
                cachedMessage.setDelegate(message);
            }
            result.add(cachedMessage);
        }

        return result;
    }

    public Iterator<Page> getPages() throws IOException {
        return new CachedPageIterator(this, delegate.getPages());
    }

    public void release() {
        delegate.release();
    }

    public List<String> delete(final List<String> messageUids) throws IOException {
        return delegate.delete(messageUids);
    }

}
