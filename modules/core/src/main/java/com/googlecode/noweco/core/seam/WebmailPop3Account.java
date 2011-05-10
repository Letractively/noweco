package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.noweco.core.pop.spi.Pop3Account;
import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;
import com.googlecode.noweco.core.webmail.WebmailConnection;

public class WebmailPop3Account implements Pop3Account {

    private WebmailConnection webmailConnection;

    private List<WebmailMessage> webmailMessages = new ArrayList<WebmailMessage>();

    public WebmailPop3Account(WebmailConnection webmailConnection) throws IOException {
        this.webmailConnection = webmailConnection;
    }

    public List<WebmailMessage> getMessages() throws IOException {
        int id = 1;
        Iterator<Page> pages = webmailConnection.getPages();
        while (pages.hasNext()) {
            List<? extends Message> messages = pages.next().getMessages();
            for (Message message : messages) {
                webmailMessages.add(new WebmailMessage(id, message));
                id++;
            }
        }
        return webmailMessages;
    }

    public void delete(List<String> uids) throws IOException {
        webmailConnection.delete(uids);
    }

}
