package com.googlecode.noweco.core.webmail.portal;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

public interface PortalConnection {

    HttpClient getHttpClient();

    HttpHost getHttpHost();

    String getPath();

}
