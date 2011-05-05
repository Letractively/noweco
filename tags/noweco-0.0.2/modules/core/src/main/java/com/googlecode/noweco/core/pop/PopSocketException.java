package com.googlecode.noweco.core.pop;

import java.io.IOException;

public class PopSocketException extends Exception {

    private static final long serialVersionUID = -2486751912349485990L;

    public PopSocketException(IOException cause) {
        super("Pop socket issue", cause);
    }

}
