package com.googlecode.noweco.core.webmail.lotus;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.PortalConnector;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.BullWebmailFrecPortalConnector;

public class INotesWebmailConnection implements WebmailConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(INotesWebmailConnection.class);

    private HttpClient httpclient;

    private HttpHost proxy;

    private HttpHost host;

    private PortalConnector portailConnector;

    protected static HttpHost createHost(boolean secure, String host) {
        if(secure) {
            return new HttpHost(host, 443, "https");
        }
        return new HttpHost(host, 80, "http");
    }

    public INotesWebmailConnection(String proxyHost, int proxyPort, boolean secure, String host) {
        this(new HttpHost(proxyHost, proxyPort, "http"), createHost(secure, host));
    }

    public INotesWebmailConnection(boolean secure, String host) {
        this(null, createHost(secure, host));
    }

    protected INotesWebmailConnection(HttpHost proxy, HttpHost host) {
        this.proxy = proxy;
        this.host = host;
        portailConnector = new BullWebmailFrecPortalConnector();
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

    public void refresh() throws IOException {
        // TODO Auto-generated method stub

    }

    public int getPageCount() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<? extends Message> getMessages(int page) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
