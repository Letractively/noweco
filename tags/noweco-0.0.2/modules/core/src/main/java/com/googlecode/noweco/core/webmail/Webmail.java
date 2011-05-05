package com.googlecode.noweco.core.webmail;

import java.io.IOException;

public interface Webmail {

    WebmailConnection connect(String user, String password) throws IOException;

    void release();

}
