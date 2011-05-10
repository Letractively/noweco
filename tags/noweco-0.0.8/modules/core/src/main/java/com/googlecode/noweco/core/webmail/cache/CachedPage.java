package com.googlecode.noweco.core.webmail.cache;

import java.io.IOException;
import java.util.List;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;

public class CachedPage implements Page {

    private Page delegate;

    private CachedWebmailConnection cachedWebmailConnection;

    public CachedPage(Page delegate, CachedWebmailConnection cachedWebmailConnection) {
        this.delegate = delegate;
        this.cachedWebmailConnection = cachedWebmailConnection;
    }

    public List<? extends Message> getMessages() throws IOException {
        return cachedWebmailConnection.getMessages(delegate.getMessages());
    }

}
