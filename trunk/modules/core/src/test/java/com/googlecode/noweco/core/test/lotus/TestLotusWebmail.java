package com.googlecode.noweco.core.test.lotus;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.test.TheTestContext;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.lotus.LotusWebmail;
import com.googlecode.noweco.core.webmail.portal.BullWebmailPortalConnector;

public class TestLotusWebmail {

    @Test
    @Ignore
    public void testDelete() throws IOException {
        LotusWebmail lotusWebmail = new LotusWebmail();
        lotusWebmail.setAuthent(new BullWebmailPortalConnector());
        WebmailConnection connect = lotusWebmail.connect(TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
        connect.delete(Arrays.asList("c2d2dd3763b45fca1e7c9a9014d5ae69".toUpperCase()));
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
