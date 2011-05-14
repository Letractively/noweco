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
import java.util.List;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;

/**
 *
 * @author Gael Lalire
 */
public class CachedPage implements Page {

    private Page delegate;

    private CachedWebmailConnection cachedWebmailConnection;

    public CachedPage(final Page delegate, final CachedWebmailConnection cachedWebmailConnection) {
        this.delegate = delegate;
        this.cachedWebmailConnection = cachedWebmailConnection;
    }

    public List<? extends Message> getMessages() throws IOException {
        return cachedWebmailConnection.getMessages(delegate.getMessages());
    }

}
