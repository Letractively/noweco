package com.googlecode.noweco.core.webmail.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.webmail.Page;
import com.googlecode.noweco.core.webmail.Webmail;
import com.googlecode.noweco.core.webmail.WebmailConnection;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class CachedWebmail implements Webmail {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedWebmail.class);

    private Webmail webmail;

    private File data;

    private Map<String, CachedWebmailConnection> cachedWebmailConnectionByUser = new HashMap<String, CachedWebmailConnection>();

    private Map<String, CachedWebmailConnection> restoredWebmailConnectionByUser = new HashMap<String, CachedWebmailConnection>();

    @SuppressWarnings("unchecked")
    public CachedWebmail(Webmail webmail, File data) {
        if (data.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data));
                restoredWebmailConnectionByUser = (Map<String, CachedWebmailConnection>) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException e) {
                LOGGER.error("Unable to restore cached data", e);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Unable to restore cached data", e);
            }
        }
        this.webmail = webmail;
        this.data = data;
    }

    public CachedWebmailConnection connect(String user, String password) throws IOException {
        CachedWebmailConnection cachedWebmailConnection = cachedWebmailConnectionByUser.get(user);
        if (cachedWebmailConnection == null) {
            WebmailConnection connect = webmail.connect(user, password);
            cachedWebmailConnection = restoredWebmailConnectionByUser.get(user);
            if (cachedWebmailConnection != null) {
                cachedWebmailConnection.setDelegate(connect);
            } else {
                cachedWebmailConnection = new CachedWebmailConnection(connect, password);
            }
            cachedWebmailConnectionByUser.put(user, cachedWebmailConnection);
        } else {
            if (!cachedWebmailConnection.getPassword().equals(password)) {
                throw new IOException("Bad password");
            }
            Iterator<Page> pages = cachedWebmailConnection.getPages();
            try {
                if (pages.hasNext()) {
                    pages.next().getMessages();
                }
            } catch (IOException e) {
                LOGGER.info("Try to reconnect", e);
                WebmailConnection connect = webmail.connect(user, password);
                cachedWebmailConnection.setDelegate(connect);
            }
        }
        return cachedWebmailConnection;
    }

    public void release() {
        for (CachedWebmailConnection cachedWebmailConnection : cachedWebmailConnectionByUser.values()) {
            cachedWebmailConnection.release();
        }
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data));
            objectOutputStream.writeObject(cachedWebmailConnectionByUser);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            LOGGER.error("Unable to save cached data", e);
        }
        webmail.release();
    }

    public void setAuthent(PortalConnector portalConnector) {
        webmail.setAuthent(portalConnector);
    }

    public void setProxy(String host, int port) {
        webmail.setProxy(host, port);
    }

}
