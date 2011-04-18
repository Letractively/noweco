package com.googlecode.noweco.core.pop;

import java.util.List;

public interface Pop3Transaction {

    List<? extends Message> getMessages();

}
