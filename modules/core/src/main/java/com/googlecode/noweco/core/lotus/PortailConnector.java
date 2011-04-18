package com.googlecode.noweco.core.lotus;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

public interface PortailConnector {

    HttpClient connect(HttpHost proxy, String user, String password) throws IOException;

}
