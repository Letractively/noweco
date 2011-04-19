package com.googlecode.noweco.core.webmail;

import java.io.IOException;
import java.util.List;

public interface WebmailConnection {

    /**
     * Reload main page, may be needed to fetch new messages.
     * If you get an {@link IOException} from other methods you can use this method to try a reconnection.
     */
    void refresh() throws IOException;

    int getPageCount() throws IOException;

    /**
     * 0 <= page < getPageCount()
     */
    List<? extends Message> getMessages(int page) throws IOException;

    void release();

}
