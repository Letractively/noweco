package com.googlecode.noweco.core.webmail.lotus;

import java.io.IOException;

import org.apache.http.HttpHost;

import com.googlecode.noweco.core.webmail.Webmail;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class LotusWebmail implements Webmail {

    private HttpHost proxy;

    private PortalConnector portalConnector;

    public WebmailConnection connect(String user, String password) throws IOException {
        PortalConnection connect = portalConnector.connect(proxy, user, password);
        return new LotusWebmailConnection(connect.getHttpClient(), connect.getHttpHost(), connect.getPath());
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
