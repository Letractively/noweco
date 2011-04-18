package com.googlecode.noweco.core.seam;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.lotus.INotesConnection;
import com.googlecode.noweco.core.pop.Pop3Manager;
import com.googlecode.noweco.core.pop.Pop3Server;
import com.googlecode.noweco.core.pop.Pop3Transaction;

public class PopServerFromHTTPClient implements Pop3Manager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PopServerFromHTTPClient.class);

    private static final Pattern PATTERN = Pattern.compile("http(s)?://([^/:]*)(?::\\d+)?(.*)");

    private Pop3Server pop3Processor;

    private boolean secure;

    private String host;

    private String path;

    private String proxyHost;

    private int proxyPort;

    public PopServerFromHTTPClient(String url) {
        this(url, null, 0);
    }

    public PopServerFromHTTPClient(String url, String proxyHost, int proxyPort) {
        Matcher matcher = PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unsupported lotus url");
        }
        if (matcher.group(1).length() == 0) {
            secure = false;
        } else {
            secure = true;
        }
        host = matcher.group(2);
        path = matcher.group(3);

        pop3Processor = new Pop3Server(this, Executors.newFixedThreadPool(3));
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public void start() throws IOException {
        pop3Processor.start();
    }

    public void stop() throws IOException, InterruptedException {
        pop3Processor.stop();
    }

    public Pop3Transaction authent(String username, String password) {
        INotesConnection iNotesConnection;
        try {
            if (proxyHost == null) {
                iNotesConnection = new INotesConnection(secure, host);
            } else {
                iNotesConnection = new INotesConnection(proxyHost, proxyPort, secure, host);
            }
            new File(".").getCanonicalPath();

          //  iNotesConnection.connect(path, username, password);
            return new INotesPop3Transaction(iNotesConnection);
        } catch (IOException e) {
            // authent failed
            return null;
        }
    }
}
