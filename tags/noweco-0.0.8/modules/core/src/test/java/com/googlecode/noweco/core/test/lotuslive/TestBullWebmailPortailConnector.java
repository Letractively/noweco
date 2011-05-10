package com.googlecode.noweco.core.test.lotuslive;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.core.test.TheTestContext;
import com.googlecode.noweco.core.webmail.portal.BullWebmailPortalConnector;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class TestBullWebmailPortailConnector {

    @Test
    @Ignore
    public void test() throws IOException {
        PortalConnector lotusLivePortailConnector = new BullWebmailPortalConnector();
        lotusLivePortailConnector.connect(null, TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
    }

}
