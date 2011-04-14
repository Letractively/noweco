package com.googlecode.noweco.core.httpclient.unsecure;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public class UnsecureHttpClientFactory {

    public static UnsecureHttpClientFactory INSTANCE = new UnsecureHttpClientFactory();

    private UnsecureHttpClientFactory() {
    }

    public DefaultHttpClient createUnsecureHttpClient(HttpHost proxy) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        SchemeRegistry schemeRegistry = httpclient.getConnectionManager().getSchemeRegistry();
        schemeRegistry.unregister("https");
        try {
            SSLContext instance = SSLContext.getInstance("TLS");
            TrustManager tm = UnsecureX509TrustManager.INSTANCE;
            instance.init(null, new TrustManager[] { tm }, null);
            schemeRegistry.register(new Scheme("https", 443, new SSLSocketFactory(instance, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)));
        } catch (Exception e) {
        }
        httpclient.removeResponseInterceptorByClass(ResponseProcessCookies.class);
        httpclient.addResponseInterceptor(new UnsecureResponseProcessCookies());
        if (proxy != null) {
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        return httpclient;
    }

}
