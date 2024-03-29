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

package com.googlecode.noweco.webmail.portal.bull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.httpclient.UnsecureHttpClientFactory;
import com.googlecode.noweco.webmail.portal.DefaultPortalConnection;
import com.googlecode.noweco.webmail.portal.PortalConnection;
import com.googlecode.noweco.webmail.portal.PortalConnector;

/**
 *
 * @author Gael Lalire
 */
public class BullWebmailPortalConnector implements PortalConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(BullWebmailPortalConnector.class);

    private static final Pattern DATAS = Pattern.compile("name=\"Datas\"\\s*value=\"([^\"]*)\"");
    private static final Pattern NEW_PASSWD = Pattern.compile("name=\"newpsw\"");
    private static final Pattern LAST_CNX = Pattern.compile("name=\"lastCnx\"\\s*value=\"([^\"]*)\"");

    private String authent(final DefaultHttpClient httpclient, final String user, final String password) throws IOException {
        // prepare the request
        HttpPost httpost = new HttpPost("https://bullsentry.bull.net:443/cgi/wway_authent?TdsName=PILX");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Internet", "1"));
        nvps.add(new BasicNameValuePair("WebAgt", "1"));
        nvps.add(new BasicNameValuePair("UrlConnect", "https://telemail.bull.fr:443/"));
        nvps.add(new BasicNameValuePair("Service", "WEB-MAIL"));
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

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("STEP 1 Authent Cookie : {}", httpclient.getCookieStore().getCookies());
        }

        // free result resources
        HttpEntity entity = rsp.getEntity();
        String firstEntity = EntityUtils.toString(entity);
        if (entity != null) {
            LOGGER.trace("STEP 1 Entity : {}", firstEntity);
            EntityUtils.consume(entity);
        }
        return firstEntity;
    }

    public PortalConnection connect(final HttpHost proxy, final String user, final String password) throws IOException {
        DefaultHttpClient httpclient = UnsecureHttpClientFactory.INSTANCE.createUnsecureHttpClient(proxy);
        HttpPost httpost;
        List<NameValuePair> nvps;
        HttpResponse rsp;
        int statusCode;
        HttpEntity entity;

        // mailbox does not appear with no FR language
        // with Mozilla actions are simple
        new ClientParamBean(httpclient.getParams()).setDefaultHeaders(Arrays.asList((Header) new BasicHeader("Accept-Language",
                "fr-fr,fr;q=0.8,en;q=0.5,en-us;q=0.3"), new BasicHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1")));

        String firstEntity = authent(httpclient, user, password);
        Matcher datasMatcher = DATAS.matcher(firstEntity);
        if (!datasMatcher.find()) {
            Matcher matcher = NEW_PASSWD.matcher(firstEntity);
            if (matcher.find()) {
                // try a bad password
                authent(httpclient, user, password + "BAD");
                // and retry good password (expired)
                firstEntity = authent(httpclient, user, password);
                datasMatcher = DATAS.matcher(firstEntity);
                if (!datasMatcher.find()) {
                    throw new IOException("Unable to find Datas, after bad password try");
                }
            } else {
                throw new IOException("Unable to find Datas");
            }
        }

        // STEP 2 : WEB-MAIL

        httpost = new HttpPost("https://bullsentry.bull.fr:443/cgi/wway_cookie");
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
        nvps.add(new BasicNameValuePair("UrlConnect", "https://telemail.bull.fr:443/"));
        nvps.add(new BasicNameValuePair("Service", "WEB-MAIL"));
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

        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("STEP 2 Apps Cookie : {}", httpclient.getCookieStore().getCookies());
        }

        // free result resources
        entity = rsp.getEntity();
        if (entity != null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("STEP 2 Entity : {}", EntityUtils.toString(entity));
            }
            EntityUtils.consume(entity);
        }

        // STEP 3 : telemail

        HttpGet httpGet = new HttpGet("https://telemail.bull.fr:443/HomePage.nsf");
        rsp = httpclient.execute(httpGet);

        entity = rsp.getEntity();
        String secondEntity = EntityUtils.toString(entity);
        if (entity != null) {
            LOGGER.trace("STEP 3 Entity : {}", secondEntity);
            EntityUtils.consume(entity);
        }

        Matcher nsfMatcher = PATTERN.matcher(secondEntity);
        if (!nsfMatcher.find()) {
            throw new IOException("Unable to find nsf");
        }

        String pathPrefix = nsfMatcher.group(3).replace('\\', '/');
        LOGGER.debug("pathPrefix : {}", pathPrefix);

        String protocol = nsfMatcher.group(1);
        int port;
        if ("http".equals(protocol)) {
            port = 80;
        } else if ("https".equals(protocol)) {
            port = 443;
        } else {
            throw new IOException("Unknown protocol " + protocol);
        }
        return new DefaultPortalConnection(httpclient, new HttpHost(nsfMatcher.group(2), port, protocol), pathPrefix);
    }

    private static final Pattern PATTERN = Pattern.compile("url=(https?)://([^/]*)(/.*?\\.nsf)");

}
