package com.googlecode.noweco.core.webmail.portal;

import java.io.IOException;

import org.apache.http.HttpHost;

public interface PortalConnector {

    PortalConnection connect(HttpHost proxy, String user, String password) throws IOException;

}
