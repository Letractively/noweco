package com.googlecode.noweco.core.webmail.cache;

import java.io.IOException;
import java.io.Serializable;

import com.googlecode.noweco.core.webmail.Message;

public class CachedMessage implements Message, Serializable {

    private static final long serialVersionUID = 8245316141066328829L;

    private transient Message delegate;

    public void setDelegate(Message delegate) {
        this.delegate = delegate;
    }

    private String uniqueID;

    private Integer size;

    private String header;

    private String content;

    public CachedMessage(Message delegate) {
        this.delegate = delegate;
        uniqueID = delegate.getUniqueID();
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int getSize() throws IOException {
        if (size == null) {
            size = delegate.getSize();
        }
        return size.intValue();
    }

    public String getHeader() throws IOException {
        if (header == null) {
            header = delegate.getHeader();
        }
        return header;
    }

    public String getContent() throws IOException {
        if (content == null) {
            content = delegate.getContent();
        }
        return content;
    }

    public void delete() throws IOException {
        delegate.delete();
    }

}
