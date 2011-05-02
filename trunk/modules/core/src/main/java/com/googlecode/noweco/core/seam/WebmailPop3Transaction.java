package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.googlecode.noweco.core.pop.spi.Pop3Transaction;
import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.WebmailConnection;

public class WebmailPop3Transaction implements Pop3Transaction {

    private WebmailConnection webmailConnection;

    private List<WebmailMessage> webmailMessages;

    public WebmailPop3Transaction(WebmailConnection webmailConnection) throws IOException {
        this.webmailConnection = webmailConnection;
    }

    public List<WebmailMessage> getMessages() throws IOException {
        if (webmailMessages == null) {
            webmailMessages = new ArrayList<WebmailMessage>();
            int pageCount = webmailConnection.getPageCount();
            int id = 1;
            for (int i = pageCount; i >= 0; i--) {
                List<? extends Message> messages = webmailConnection.getMessages(i);
                ListIterator<? extends Message> listIterator = messages.listIterator(messages.size());
                while (listIterator.hasPrevious()) {
                    webmailMessages.add(new WebmailMessage(id, listIterator.previous()));
                    id++;
                }
            }
        }
        return webmailMessages;
    }

}
