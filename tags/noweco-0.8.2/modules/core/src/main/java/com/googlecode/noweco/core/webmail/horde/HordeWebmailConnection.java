package com.googlecode.noweco.core.webmail.horde;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.webmail.Message;
import com.googlecode.noweco.core.webmail.Page;
import com.googlecode.noweco.core.webmail.WebmailConnection;

public class HordeWebmailConnection implements WebmailConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(HordeWebmailConnection.class);

    private HttpClient httpclient;

    private HttpHost host;

    public HordeWebmailConnection(HttpClient httpclient, HttpHost host) {
        this.httpclient = httpclient;
        this.host = host;
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

    public String getContent(int id) throws IOException {
        HttpGet httpGet = new HttpGet("/horde/imp/view.php?index=" + id + "&mailbox=INBOX&actionID=view_source&id=0");
        HttpResponse response = httpclient.execute(host, httpGet);
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        if (entity != null) {
            EntityUtils.consume(entity);
        }
        return content;
    }

    public int getPageCount() throws IOException {
        if (pageCount == null) {
            refresh();
        }
        return pageCount;
    }

    private static final Pattern MESSAGES_PATTERN = Pattern.compile("var messagelist = new Array\\(\"([^)]*)\"\\)");

    public List<? extends Message> getMessages(int page) throws IOException {
        HttpGet httpGet = new HttpGet("/horde/imp/mailbox.php?page=" + (page + 1));
        HttpResponse rsp = httpclient.execute(host, httpGet);

        HttpEntity entity = rsp.getEntity();
        String pageContent = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(pageContent);
            }
            EntityUtils.consume(entity);
        }
        Matcher matcher = MESSAGES_PATTERN.matcher(pageContent);
        if (!matcher.find()) {
            throw new IOException("Invalid page content, message list not found");
        }
        String[] split = matcher.group(1).split("\",\\s*\"");
        List<Message> messages = new ArrayList<Message>(split.length);
        for (String s : split) {
            messages.add(new HordeMessage(this, Integer.parseInt(s), 20));
        }
        return messages;
    }

    private static final Pattern PAGE_MAX_PATTERN = Pattern.compile(Pattern.quote("<span style=\"width:35%; text-align:center\" class=\"leftFloat nowrap\">")
            + "[^<]*\\s(\\d+)\\s*" + Pattern.quote("</span>"));

    private Integer pageCount;

    public void refresh() throws IOException {
        HttpGet httpGet = new HttpGet("/horde/imp/mailbox.php?mailbox=INBOX");
        HttpResponse rsp = httpclient.execute(host, httpGet);

        HttpEntity entity = rsp.getEntity();
        String content = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(content);
            }
            EntityUtils.consume(entity);
        }
        Matcher matcher = PAGE_MAX_PATTERN.matcher(content);
        if (!matcher.find()) {
            throw new IOException("Invalid page content, max page not found");
        }
        pageCount = Integer.parseInt(matcher.group(1));
    }

    public Iterator<Page> getPages() throws IOException {
        return null;
    }

    public List<String> delete(List<String> messageUids) throws IOException {
        throw new IOException("NYI");
    }

}
