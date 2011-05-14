package com.googlecode.noweco.core.webmail.cache;

import java.util.Iterator;

import com.googlecode.noweco.core.webmail.Page;

public class CachedPageIterator implements Iterator<Page> {

    private CachedWebmailConnection cachedWebmailConnection;

    private Iterator<Page> delegate;

    public CachedPageIterator(CachedWebmailConnection cachedWebmailConnection, Iterator<Page> delegate) {
        this.cachedWebmailConnection = cachedWebmailConnection;
        this.delegate = delegate;
    }

    public boolean hasNext() {
        return delegate.hasNext();
    }

    public Page next() {
        return new CachedPage(delegate.next(), cachedWebmailConnection);
    }

    public void remove() {
        delegate.remove();
    }

}
