package com.googlecode.noweco.core.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.webmail.lotus.INotesWebmailConnection;

public class TestINotesConnection {

    private static final Pattern PATTERN = Pattern.compile("http(s)?://([^/:]*)(?::\\d+)?(.*)");

    @Test
    @Ignore
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
        INotesWebmailConnection iNotesConnection;
        if (proxyHost != null && proxyHost.length() != 0) {
            iNotesConnection = new INotesWebmailConnection(proxyHost, TheTestContext.getProxyPort(), secure, host);
        } else {
            iNotesConnection = new INotesWebmailConnection(secure, host);
        }
        try {
            iNotesConnection.connect(path, TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        } finally {
            iNotesConnection.release();
        }
    }

}
