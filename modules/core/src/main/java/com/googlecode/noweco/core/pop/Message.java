package com.googlecode.noweco.core.pop;

import java.io.Reader;

public interface Message {

    int getId();

    int getSize();

    void setMarkedForDeletion(boolean markedForDeletion);

    boolean isMarkedForDeletion();

    // realy delete messages
    void update();

    Reader getContent();

}
