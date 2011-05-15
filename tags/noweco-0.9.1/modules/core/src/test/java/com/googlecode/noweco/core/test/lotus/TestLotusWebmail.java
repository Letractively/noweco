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

package com.googlecode.noweco.core.test.lotus;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.test.TheTestContext;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.lotus.LotusWebmail;
import com.googlecode.noweco.core.webmail.portal.BullWebmailPortalConnector;

/**
 *
 * @author Gael Lalire
 */
public class TestLotusWebmail {

    @Test
    @Ignore
    public void testDelete() throws IOException {
        LotusWebmail lotusWebmail = new LotusWebmail();
        lotusWebmail.setAuthent(new BullWebmailPortalConnector());
        WebmailConnection connect = lotusWebmail.connect(TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        List<String> delete = connect.delete(Arrays.asList("1989da2fc0f6488f1ccd23090b69752a".toUpperCase()));
        Assert.assertTrue(delete.size() != 0);
    }

    @Test
    @Ignore
    public void testConnect() throws IOException {
        LotusWebmail lotusWebmail = new LotusWebmail();
        lotusWebmail.setAuthent(new BullWebmailPortalConnector());
        WebmailConnection connect = lotusWebmail.connect(TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        Assert.assertFalse(connect.getPages().next().getMessages().isEmpty());
    }

}
