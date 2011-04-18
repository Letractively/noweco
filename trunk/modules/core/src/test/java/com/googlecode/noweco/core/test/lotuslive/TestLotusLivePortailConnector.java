package com.googlecode.noweco.core.test.lotuslive;

import java.io.IOException;

import org.junit.Test;

import com.googlecode.noweco.core.lotus.portailconnector.LotusLivePortailConnector;
import com.googlecode.noweco.core.test.TheTestContext;

public class TestLotusLivePortailConnector {

    @Test
    public void test() throws IOException {
        LotusLivePortailConnector lotusLivePortailConnector = new LotusLivePortailConnector();
        lotusLivePortailConnector.connect(null, TheTestContext.getLotusUserName(), TheTestContext.getLotusPassword());
    }

}
