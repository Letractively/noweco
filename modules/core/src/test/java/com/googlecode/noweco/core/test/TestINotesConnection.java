package com.googlecode.noweco.core.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.noweco.core.INotesConnection;

public class TestINotesConnection {

    private static final Pattern PATTERN = Pattern.compile("http([s])://([^/]*)(.*)");

    @Test
    public void testConnect() throws IOException {
        boolean secure = false;
        String lotusURL = TheTestContext.getLotusURL();
        Matcher matcher = PATTERN.matcher(lotusURL);
        Assert.assertTrue(matcher.matches());
        if (matcher.group(1).length() == 0) {
            secure = false;
        } else {
            secure = true;
        }
        String host = matcher.group(2);
        String path = matcher.group(3);
        String proxyHost = TheTestContext.getProxyHost();
        INotesConnection iNotesConnection;
        if (proxyHost != null && proxyHost.length() != 0) {
            iNotesConnection = new INotesConnection(proxyHost, TheTestContext.getProxyPort(), secure, host);
        } else {
            iNotesConnection = new INotesConnection(secure, host);
        }
        try {
            iNotesConnection.connect(path, TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        } finally {
            iNotesConnection.release();
        }
    }

}
