package com.googlecode.noweco.core.pop;

import java.io.IOException;
import java.io.Reader;

public interface Message {

    int getId();

    int getSize() throws IOException;

    void setMarkedForDeletion(boolean markedForDeletion);

    boolean isMarkedForDeletion();

    // realy delete messages
    void update() throws IOException;

    Reader getContent() throws IOException;

    Reader getHeaders() throws IOException;

}
