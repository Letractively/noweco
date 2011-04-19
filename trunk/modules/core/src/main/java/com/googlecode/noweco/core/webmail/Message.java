package com.googlecode.noweco.core.webmail;

import java.io.IOException;

public interface Message {

    int getSize() throws IOException;

    String getHeader() throws IOException;

    String getContent() throws IOException;

    void delete() throws IOException;

}
