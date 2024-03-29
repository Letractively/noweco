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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 *
 * @author Gael Lalire
 */
public final class UnsecureX509TrustManager implements X509TrustManager {

    public static final X509TrustManager INSTANCE = new UnsecureX509TrustManager();

    private UnsecureX509TrustManager() {
    }

    public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {

    }

    public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {

    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
