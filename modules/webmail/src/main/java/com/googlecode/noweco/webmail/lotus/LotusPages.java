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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.WebmailMessage;
import com.googlecode.noweco.webmail.WebmailPages;

/**
 * @author Gael Lalire
 */
public class LotusPages implements WebmailPages {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotusPages.class);

    private int page = 0;

    private LotusWebmailConnection lotusWebmail;

    private boolean hasNext = true;

    public LotusPages(final LotusWebmailConnection lotusWebmail) {
        this.lotusWebmail = lotusWebmail;
    }

    public boolean hasNextPage() throws IOException {
        return hasNext;
    }

    public List<? extends WebmailMessage> getNextPageMessages() throws IOException {
        if (!hasNext) {
            throw new IOException("No more page");
        }
        LOGGER.debug("Fetching webmail page #{}", page);
        final List<? extends WebmailMessage> messages = lotusWebmail.getMessages(1 + page * LotusWebmailConnection.MAX_MESSAGE);
        if (messages.size() != LotusWebmailConnection.MAX_MESSAGE) {
            hasNext = false;
        }
        page++;
        return messages;
    }

}
