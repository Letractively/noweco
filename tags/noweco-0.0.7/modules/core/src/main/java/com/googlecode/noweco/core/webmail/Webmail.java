package com.googlecode.noweco.core.webmail;

import java.io.IOException;

import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public interface Webmail {

    void setAuthent(PortalConnector portalConnector);

    void setProxy(String host, int port);

    WebmailConnection connect(String user, String password) throws IOException;

    void release();

}
