package com.googlecode.noweco.core.seam;

import java.util.regex.Pattern;

import com.googlecode.noweco.core.pop.spi.Pop3Manager;

public class DispatchedPop3Manager {

    private String id;

    private Pop3Manager pop3Manager;

    private Pattern pattern;

    public DispatchedPop3Manager(String id, Pop3Manager pop3Manager, Pattern pattern) {
        this.id = id;
        this.pop3Manager = pop3Manager;
        this.pattern = pattern;
    }

    public String getId() {
        return id;
    }

    public Pop3Manager getPop3Manager() {
        return pop3Manager;
    }

    public Pattern getPattern() {
        return pattern;
    }

}
