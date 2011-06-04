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

package com.googlecode.noweco.webmail.lotus;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.googlecode.noweco.webmail.Message;
import com.googlecode.noweco.webmail.Page;

/**
 *
 * @author Gael Lalire
 */
public class LotusPagesIterator implements Iterator<Page> {

    private int page = 0;

    private LotusWebmailConnection lotusWebmail;

    private boolean hasNext = true;

    public LotusPagesIterator(final LotusWebmailConnection lotusWebmail) {
        this.lotusWebmail = lotusWebmail;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public Page next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        try {
            final List<? extends Message> messages = lotusWebmail.getMessages(1 + page * LotusWebmailConnection.MAX_MESSAGE);
            if (messages.size() != LotusWebmailConnection.MAX_MESSAGE) {
                hasNext = false;
            }
            page++;
            return new Page() {

                public List<? extends Message> getMessages() throws IOException {
                    return messages;
                }
            };
        } catch (final IOException e) {
            hasNext = false;
            return new Page() {

                public List<? extends Message> getMessages() throws IOException {
                    throw e;
                }
            };
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
