/*
 * Copyright 2011 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.noweco.webmail.httpclient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 *
 * @author Gael Lalire
 */
public final class UnsecureHttpClientFactory {

    public static final UnsecureHttpClientFactory INSTANCE = new UnsecureHttpClientFactory();

    private UnsecureHttpClientFactory() {
    }

    public DefaultHttpClient createUnsecureHttpClient(final HttpHost proxy) {
        DefaultHttpClient httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        SchemeRegistry schemeRegistry = httpclient.getConnectionManager().getSchemeRegistry();
        schemeRegistry.unregister("https");
        try {
            SSLContext instance = SSLContext.getInstance("TLS");
            TrustManager tm = UnsecureX509TrustManager.INSTANCE;
            instance.init(null, new TrustManager[] { tm }, null);
            schemeRegistry.register(new Scheme("https", 443, new SSLSocketFactory(instance, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)));
        } catch (Exception e) {
            throw new RuntimeException("TLS issue", e);
        }
        httpclient.removeResponseInterceptorByClass(ResponseProcessCookies.class);
        httpclient.addResponseInterceptor(new UnsecureResponseProcessCookies());
        HttpParams params = httpclient.getParams();
        if (proxy != null) {
            ConnRouteParams.setDefaultProxy(params, proxy);
        }
        HttpConnectionParams.setSoTimeout(params, 7000);
        return httpclient;
    }

}
