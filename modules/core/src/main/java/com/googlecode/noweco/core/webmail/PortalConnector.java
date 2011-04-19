package com.googlecode.noweco.core.webmail;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

public interface PortalConnector {

    HttpClient connect(HttpHost proxy, String user, String password) throws IOException;

}
