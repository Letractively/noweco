package com.googlecode.noweco.core.httpclient.unsecure;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class UnsecureX509TrustManager implements X509TrustManager {

    public static final X509TrustManager INSTANCE = new UnsecureX509TrustManager();

    private UnsecureX509TrustManager() {
    }

    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
