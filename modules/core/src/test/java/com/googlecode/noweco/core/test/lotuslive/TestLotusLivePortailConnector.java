package com.googlecode.noweco.core.test.lotuslive;

import java.io.IOException;

import org.junit.Test;

import com.googlecode.noweco.core.test.TheTestContext;
import com.googlecode.noweco.core.webmail.portal.LotusLivePortalConnector;

public class TestLotusLivePortailConnector {

    @Test
    public void test() throws IOException {
        LotusLivePortalConnector lotusLivePortailConnector = new LotusLivePortalConnector();
        lotusLivePortailConnector.connect(null, TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
    }

}
