package com.googlecode.noweco.core.webmail.lotus;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;

public class LotusPagesIterator implements Iterator<Page> {

    private int page = 0;

    private LotusWebmailConnection lotusWebmail;

    private boolean hasNext = true;

    public LotusPagesIterator(LotusWebmailConnection lotusWebmail) {
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
