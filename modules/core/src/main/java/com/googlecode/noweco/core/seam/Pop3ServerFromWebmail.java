package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.util.concurrent.Executors;

import com.googlecode.noweco.core.pop.Pop3Manager;
import com.googlecode.noweco.core.pop.Pop3Server;
import com.googlecode.noweco.core.pop.Pop3Transaction;
import com.googlecode.noweco.core.webmail.Webmail;

public class Pop3ServerFromWebmail implements Pop3Manager {

    private Pop3Server pop3Server;

    private Webmail webmail;

    public Pop3ServerFromWebmail(Webmail webmail) {
        this.webmail = webmail;
        pop3Server = new Pop3Server(this, Executors.newFixedThreadPool(3));
    }

    public void start() throws IOException {
        pop3Server.start();
    }

    public void stop() throws IOException, InterruptedException {
        pop3Server.stop();
    }

    public Pop3Transaction authent(String username, String password) throws IOException {
        return new WebmailPop3Transaction(webmail.connect(username, password));
    }
}
