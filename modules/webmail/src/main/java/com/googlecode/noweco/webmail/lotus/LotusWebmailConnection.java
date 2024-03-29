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

package com.googlecode.noweco.webmail.lotus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.WebmailMessage;
import com.googlecode.noweco.webmail.WebmailPages;

/**
 * @author Gael Lalire
 */
public class LotusWebmailConnection implements WebmailConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotusWebmailConnection.class);

    public static final int MAX_MESSAGE = 30;

    private HttpClient httpclient;

    private HttpHost host;

    private String prefix;

    private String pagePrefix;

    private static final String MIME_SUFFIX = "/?OpenDocument&Form=l_MailMessageHeader&PresetFields=FullMessage;1";

    private static final Pattern MAIN_PAGE_PATTERN = Pattern.compile("location.replace\\(\"([^\"]*?&AutoFramed)\"\\);");

    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("(\\w+)(?:-(\\w+))?");

    @SuppressWarnings("unchecked")
    public LotusWebmailConnection(final HttpClient httpclient, final HttpHost host, final String prefix) throws IOException {
        this.prefix = prefix;
        HttpGet httpGet;
        HttpResponse rsp;
        HttpEntity entity;

        String baseName = getClass().getPackage().getName().replace('.', '/') + "/lotus";
        ResourceBundle bundleBrowser = null;

        for (Header header : (Collection<Header>) httpclient.getParams().getParameter(ClientPNames.DEFAULT_HEADERS)) {
            if (header.getName().equals("Accept-Language")) {
                Matcher matcher = LANGUAGE_PATTERN.matcher(header.getValue());
                if (matcher.find()) {
                    String region = matcher.group(2);
                    if (region != null && region.length() != 0) {
                        bundleBrowser = ResourceBundle.getBundle(baseName, new Locale(matcher.group(1), region));
                    } else {
                        bundleBrowser = ResourceBundle.getBundle(baseName, new Locale(matcher.group(1)));
                    }
                }
            }
        }

        ResourceBundle bundleEnglish = ResourceBundle.getBundle(baseName, new Locale("en"));

        String deletePattern = "(?:" + Pattern.quote(bundleEnglish.getString("Delete"));
        if (bundleBrowser != null) {
            deletePattern = deletePattern + "|" + Pattern.quote(bundleBrowser.getString("Delete")) + ")";
        }
        String emptyTrashPattern = "(?:" + Pattern.quote(bundleEnglish.getString("EmptyTrash"));
        if (bundleBrowser != null) {
            emptyTrashPattern = emptyTrashPattern + "|" + Pattern.quote(bundleBrowser.getString("EmptyTrash")) + ")";
        }

        deleteDeletePattern = Pattern.compile("_doClick\\('([^/]*/\\$V\\d+ACTIONS/[^']*)'[^>]*>" + deletePattern + "\\\\" + deletePattern);
        deleteEmptyTrashPattern = Pattern.compile("_doClick\\('([^/]*/\\$V\\d+ACTIONS/[^']*)'[^>]*>" + deletePattern + "\\\\" + emptyTrashPattern);

        httpGet = new HttpGet(prefix + "/($Inbox)?OpenView");

        rsp = httpclient.execute(host, httpGet);

        entity = rsp.getEntity();
        String string = EntityUtils.toString(entity);
        if (entity != null) {
            LOGGER.trace("inbox content : {}", string);
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

    public void close() {
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
            httpclient = null;
        }
    }

    @Override
    protected void finalize() {
        try {
            close();
        } catch (Throwable e) {
            // ignore
        }
    }

    public InputStream getContent(final String id) throws IOException {
        LOGGER.info("Fetch {} content", id);
        HttpGet httpGet = new HttpGet(prefix + "/($Inbox)/" + id + MIME_SUFFIX);
        HttpResponse response = httpclient.execute(host, httpGet);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != 200) {
            throw new IOException("Unable to get message content : " + statusLine.getReasonPhrase());
        }
        return response.getEntity().getContent();
    }

    private static final Pattern CONNECTED_PATTERN = Pattern.compile("<form\\s*name=\"_DominoForm\"");

    private static final Pattern MESSAGE_BOUND = Pattern.compile("(?s)<tr[^>]*>.*?<(/?)tr");

    private static final Pattern MESSAGE_PATTERN = Pattern
            .compile("(?s)<tr[^>]*>\\s*<td.*?</td>\\s*<td.*?<input\\s.*?value=\"([^\"]*)\".*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?</td>\\s*<td.*?(\\d+)(?:[,.](\\d+))?([KM]).*?</td>.*?</tr");

    /**
     * @author Gael Lalire
     */
    public abstract static class MessageListener {

        private boolean maxMessage = false;

        public MessageListener(final String pageContent) {
            Matcher matcherBound = MESSAGE_BOUND.matcher(pageContent);
            int messageCount = 0;
            int start = 0;
            while (matcherBound.find(start)) {
                String group = matcherBound.group(1);
                if (group != null && group.length() != 0) {
                    // simple tr
                    Matcher matcher = MESSAGE_PATTERN.matcher(matcherBound.group());
                    if (matcher.matches()) {
                        int enOctet = 1;
                        switch (matcher.group(4).charAt(0)) {
                        case 'K':
                            enOctet = 1024;
                            break;
                        case 'M':
                            enOctet = 1024 * 1024;
                        default:
                            // unreachable
                            break;
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
                        messageCount++;
                        appendMessage(matcher.group(1), octets);
                    }
                    start = matcherBound.end();
                } else {
                    start = matcherBound.end() - "<tr".length();
                }
            }
            if (messageCount == MAX_MESSAGE) {
                maxMessage = true;
            }
        }

        public boolean isMaxMessage() {
            return maxMessage;
        }

        public abstract void appendMessage(String messageId, int messageSize);

    }

    public List<? extends WebmailMessage> getMessages(final int index) throws IOException {
        String pageContent = loadPageContent(index);

        final List<WebmailMessage> messages = new ArrayList<WebmailMessage>();

        new MessageListener(pageContent) {

            @Override
            public void appendMessage(final String messageId, final int messageSize) {
                messages.add(new LotusMessage(LotusWebmailConnection.this, messageId));
            }
        };

        return messages;
    }

    public String loadPageContent(final int index) throws IOException, ClientProtocolException {
        HttpGet httpGet = new HttpGet(pagePrefix + "&Start=" + index);
        HttpResponse rsp = httpclient.execute(host, httpGet);

        HttpEntity entity = rsp.getEntity();
        String pageContent = EntityUtils.toString(entity);
        if (entity != null) {
            LOGGER.trace("page {} content : {}", index, pageContent);
            EntityUtils.consume(entity);
        }

        if (!CONNECTED_PATTERN.matcher(pageContent).find()) {
            throw new IOException("Page content is not valid");
        }
        return pageContent;
    }

    public WebmailPages getPages() throws IOException {
        return new LotusPages(this);
    }

    private static final Pattern TEMP_RAND_NUM_PATTERN = Pattern.compile("<input name=\"tmpRandNum\"[^>]*value=\"([^\"]*)\">");

    private Pattern deleteDeletePattern;

    private Pattern deleteEmptyTrashPattern;

    public Set<String> delete(final Set<String> messageUids) throws IOException {
        Set<String> deleted = new HashSet<String>();
        // deleted in first but not deleted in second
        deleted.addAll(tryDelete(messageUids));
        // second time because deleting a deleted lotus message restore it
        // message deleted on second time may or may be not deleted so keep in cache
        deleted.removeAll(tryDelete(messageUids));
        return deleted;
    }

    public Set<String> tryDelete(final Set<String> messageUids) throws IOException {
        Set<String> deleted = new HashSet<String>();
        final List<String> toDelete = new ArrayList<String>();
        int page = 0;
        boolean maxMessage;
        do {
            int index = 1 + page * MAX_MESSAGE;
            String pageContent = loadPageContent(index);

            toDelete.clear();

            Matcher matcherDelete = deleteDeletePattern.matcher(pageContent);
            if (!matcherDelete.find()) {
                throw new IOException("No DELETE/DELETE find");
            }

            maxMessage = new MessageListener(pageContent) {

                @Override
                public void appendMessage(final String messageId, final int messageSize) {
                    if (messageUids.contains(messageId)) {
                        toDelete.add(messageId);
                    }
                }

            }.isMaxMessage();

            HttpPost httpPost = new HttpPost(pagePrefix + "&Start=" + index);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("__Click", matcherDelete.group(1)));
            nvps.add(new BasicNameValuePair("%%Surrogate_$$SelectDestFolder", "1"));
            nvps.add(new BasicNameValuePair("$$SelectDestFolder", "--Aucun dossier disponible--"));
            for (String doc : toDelete) {
                nvps.add(new BasicNameValuePair("$$SelectDoc", doc));
                deleted.add(doc);
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
        } while (maxMessage);

        // inline clear garbage

        String pageContent = loadPageContent(1);

        Matcher matcherDeleteGarbage = deleteEmptyTrashPattern.matcher(pageContent);
        if (!matcherDeleteGarbage.find()) {
            throw new IOException("No DELETE/EMPTY_TRASH find");
        }

        HttpPost httpPost = new HttpPost(pagePrefix);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("__Click", matcherDeleteGarbage.group(1)));
        nvps.add(new BasicNameValuePair("%%Surrogate_$$SelectDestFolder", "1"));
        nvps.add(new BasicNameValuePair("$$SelectDestFolder", "--Aucun dossier disponible--"));
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

        return deleted;
    }

}
