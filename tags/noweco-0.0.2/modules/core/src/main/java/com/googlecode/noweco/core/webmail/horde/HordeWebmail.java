package com.googlecode.noweco.core.webmail.horde;

import java.io.IOException;

import org.apache.http.HttpHost;

import com.googlecode.noweco.core.webmail.Webmail;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.BullWebmailFrecPortalConnector;
import com.googlecode.noweco.core.webmail.portal.PortalConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class HordeWebmail implements Webmail {

    private HttpHost proxy;

    private PortalConnector portailConnector;

    public HordeWebmail(String proxyHost, int proxyPort) {
        this(new HttpHost(proxyHost, proxyPort, "http"));
    }

    public HordeWebmail() {
        this(null);
    }

    protected HordeWebmail(HttpHost proxy) {
        this.proxy = proxy;
        portailConnector = new BullWebmailFrecPortalConnector();
    }

    public WebmailConnection connect(String user, String password) throws IOException {
        PortalConnection connect = portailConnector.connect(proxy, user, password);
        return new HordeWebmailConnection(connect.getHttpClient(), connect.getHttpHost());
    }

    public void release() {
    }

}
