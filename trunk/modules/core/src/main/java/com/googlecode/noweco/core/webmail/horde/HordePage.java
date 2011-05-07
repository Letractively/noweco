package com.googlecode.noweco.core.webmail.horde;

import java.io.IOException;
import java.util.List;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;

public class HordePage implements Page {

    private int index;

    public HordePage(int index) {
        this.index = index;
    }

    public List<? extends Message> getMessages() throws IOException {
        index++;
        return null;
    }

}
