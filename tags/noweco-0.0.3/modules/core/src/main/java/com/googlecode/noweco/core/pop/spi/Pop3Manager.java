package com.googlecode.noweco.core.pop.spi;

import java.io.IOException;

public interface Pop3Manager {

    Pop3Transaction authent(String username, String password) throws IOException;

}