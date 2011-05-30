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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;
import com.googlecode.noweco.core.webmail.WebmailConnection;

/**
 *
 * @author Gael Lalire
 */
public class CachedWebmailConnection implements WebmailConnection, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedWebmailConnection.class);

    private static final long serialVersionUID = 7636772578295623800L;

    private Map<String, CachedMessage> messagesByUID = new HashMap<String, CachedMessage>();

    private Set<String> deleteFailed = new HashSet<String>();

    public Map<String, CachedMessage> getMessagesByUID() {
        return messagesByUID;
    }

    private transient WebmailConnection delegate;

    public void setDelegate(final WebmailConnection delegate) {
        this.delegate = delegate;
    }

    private String password;

    public void setPassword(final String password) {
        this.password = password;
    }

    public CachedWebmailConnection(final WebmailConnection delegate, final String password) {
        this.password = password;
        this.delegate = delegate;
    }

    public String getPassword() {
        return password;
    }

    public void removeFromCache(final List<String> uids) {
        synchronized (messagesByUID) {
            for (String uid : uids) {
                if (messagesByUID.remove(uid) != null) {
                    LOGGER.info("Remove {} from cache", uid);
                }
            }
        }
    }

    public List<? extends Message> getMessages(final List<? extends Message> messages) {
        List<Message> result = new ArrayList<Message>(messages.size());

        synchronized (messagesByUID) {
            for (Message message : messages) {
                String uniqueID = message.getUniqueID();
                if (!deleteFailed.contains(uniqueID)) {
                    CachedMessage cachedMessage = messagesByUID.get(uniqueID);
                    if (cachedMessage == null) {
                        cachedMessage = new CachedMessage(message);
                        LOGGER.info("Add {} to cache", uniqueID);
                        messagesByUID.put(uniqueID, cachedMessage);
                    } else {
                        cachedMessage.setDelegate(message);
                    }
                    result.add(cachedMessage);
                }
            }
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
        try {
            List<String> delegateMessageUids;
            synchronized (messagesByUID) {
                if (deleteFailed.size() != 0) {
                    Set<String> uids = new HashSet<String>(messageUids);
                    uids.addAll(deleteFailed);
                    delegateMessageUids = new ArrayList<String>(uids);
                } else {
                    delegateMessageUids = messageUids;
                }
            }
            List<String> deletedUid = delegate.delete(delegateMessageUids);
            synchronized (messagesByUID) {
                deleteFailed.removeAll(delegateMessageUids);
            }
            List<String> result;
            if (delegateMessageUids != messageUids) {
                result = new ArrayList<String>(messageUids);
                result.retainAll(deletedUid);
            } else {
                result = deletedUid;
            }
            return result;
        } catch (IOException e) {
            synchronized (messagesByUID) {
                deleteFailed.addAll(messageUids);
            }
            LOGGER.warn("Delete failed, however message deletion is keeped in cache for next deletion try", e);
            return messageUids;
        }
    }

}
