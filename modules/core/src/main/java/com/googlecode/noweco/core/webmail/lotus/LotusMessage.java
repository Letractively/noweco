package com.googlecode.noweco.core.webmail.lotus;

import java.io.IOException;

import com.googlecode.noweco.core.webmail.Message;

public class LotusMessage implements Message {

    private String id;

    private int size;

    private String headers;

    private String content;

    private LotusWebmailConnection webmail;

    public LotusMessage(LotusWebmailConnection webmail, String id, int size) {
        this.webmail = webmail;
        this.id = id;
        this.size = size;
        content = null;
    }

    public int getSize() {
        return size;
    }

    public String getHeader() throws IOException {
        if (headers == null) {
            if (content == null) {
                content = webmail.getContent(id);
            }
            int indexOf = content.indexOf("\r\n\r\n");
            if (indexOf == -1) {
                indexOf = content.indexOf("\n\n");
            }
            headers = content.substring(0, indexOf);
        }
        return headers;
    }

    public String getContent() throws IOException {
        if (content == null) {
            content = webmail.getContent(id);
        }
        return content;
    }

    @Override
    public String toString() {
        return getUniqueID();
    }

    public String getUniqueID() {
        return id;
    }

}
