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

package com.googlecode.noweco.calendar.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gael Lalire
 */
public class CalendarClientTest {


    public DefaultHttpClient getGoogleAccess() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        return httpclient;
    }

    private boolean google = false;

    private boolean chandlerproject = false;

    private boolean apple = true;

    // https://hub.chandlerproject.org/dav/collection/8de93530-8796-11e0-82b8-d279848d8f3e

    @Test
    @Ignore
    public void test() throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();

//        if (google || chandlerproject) {
//            HttpParams params = httpclient.getParams();
//            ConnRouteParams.setDefaultProxy(params, new HttpHost("ecprox.bull.fr"));
//        }

        HttpEntityEnclosingRequestBase httpRequestBase = new HttpEntityEnclosingRequestBase() {


            @Override
            public String getMethod() {
                return "PROPFIND";
            }
        };

        BasicHttpEntity entity = new BasicHttpEntity();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
        //
        outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?><D:propfind xmlns:D=\"DAV:\">   <D:prop>  <D:displayname/>  <D:principal-collection-set/> <calendar-home-set xmlns=\"urn:ietf:params:xml:ns:caldav\"/>   </D:prop> </D:propfind>");
        outputStreamWriter.close();
        entity.setContent(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        httpRequestBase.setEntity(entity);
        httpRequestBase.setURI(new URI("/dav/collection/gael.lalire@bull.com/"));
        if (google) {
            httpRequestBase.setURI(new URI("/calendar/dav/gael.lalire@gmail.com/user/"));
        }
        if (chandlerproject) {
            httpRequestBase.setURI(new URI("/dav/collection/8de93530-8796-11e0-82b8-d279848d8f3e"));
        }
        if (apple) {
            httpRequestBase.setURI(new URI("/"));
        }
        HttpHost target = new HttpHost("localhost", 8080);
        if (google) {
            target = new HttpHost("www.google.com", 443, "https");
        }
        if (chandlerproject) {
            target = new HttpHost("hub.chandlerproject.org", 443, "https");
        }
        if (apple) {
            target = new HttpHost("localhost", 8008, "http");
        }
        httpRequestBase.setHeader("Depth", "0");
        String userpass = null;
        if (apple) {
            userpass = "admin:admin";
        }
        httpRequestBase.setHeader("authorization", "Basic " + Base64.encodeBase64String(userpass.getBytes()));
        HttpResponse execute = httpclient.execute(target, httpRequestBase);
        System.out.println(Arrays.deepToString(execute.getAllHeaders()));
        System.out.println(execute.getStatusLine());
        System.out.println(EntityUtils.toString(execute.getEntity()));
    }
}
