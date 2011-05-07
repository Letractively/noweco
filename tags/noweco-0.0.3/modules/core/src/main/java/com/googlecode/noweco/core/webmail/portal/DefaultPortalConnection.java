package com.googlecode.noweco.core.webmail.portal;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

public class DefaultPortalConnection implements PortalConnection {

    private HttpClient httpClient;

    private HttpHost httpHost;

    private String path;

    public DefaultPortalConnection(HttpClient httpClient, HttpHost httpHost, String path) {
        this.httpClient = httpClient;
        this.httpHost = httpHost;
        this.path = path;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public String getPath() {
        return path;
    }

}
