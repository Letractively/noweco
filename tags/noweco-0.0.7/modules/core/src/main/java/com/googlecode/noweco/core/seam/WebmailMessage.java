package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.googlecode.noweco.core.pop.spi.Message;

public class WebmailMessage implements Message {

    private com.googlecode.noweco.core.webmail.Message webmailMessage;

    private int id;

    public WebmailMessage(int id, com.googlecode.noweco.core.webmail.Message webmailMessage) {
        this.id = id;
        this.webmailMessage = webmailMessage;
    }

    public int getId() {
        return id;
    }

    public int getSize() throws IOException {
        return webmailMessage.getSize();
    }

    public Reader getContent() throws IOException {
        return new StringReader(webmailMessage.getContent());
    }

    public void delete() throws IOException {
        webmailMessage.delete();
    }

    public Reader getHeaders() throws IOException {
        return new StringReader(webmailMessage.getHeader());
    }

    public String getUID() throws IOException {
        return webmailMessage.getUniqueID();
    }

}
