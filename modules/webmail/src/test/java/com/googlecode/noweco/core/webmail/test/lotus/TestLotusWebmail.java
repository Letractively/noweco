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

package com.googlecode.noweco.core.webmail.test.lotus;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.webmail.test.TheTestContext;
import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.WebmailMessage;
import com.googlecode.noweco.webmail.WebmailPages;
import com.googlecode.noweco.webmail.lotus.LotusWebmail;
import com.googlecode.noweco.webmail.portal.PortalConnector;

/**
 * @author Gael Lalire
 */
public class TestLotusWebmail {

    @Test
    @Ignore
    public void testDelete() throws Exception {
        LotusWebmail lotusWebmail = new LotusWebmail();
        lotusWebmail.setAuthent((PortalConnector) getClass().getClassLoader().loadClass(TheTestContext.getLotusPortal())
                .newInstance());
        WebmailConnection connect = lotusWebmail.connect(TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        Set<String> delete = connect.delete(Collections.singleton("1989da2fc0f6488f1ccd23090b69752a".toUpperCase()));
        Assert.assertTrue(delete.size() != 0);
    }

    @Test
    @Ignore
    public void testConnect() throws Exception {
        LotusWebmail lotusWebmail = new LotusWebmail();
        lotusWebmail.setAuthent((PortalConnector) getClass().getClassLoader().loadClass(TheTestContext.getLotusPortal())
                .newInstance());
        WebmailConnection connect = lotusWebmail.connect(TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        WebmailPages pages = connect.getPages();
        while (pages.hasNextPage()) {
            List<? extends WebmailMessage> nextPageMessages = pages.getNextPageMessages();
            for (WebmailMessage webmailMessage : nextPageMessages) {
                if (webmailMessage.getUniqueID().equalsIgnoreCase("2f64649ac12c3fa15a7307cac64c2f2c")) {
                    System.out.println("create Bad FILE");
                    FileOutputStream fos = new FileOutputStream("badfile.txt");

                    InputStream content = webmailMessage.getContent();
                    byte[] b = new byte[1024];
                    int read = content.read(b);
                    while (read != -1) {
                        fos.write(b, 0, read);
                        read = content.read(b);
                    }
                    fos.close();
                }
            }
        }
//        Assert.assertFalse(nextPageMessages.isEmpty());
    }

}
