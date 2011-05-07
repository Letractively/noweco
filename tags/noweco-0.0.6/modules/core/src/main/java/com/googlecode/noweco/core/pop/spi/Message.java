package com.googlecode.noweco.core.pop.spi;

import java.io.IOException;
import java.io.Reader;

public interface Message {

    int getId();

    int getSize() throws IOException;

    void delete() throws IOException;

    Reader getContent() throws IOException;

    Reader getHeaders() throws IOException;

    String getUID() throws IOException;

}
