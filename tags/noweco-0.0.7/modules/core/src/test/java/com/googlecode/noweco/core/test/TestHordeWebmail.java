package com.googlecode.noweco.core.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.horde.HordeWebmail;

public class TestHordeWebmail {

    private static final Pattern PATTERN = Pattern.compile("http(s)?://([^/:]*)(?::\\d+)?(.*)");

    @Test
    @Ignore
    public void testConnect() throws IOException {
        String lotusURL = TheTestContext.getLotusURL();
        Matcher matcher = PATTERN.matcher(lotusURL);
        Assert.assertTrue(matcher.matches());
        String proxyHost = TheTestContext.getProxyHost();
        HordeWebmail webmail;
        if (proxyHost != null && proxyHost.length() != 0) {
            webmail = new HordeWebmail();
            webmail.setProxy(proxyHost, TheTestContext.getProxyPort());
        } else {
            webmail = new HordeWebmail();
        }
        WebmailConnection connect = webmail.connect(TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        try {
//            for (int i = 0; i < connect.getPageCount(); i++) {
//                System.out.println(connect.getMessages(i));
//            }
        } finally {
            connect.release();
        }
    }

}
