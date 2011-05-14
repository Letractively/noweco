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

package com.googlecode.noweco.core.webmail.portal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.googlecode.noweco.core.httpclient.unsecure.UnsecureHttpClientFactory;

/**
 *
 * @author Gael Lalire
 */
public class LotusLivePortalConnector implements PortalConnector {

//    private static final Logger LOGGER = LoggerFactory.getLogger(LotusLivePortalConnector.class);

    private static final Pattern SAML_RESPONSE = Pattern.compile("name=\"SAMLResponse\"\\s*value=\"([^\"]*)\"");

    public PortalConnection connect(final HttpHost proxy, final String user, final String password) throws IOException {
        DefaultHttpClient httpclient = UnsecureHttpClientFactory.INSTANCE.createUnsecureHttpClient(proxy);

        HttpGet httpGet;
        HttpPost httpost;
        HttpResponse rsp;
        HttpEntity entity;

        // STEP 1 : Login page

        httpGet = new HttpGet("https://apps.lotuslive.com/manage/account/dashboardHandler/input");
        rsp = httpclient.execute(httpGet);

        entity = rsp.getEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }

        // STEP 2 : Send form

        // prepare the request
        httpost = new HttpPost("https://apps.lotuslive.com/pkmslogin.form");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login-form-type", "pwd"));
        nvps.add(new BasicNameValuePair("username", user));
        nvps.add(new BasicNameValuePair("password", password));
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        // send the request
        rsp = httpclient.execute(httpost);

        // free result resources
        entity = rsp.getEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }

        int statusCode = rsp.getStatusLine().getStatusCode();
        if (statusCode != 302) {
            throw new IOException("Unable to connect to lotus live portail, status code : " + statusCode);
        }

        // STEP 3 : Fetch SAML token

        httpGet = new HttpGet("https://mail.lotuslive.com/mail/loginlanding");
        rsp = httpclient.execute(httpGet);

        entity = rsp.getEntity();
        String firstEntity = EntityUtils.toString(entity);
        if (entity != null) {
            EntityUtils.consume(entity);
        }

        // STEP 4 : Use SAML token to identify

        httpost = new HttpPost("https://mail.lotuslive.com/auth/tfim");
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("TARGET", "http://mail.lotuslive.com/mail"));
        Matcher samlMatcher = SAML_RESPONSE.matcher(firstEntity);
        if (!samlMatcher.find()) {
            throw new IOException("Unable to find SAML token");
        }
        nvps.add(new BasicNameValuePair("SAMLResponse", samlMatcher.group(1)));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        // send the request
        rsp = httpclient.execute(httpost);

        entity = rsp.getEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }

        // STEP 5 : VIEW ROOT PAGE (TODO can delete ?)

        httpGet = new HttpGet("https://mail-usw.lotuslive.com/mail/mail/listing/INBOX");
        rsp = httpclient.execute(httpGet);

        entity = rsp.getEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }

        return new DefaultPortalConnection(httpclient, new HttpHost("mail-usw.lotuslive.com", 443), "");
    }

    public String getPathPrefix() {
        return "";
    }
}
