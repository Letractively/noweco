package com.googlecode.noweco.core.webmail.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.WebmailConnection;

public class CachedWebmailConnection implements WebmailConnection, Serializable {

    private static final long serialVersionUID = 7636772578295623800L;

    private Map<String, CachedMessage> messagesByUID = new HashMap<String, CachedMessage>();

    private transient WebmailConnection delegate;

    public void setDelegate(WebmailConnection delegate) {
        this.delegate = delegate;
    }

    private String password;

    public CachedWebmailConnection(WebmailConnection delegate, String password) {
        this.password = password;
        this.delegate = delegate;
    }

    public String getPassword() {
        return password;
    }

    public void refresh() throws IOException {
        delegate.refresh();
    }

    private boolean sortByDate = true;

    private int latestPageCount;

    private boolean noChangeSinceLatestPageCount = false;

    public int getPageCount() throws IOException {
        noChangeSinceLatestPageCount = false;
        return delegate.getPageCount();
    }

    public List<? extends Message> getMessages(int page) throws IOException {
        List<? extends Message> messages = delegate.getMessages(page);
        List<Message> result = new ArrayList<Message>(messages.size());

        boolean allCached = true;
        for (Message message : messages) {
            String uniqueID = message.getUniqueID();
            CachedMessage cachedMessage = messagesByUID.get(uniqueID);
            if (cachedMessage == null) {
                allCached = false;
                cachedMessage = new CachedMessage(message);
                messagesByUID.put(uniqueID, cachedMessage);
            } else {
                cachedMessage.setDelegate(message);
            }
            result.add(cachedMessage);
        }
        if (sortByDate && page == 1 && allCached) {
            noChangeSinceLatestPageCount = true;
        }

        return result;
    }

    public void release() {
        delegate.release();
    }

}
