package com.googlecode.noweco.core.pop.spi;

import java.io.IOException;
import java.io.Reader;

public interface Message {

    int getSize() throws IOException;

    Reader getContent() throws IOException;

    Reader getHeaders() throws IOException;

    String getUID();

}
