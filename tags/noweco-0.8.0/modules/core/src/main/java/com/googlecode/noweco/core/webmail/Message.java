package com.googlecode.noweco.core.webmail;

import java.io.IOException;

public interface Message {

    String getUniqueID();

    int getSize() throws IOException;

    String getHeader() throws IOException;

    String getContent() throws IOException;

}
