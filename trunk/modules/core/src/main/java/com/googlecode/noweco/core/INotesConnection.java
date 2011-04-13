package com.googlecode.noweco.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class INotesConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(INotesConnection.class);

    private HttpClient httpclient;

    private HttpHost target;

    protected static HttpHost createHost(boolean secure, String host) {
        if(secure) {
            return new HttpHost(host, 443, "https");
        }
        return new HttpHost(host, 80, "http");
    }

    public INotesConnection(String proxyHost, int proxyPort, boolean secure, String host) {
        this(new HttpHost(proxyHost, proxyPort, "http"), createHost(secure, host));
    }

    public INotesConnection(boolean secure, String host) {
        this(null, createHost(secure, host));
    }

    protected INotesConnection(HttpHost proxy, HttpHost target) {
        httpclient = new DefaultHttpClient();
        if (proxy != null) {
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        this.target = target;
    }

    public void connect(String loginPath, String user, String password) throws IOException {
        HttpGet req = new HttpGet(loginPath);

        HttpResponse rsp = httpclient.execute(target, req);
        HttpEntity entity = rsp.getEntity();

        LOGGER.info("----------------------------------------");
        LOGGER.info(rsp.getStatusLine().toString());
        Header[] headers = rsp.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            LOGGER.info(headers[i].toString());
        }
        LOGGER.info("----------------------------------------");

        if (entity != null) {
            LOGGER.info(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        }

        // LOGIN

        //  "login-form-type=pwd&" + "goto=/portal/dt&" + "gotoOnFail=/portal/dt?error=true"
        HttpPost httpost = new HttpPost("/pkmslogin.form");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("login-form-type", "pwd"));
        nvps.add(new BasicNameValuePair("username", user));
        nvps.add(new BasicNameValuePair("password", password));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        rsp = httpclient.execute(target, httpost);
        entity = rsp.getEntity();

        if (entity != null) {
            LOGGER.info(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        }


        /*
        <form id="loginActionForm" name="login" onsubmit="return true;" action="/pkmslogin.form" method="post">
        <input type="hidden" name="login-form-type" value="pwd" />

        <label id="loginActionForm_eMail" for="username">E-mail address:</label>
        <input type="text" name="username" id="username" class="text" />

<label id="loginActionForm_Password" for="password">Password:</label>
<input type="password" name="password" id="password" class="text" onkeypress="keyEvent(event, this)"/>

</form>
*/


        // FORM LOGIN
//        HttpGet httpget = new HttpGet("https://portal.sun.com/portal/dt");
//
//        HttpResponse response = httpclient.execute(httpget);
//        HttpEntity entity = response.getEntity();
//
//        System.out.println("Login form get: " + response.getStatusLine());
//        EntityUtils.consume(entity);
//
//        System.out.println("Initial set of cookies:");
//        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
//        if (cookies.isEmpty()) {
//            System.out.println("None");
//        } else {
//            for (int i = 0; i < cookies.size(); i++) {
//                System.out.println("- " + cookies.get(i).toString());
//            }
//        }
//
//        HttpPost httpost = new HttpPost("https://portal.sun.com/amserver/UI/Login?" +
//                "org=self_registered_users&" +
//                "goto=/portal/dt&" +
//                "gotoOnFail=/portal/dt?error=true");
//
//        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//        nvps.add(new BasicNameValuePair("IDToken1", "username"));
//        nvps.add(new BasicNameValuePair("IDToken2", "password"));
//
//        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
//
//        response = httpclient.execute(httpost);
//        entity = response.getEntity();
//
//        System.out.println("Login form get: " + response.getStatusLine());
//        EntityUtils.consume(entity);
//
//        System.out.println("Post logon cookies:");
//        cookies = httpclient.getCookieStore().getCookies();
//        if (cookies.isEmpty()) {
//            System.out.println("None");
//        } else {
//            for (int i = 0; i < cookies.size(); i++) {
//                System.out.println("- " + cookies.get(i).toString());
//            }
//        }

    }

    public void release() {
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
            httpclient = null;
        }

    }

    @Override
    protected void finalize() {
        try {
            release();
        } catch (Throwable e) {
        }
    }

}
