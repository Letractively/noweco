package com.googlecode.noweco.core.webmail.horde;

import java.io.IOException;

import org.apache.http.HttpHost;

import com.googlecode.noweco.core.webmail.PortalConnector;
import com.googlecode.noweco.core.webmail.Webmail;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.BullWebmailFrecPortalConnector;

public class HordeWebmail implements Webmail {

    private HttpHost proxy;

    private HttpHost host;

    private PortalConnector portailConnector;

    protected static HttpHost createHost(boolean secure, String host) {
        if (secure) {
            return new HttpHost(host, 443, "https");
        }
        return new HttpHost(host, 80, "http");
    }

    public HordeWebmail(String proxyHost, int proxyPort, boolean secure, String host) {
        this(new HttpHost(proxyHost, proxyPort, "http"), createHost(secure, host));
    }

    public HordeWebmail(boolean secure, String host) {
        this(null, createHost(secure, host));
    }

    protected HordeWebmail(HttpHost proxy, HttpHost host) {
        this.proxy = proxy;
        this.host = host;
        portailConnector = new BullWebmailFrecPortalConnector();
    }

    public WebmailConnection connect(String user, String password) throws IOException {
        return new HordeWebmailConnection(portailConnector.connect(proxy, user, password), host);
    }

}
