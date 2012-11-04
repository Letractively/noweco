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

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.WebmailMessage;
import com.googlecode.noweco.webmail.WebmailPages;

/**
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

    private File data;

    private IDGenerator generator;

    private transient Thread workingThread;

    private transient WeakReference<WebmailPages> lastResult;

    public CachedWebmailConnection(final WebmailConnection delegate, final File data, final IDGenerator generator,
            final String password) {
        this.password = password;
        this.data = data;
        this.delegate = delegate;
        this.generator = generator;
    }

    public String getPassword() {
        return password;
    }

    public void removeFromCache(final List<String> uids) {
        synchronized (messagesByUID) {
            for (String uid : uids) {
                CachedMessage remove = messagesByUID.remove(uid);
                if (remove != null) {
                    generator.releaseID(remove.getId());
                    try {
                        remove.delete();
                    } catch (IOException e) {
                        LOGGER.error("Unable to delete cache", e);
                    }
                    LOGGER.info("Remove {} from cache", uid);
                }
            }
        }
    }

    public List<? extends WebmailMessage> getMessages(final List<? extends WebmailMessage> messages) throws IOException {
        List<WebmailMessage> result = new ArrayList<WebmailMessage>(messages.size());

        synchronized (messagesByUID) {
            for (WebmailMessage message : messages) {
                String uniqueID = message.getUniqueID();
                if (!deleteFailed.contains(uniqueID)) {
                    CachedMessage cachedMessage = messagesByUID.get(uniqueID);
                    if (cachedMessage == null) {
                        int id = generator.takeID();
                        cachedMessage = new CachedMessage(message, new File(data, "msg" + id), id);
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

    public WebmailPages getPages() throws IOException {
        CachedPages cachedPages = null;
        if (delegate == null) {
            throw new IOException("delegate not fixed");
        }
        synchronized (this) {
            if (workingThread != null) {
                do {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                } while (workingThread != null);
                if (lastResult != null) {
                    WebmailPages webmailPages = lastResult.get();
                    if (webmailPages != null) {
                        return webmailPages;
                    }
                }
            }
            workingThread = Thread.currentThread();
        }
        try {
            cachedPages = new CachedPages(this, delegate.getPages());
            lastResult = new WeakReference<WebmailPages>(cachedPages);
            return cachedPages;
        } finally {
            synchronized (this) {
                workingThread = null;
                notifyAll();
            }
        }
    }

    public void close() throws IOException {
        if (workingThread != null) {
            workingThread.interrupt();
        }
    }

    public Set<String> delete(final Set<String> messageUids) {
        try {
            Set<String> delegateMessageUids;
            synchronized (messagesByUID) {
                if (deleteFailed.size() != 0) {
                    Set<String> uids = new HashSet<String>(messageUids);
                    uids.addAll(deleteFailed);
                    delegateMessageUids = uids;
                } else {
                    delegateMessageUids = messageUids;
                }
            }
            Set<String> deletedUid = delegate.delete(delegateMessageUids);
            // delete successful
            synchronized (messagesByUID) {
                deleteFailed.removeAll(deletedUid);
            }
            Set<String> result;
            if (delegateMessageUids != messageUids) {
                result = new HashSet<String>(messageUids);
                result.retainAll(deletedUid);
            } else {
                result = deletedUid;
            }
            return result;
        } catch (IOException e) {
            synchronized (messagesByUID) {
                deleteFailed.addAll(messageUids);
            }
            LOGGER.warn("Delete failed, however message deletion is kept in cache for next deletion try", e);
            return messageUids;
        }
    }

    public void shutdown() throws IOException {
        if (delegate != null) {
            delegate.close();
        }
        for (CachedMessage cachedMessage : messagesByUID.values()) {
            cachedMessage.shutdown();
        }
    }

}
