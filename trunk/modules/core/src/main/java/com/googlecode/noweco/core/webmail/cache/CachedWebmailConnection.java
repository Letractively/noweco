package com.googlecode.noweco.core.webmail.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;
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

    public List<? extends Message> getMessages(List<? extends Message> messages) {
        List<Message> result = new ArrayList<Message>(messages.size());

        for (Message message : messages) {
            String uniqueID = message.getUniqueID();
            CachedMessage cachedMessage = messagesByUID.get(uniqueID);
            if (cachedMessage == null) {
                cachedMessage = new CachedMessage(message);
                messagesByUID.put(uniqueID, cachedMessage);
            } else {
                cachedMessage.setDelegate(message);
            }
            result.add(cachedMessage);
        }

        return result;
    }

    public Iterator<Page> getPages() throws IOException {
        return new CachedPageIterator(this, delegate.getPages());
    }

    public void release() {
        delegate.release();
    }

}
