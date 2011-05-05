package com.googlecode.noweco.core.webmail.lotus;

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

public class LotusWebmailConnection implements WebmailConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotusWebmailConnection.class);

    private HttpClient httpclient;

    private HttpHost host;

    private String prefix;

    private String pagePrefix;

    private static final String MIME_SUFFIX = "/?OpenDocument&Form=l_MailMessageHeader&PresetFields=FullMessage;1";

    private static final Pattern MAIN_PAGE_PATTERN = Pattern.compile("location.replace\\(\"([^\"]*?&AutoFramed)\"\\);");

    public LotusWebmailConnection(HttpClient httpclient, HttpHost host, String prefix) throws IOException {
        this.prefix = prefix;

        HttpGet httpGet = new HttpGet(prefix + "/($Inbox)?OpenView");
        HttpResponse rsp = httpclient.execute(host, httpGet);

        HttpEntity entity = rsp.getEntity();
        String string = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(string);
            }
            EntityUtils.consume(entity);
        }

        Matcher matcher = MAIN_PAGE_PATTERN.matcher(string);
        if (!matcher.find()) {
            throw new IOException("Unable to parse main page");
        }
        pagePrefix = matcher.group(1);

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

    public String getContent(String id) throws IOException {
        HttpGet httpGet = new HttpGet(prefix + "/($Inbox)/" + id + MIME_SUFFIX);
        HttpResponse response = httpclient.execute(host, httpGet);
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        if (entity != null) {
            EntityUtils.consume(entity);
        }
        return content;
    }

    private static final Pattern MESSAGE_PATTERN = Pattern
            .compile("(?s)<tr[^>]*>\\s*<td.*?</td>\\s*<td.*?<input\\s.*?value=\"([^\"]*)\".*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?(\\d+)K.*?</td>.*?</tr>");

    public List<? extends Message> getMessages(int index) throws IOException {
        HttpGet httpGet = new HttpGet(pagePrefix + "&Start=" + index);
        HttpResponse rsp = httpclient.execute(host, httpGet);

        HttpEntity entity = rsp.getEntity();
        String pageContent = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(pageContent);
            }
            EntityUtils.consume(entity);
        }

        List<Message> messages = new ArrayList<Message>();

        Matcher matcher = MESSAGE_PATTERN.matcher(pageContent);
        int start = 0;
        while (matcher.find(start)) {
            int octets = Integer.parseInt(matcher.group(2)) * 1000;
            if (octets == 0) {
                octets = 1;
            }
            messages.add(new LotusMessage(this, matcher.group(1), octets));

            start = matcher.end();
        }

        return messages;
    }

    public Iterator<Page> getPages() throws IOException {
        return new LotusPagesIterator(this);
    }

}
