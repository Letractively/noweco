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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
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

    private String garbagePrefix;

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

        httpGet = new HttpGet(prefix + "/($Trash)/?OpenView");
        rsp = httpclient.execute(host, httpGet);

        entity = rsp.getEntity();
        string = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(string);
            }
            EntityUtils.consume(entity);
        }

        matcher = MAIN_PAGE_PATTERN.matcher(string);
        if (!matcher.find()) {
            throw new IOException("Unable to parse garbage page");
        }
        garbagePrefix = matcher.group(1);

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

    private static final Pattern CONNECTED_PATTERN = Pattern.compile("<form\\s*name=\"_DominoForm\"");

    private static final Pattern MESSAGE_PATTERN = Pattern
            .compile("(?s)<tr[^>]*>\\s*<td.*?</td>\\s*<td.*?<input\\s.*?value=\"([^\"]*)\".*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?(\\d+)(?:[,.](\\d+))?([KM]).*?</td>.*?</tr>");

    public List<? extends Message> getMessages(int index) throws IOException {
        String pageContent = loadPageContent(index);

        List<Message> messages = new ArrayList<Message>();

        Matcher matcher = MESSAGE_PATTERN.matcher(pageContent);
        int start = 0;
        while (matcher.find(start)) {
            int enOctet = 1;
            switch (matcher.group(4).charAt(0)) {
            case 'K':
                enOctet = 1024;
                break;
            case 'M':
                enOctet = 1024 * 1024;
            }

            double value = Integer.parseInt(matcher.group(2));
            String afterCommaValue = matcher.group(3);
            if (afterCommaValue != null) {
                value += Double.parseDouble("0." + afterCommaValue);
            }
            int octets = (int) (value * enOctet);
            if (octets == 0) {
                octets = 1;
            }
            messages.add(new LotusMessage(this, matcher.group(1), octets));

            start = matcher.end();
        }

        return messages;
    }

    public String loadPageContent(int index) throws IOException, ClientProtocolException {
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

        if (!CONNECTED_PATTERN.matcher(pageContent).find()) {
            throw new IOException("Page content is not valid");
        }
        return pageContent;
    }

    public Iterator<Page> getPages() throws IOException {
        return new LotusPagesIterator(this);
    }

    private static final Pattern TEMP_RAND_NUM_PATTERN = Pattern.compile("<input name=\"tmpRandNum\"[^>]*value=\"([^\"]*)\">");

    private static final Pattern DELETE_DELETE_PATTERN = Pattern.compile("_doClick\\('([^/]*/\\$V5ACTIONS/4\\.7C1A)'");

    private static final Pattern CLEAR_GARBAGE_PATTERN = Pattern.compile("_doClick\\('([^/]*/\\$V5ACTIONS/0\\.FFC)'");

    public void delete(List<String> messageUids) throws IOException {
        List<String> toDelete = new ArrayList<String>();
        int page = 0;
        int messageCount;
        do {
            messageCount = 0;
            int index = 1 + page * 30;
            String pageContent = loadPageContent(index);

            toDelete.clear();

            Matcher matcherDelete = DELETE_DELETE_PATTERN.matcher(pageContent);
            if (!matcherDelete.find()) {
                throw new IOException("No DELETE/DELETE find");
            }

            Matcher matcher = MESSAGE_PATTERN.matcher(pageContent);
            int start = 0;
            while (matcher.find(start)) {
                String group = matcher.group(1);
                if (messageUids.contains(group)) {
                    toDelete.add(group);
                }
                start = matcher.end();
                messageCount++;
            }
            HttpPost httpPost = new HttpPost(pagePrefix + "&Start=" + index);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("__Click", matcherDelete.group(1)));
            nvps.add(new BasicNameValuePair("%%Surrogate_$$SelectDestFolder", "1"));
            nvps.add(new BasicNameValuePair("$$SelectDestFolder", "--Aucun dossier disponible--"));
            for (String doc : toDelete) {
                nvps.add(new BasicNameValuePair("$$SelectDoc", doc));
            }
            Matcher matcherRandNum = TEMP_RAND_NUM_PATTERN.matcher(pageContent);
            if (!matcherRandNum.find()) {
                throw new IOException("No rand num find");
            }
            nvps.add(new BasicNameValuePair("tmpRandNum", matcherRandNum.group(1)));
            nvps.add(new BasicNameValuePair("viewName", "($Inbox)"));
            nvps.add(new BasicNameValuePair("folderError", ""));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            HttpResponse rsp = httpclient.execute(host, httpPost);
            EntityUtils.consume(rsp.getEntity());
            page++;
        } while (messageCount == 30);

        // clear garbage

        HttpGet httpGet = new HttpGet(garbagePrefix);
        HttpResponse rsp = httpclient.execute(host, httpGet);

        HttpEntity entity = rsp.getEntity();
        String pageContent = EntityUtils.toString(entity);
        if (entity != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(pageContent);
            }
            EntityUtils.consume(entity);
        }

        Matcher matcherGarbage = CLEAR_GARBAGE_PATTERN.matcher(pageContent);
        if (!matcherGarbage.find()) {
            throw new IOException("No Garbage/Clear find");
        }

        HttpPost httpPost = new HttpPost(garbagePrefix);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("__Click", matcherGarbage.group(1)));
        Matcher matcherRandNum = TEMP_RAND_NUM_PATTERN.matcher(pageContent);
        if (!matcherRandNum.find()) {
            throw new IOException("No rand num find");
        }
        nvps.add(new BasicNameValuePair("tmpRandNum", matcherRandNum.group(1)));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        rsp = httpclient.execute(host, httpPost);
        EntityUtils.consume(rsp.getEntity());
    }

}
