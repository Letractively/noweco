package com.googlecode.noweco.core.pop.spi;

import java.io.IOException;
import java.util.List;


public interface Pop3Account {

    List<? extends Message> getMessages() throws IOException;

    void delete(List<String> uids) throws IOException;

}
