package com.googlecode.noweco.core.pop;

import java.io.IOException;
import java.util.List;

public interface Pop3Transaction {

    List<? extends Message> getMessages() throws IOException;

}
