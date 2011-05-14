package com.googlecode.noweco.core.seam;

import java.io.IOException;

import com.googlecode.noweco.core.pop.spi.Pop3Account;
import com.googlecode.noweco.core.pop.spi.Pop3Manager;
import com.googlecode.noweco.core.webmail.Webmail;

public class WebmailPop3Manager implements Pop3Manager {

    private Webmail webmail;

    public WebmailPop3Manager(Webmail webmail) {
        this.webmail = webmail;
    }

    public Pop3Account authent(String username, String password) throws IOException {
        return new WebmailPop3Account(webmail.connect(username, password));
    }

    public void release() throws IOException {
        webmail.release();
    }
}
