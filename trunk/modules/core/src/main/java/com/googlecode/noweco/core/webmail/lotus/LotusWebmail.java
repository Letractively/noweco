package com.googlecode.noweco.core.webmail.lotus;

import java.io.IOException;

import org.apache.http.HttpHost;

import com.googlecode.noweco.core.webmail.Webmail;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.BullWebmailPortalConnector;
import com.googlecode.noweco.core.webmail.portal.PortalConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class LotusWebmail implements Webmail {

    private HttpHost proxy;

    private PortalConnector portailConnector;

    public LotusWebmail(String proxyHost, int proxyPort) {
        this(new HttpHost(proxyHost, proxyPort, "http"));
    }

    public LotusWebmail() {
        this(null);
    }

    protected LotusWebmail(HttpHost proxy) {
        this.proxy = proxy;
        portailConnector = new BullWebmailPortalConnector();
    }

    public WebmailConnection connect(String user, String password) throws IOException {
        PortalConnection connect = portailConnector.connect(proxy, user, password);
        return new LotusWebmailConnection(connect.getHttpClient(), connect.getHttpHost(), connect.getPath());
    }

    public void release() {
    }

}
