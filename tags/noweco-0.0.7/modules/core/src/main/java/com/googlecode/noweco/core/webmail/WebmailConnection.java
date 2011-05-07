package com.googlecode.noweco.core.webmail;

import java.io.IOException;
import java.util.Iterator;

public interface WebmailConnection {

    Iterator<Page> getPages() throws IOException;

    void release();

}
