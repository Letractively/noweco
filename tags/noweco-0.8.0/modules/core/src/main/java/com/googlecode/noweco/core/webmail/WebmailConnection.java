package com.googlecode.noweco.core.webmail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public interface WebmailConnection {

    Iterator<Page> getPages() throws IOException;

    void delete(List<String> messageUids) throws IOException;

    void release();

}
