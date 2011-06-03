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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.httpclient.unsecure.UnsecureHttpClientFactory;

/**
 *
 * @author Gael Lalire
 */
public class BullWebmailFrecPortalConnector implements PortalConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(BullWebmailFrecPortalConnector.class);

    private static final Pattern DATAS = Pattern.compile("name=\"Datas\"\\s*value=\"([^\"]*)\"");
    private static final Pattern LAST_CNX = Pattern.compile("name=\"lastCnx\"\\s*value=\"([^\"]*)\"");

    public PortalConnection connect(final HttpHost proxy, final String user, final String password) throws IOException {
        DefaultHttpClient httpclient = UnsecureHttpClientFactory.INSTANCE.createUnsecureHttpClient(proxy);

        // prepare the request
        HttpPost httpost = new HttpPost("https://bullsentry3.bull.net:443/cgi/wway_authent?TdsName=PILX");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Internet", "1"));
        nvps.add(new BasicNameValuePair("WebAgt", "1"));
        nvps.add(new BasicNameValuePair("UrlConnect", "https://telefrec.bull.fr/horde/"));
        nvps.add(new BasicNameValuePair("Service", "WEBMAIL-FREC"));
        nvps.add(new BasicNameValuePair("Action", "go"));
        nvps.add(new BasicNameValuePair("lng", "EN"));
        nvps.add(new BasicNameValuePair("stdport", "80"));
        nvps.add(new BasicNameValuePair("sslport", "443"));
        nvps.add(new BasicNameValuePair("user", user));
        nvps.add(new BasicNameValuePair("cookie", password));
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        // send the request
        HttpResponse rsp = httpclient.execute(httpost);
        int statusCode = rsp.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException("Unable to connect to bull portail, status code : " + statusCode);
        }

        if (LOGGER.isDebugEnabled()) {
            for (Cookie c : httpclient.getCookieStore().getCookies()) {
                LOGGER.debug("Authent Cookie {}", c);
            }
        }

        // free result resources
        HttpEntity entity = rsp.getEntity();
        String firstEntity = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(firstEntity);
            }
            EntityUtils.consume(entity);
        }

        // STEP 2 : WEBMAIL-FREC

        httpost = new HttpPost("https://bullsentry3.bull.fr:443/cgi/wway_cookie");
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("TdsName", "PILX"));
        nvps.add(new BasicNameValuePair("Proto", "s"));
        nvps.add(new BasicNameValuePair("Port", "443"));
        nvps.add(new BasicNameValuePair("Cgi", "cgi"));
        nvps.add(new BasicNameValuePair("Mode", "set"));
        nvps.add(new BasicNameValuePair("Total", "3"));
        nvps.add(new BasicNameValuePair("Current", "1"));
        nvps.add(new BasicNameValuePair("ProtoR", "s"));
        nvps.add(new BasicNameValuePair("PortR", "443"));
        nvps.add(new BasicNameValuePair("WebAgt", "1"));
        nvps.add(new BasicNameValuePair("UrlConnect", "https://telefrec.bull.fr/horde/"));
        nvps.add(new BasicNameValuePair("Service", "WEBMAIL-FREC"));

        Matcher datasMatcher = DATAS.matcher(firstEntity);
        if (!datasMatcher.find()) {
            throw new IOException("Unable to find Datas");
        }
        nvps.add(new BasicNameValuePair("Datas", datasMatcher.group(1)));

        Matcher lastCnxMatcher = LAST_CNX.matcher(firstEntity);
        if (!lastCnxMatcher.find()) {
            throw new IOException("Unable to find lastCnx");
        }
        nvps.add(new BasicNameValuePair("lastCnx", lastCnxMatcher.group(1)));
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        // send the request
        rsp = httpclient.execute(httpost);
        statusCode = rsp.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException("Unable to connect to bull portail, status code : " + statusCode);
        }

        if (LOGGER.isDebugEnabled()) {
            for (Cookie c : httpclient.getCookieStore().getCookies()) {
                LOGGER.debug("Apps Cookie {}", c);
            }
        }

        // free result resources
        entity = rsp.getEntity();
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(EntityUtils.toString(entity));
            }
            EntityUtils.consume(entity);
        }

        // STEP 3 VIEW ROOT PAGE

        HttpGet httpGet = new HttpGet("https://telefrec.bull.fr/horde/imp/mailbox.php?mailbox=INBOX");
        rsp = httpclient.execute(httpGet);

        entity = rsp.getEntity();
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(EntityUtils.toString(entity));
            }
            EntityUtils.consume(entity);
        }

        return new DefaultPortalConnection(httpclient, new HttpHost("telefrec.bull.fr", 443, "https"), "");
    }

}