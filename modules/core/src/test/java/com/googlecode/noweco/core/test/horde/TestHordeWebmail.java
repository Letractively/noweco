/*
 * Copyright 2011 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.noweco.core.test.horde;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.test.TheTestContext;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.horde.HordeWebmail;

/**
 *
 * @author Gael Lalire
 */
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
        connect.getPages();
//        try {
//            for (int i = 0; i < connect.getPageCount(); i++) {
//                System.out.println(connect.getMessages(i));
//            }
//        } finally {
//            connect.release();
//        }
    }

}
