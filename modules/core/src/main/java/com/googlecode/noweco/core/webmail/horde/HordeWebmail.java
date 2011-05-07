package com.googlecode.noweco.core.webmail.horde;

import java.io.IOException;

import org.apache.http.HttpHost;

import com.googlecode.noweco.core.webmail.Webmail;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class HordeWebmail implements Webmail {

    private HttpHost proxy;

    private PortalConnector portalConnector;

    public HordeWebmail() {
    }

    public WebmailConnection connect(String user, String password) throws IOException {
        PortalConnection connect = portalConnector.connect(proxy, user, password);
        return new HordeWebmailConnection(connect.getHttpClient(), connect.getHttpHost());
    }

    public void release() {
    }

    public void setAuthent(PortalConnector portalConnector) {
        this.portalConnector = portalConnector;
    }

    public void setProxy(String host, int port) {
        this.proxy = new HttpHost(host, port, "http");
    }

}
