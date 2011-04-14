package com.googlecode.noweco.core.httpclient.unsecure;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
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

    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }

    public void verify(String host, SSLSocket ssl) throws IOException {

    }

    public void verify(String host, X509Certificate cert) throws SSLException {

    }

    public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

    }

}
