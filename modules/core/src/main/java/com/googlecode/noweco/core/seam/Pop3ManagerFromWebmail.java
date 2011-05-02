package com.googlecode.noweco.core.seam;

import java.io.IOException;

import com.googlecode.noweco.core.pop.spi.Pop3Manager;
import com.googlecode.noweco.core.pop.spi.Pop3Transaction;
import com.googlecode.noweco.core.webmail.Webmail;

public class Pop3ManagerFromWebmail implements Pop3Manager {

    private Webmail webmail;

    public Pop3ManagerFromWebmail(Webmail webmail) {
        this.webmail = webmail;
    }

    public Pop3Transaction authent(String username, String password) throws IOException {
        return new WebmailPop3Transaction(webmail.connect(username, password));
    }
}
