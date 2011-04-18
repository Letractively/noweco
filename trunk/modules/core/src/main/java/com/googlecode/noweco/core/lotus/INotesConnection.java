package com.googlecode.noweco.core.lotus;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.lotus.portailconnector.BullPortailConnector;

public class INotesConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(INotesConnection.class);

    private HttpClient httpclient;

    private HttpHost proxy;

    private HttpHost host;

    private PortailConnector portailConnector;

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

    protected INotesConnection(HttpHost proxy, HttpHost host) {
        this.proxy = proxy;
        this.host = host;
        portailConnector = new BullPortailConnector();
    }

    public void connect(String loginPath, String user, String password) throws IOException {
        httpclient = portailConnector.connect(proxy, user, password);
        LOGGER.info("Get on {}", loginPath);
//        HttpGet httpGet = new HttpGet("/horde/imp/message.php?index=42652");
        HttpGet httpGet = new HttpGet("/horde/imp/view.php?index=42652&mailbox=INBOX&actionID=view_source&id=0");
        HttpResponse response = httpclient.execute(host, httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            LOGGER.info(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        }
    }

    public void listMessages() throws IOException {

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
