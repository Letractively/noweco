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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.noweco.webmail.Page;

/**
 *
 * @author Gael Lalire
 */
public class CachedPageIterator implements Iterator<Page> {

    private CachedWebmailConnection cachedWebmailConnection;

    private Iterator<Page> delegate;

    private List<String> uids;

    private int pageCount = 0;

    private int uidCount = 0;

    public CachedPageIterator(final CachedWebmailConnection cachedWebmailConnection, final Iterator<Page> delegate) {
        Map<String, CachedMessage> messagesByUID = cachedWebmailConnection.getMessagesByUID();
        synchronized (messagesByUID) {
            uids = new ArrayList<String>(messagesByUID.keySet());
        }
        this.cachedWebmailConnection = cachedWebmailConnection;
        this.delegate = delegate;
    }

    public void addUIDs(final List<String> uids) {
        this.uids.removeAll(uids);
        uidCount++;
        if (ended && pageCount == uidCount) {
            cachedWebmailConnection.removeFromCache(this.uids);
        }
    }

    private boolean ended;

    public boolean hasNext() {
        boolean hasNext = delegate.hasNext();
        if (!hasNext) {
            ended = true;
            if (pageCount == uidCount) {
                cachedWebmailConnection.removeFromCache(uids);
            }
        }
        return hasNext;
    }

    public Page next() {
        CachedPage cachedPage = new CachedPage(this, delegate.next(), cachedWebmailConnection);
        pageCount++;
        return cachedPage;
    }

    public void remove() {
        delegate.remove();
    }

}
