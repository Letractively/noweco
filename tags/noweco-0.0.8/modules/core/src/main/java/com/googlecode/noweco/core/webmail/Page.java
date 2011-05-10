package com.googlecode.noweco.core.webmail;

import java.io.IOException;
import java.util.List;

public interface Page {

    List<? extends Message> getMessages() throws IOException;

}
