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

package com.googlecode.noweco.webmail.cache;

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

import com.googlecode.noweco.webmail.Page;
import com.googlecode.noweco.webmail.Webmail;
import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.portal.PortalConnector;

/**
 *
 * @author Gael Lalire
 */
public class CachedWebmail implements Webmail {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedWebmail.class);

    private Webmail webmail;

    private File data;

    private Map<String, CachedWebmailConnection> cachedWebmailConnectionByUser = new HashMap<String, CachedWebmailConnection>();

    private Map<String, CachedWebmailConnection> restoredWebmailConnectionByUser = new HashMap<String, CachedWebmailConnection>();

    @SuppressWarnings("unchecked")
    public CachedWebmail(final Webmail webmail, final File data) {
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

    public CachedWebmailConnection connect(final String user, final String password) throws IOException {
        CachedWebmailConnection cachedWebmailConnection;
        synchronized (cachedWebmailConnectionByUser) {
            cachedWebmailConnection = cachedWebmailConnectionByUser.get(user);
        }
        if (cachedWebmailConnection == null) {
            WebmailConnection connect = webmail.connect(user, password);
            cachedWebmailConnection = restoredWebmailConnectionByUser.get(user);
            if (cachedWebmailConnection != null) {
                cachedWebmailConnection.setPassword(password);
                cachedWebmailConnection.setDelegate(connect);
            } else {
                cachedWebmailConnection = new CachedWebmailConnection(connect, password);
            }
            synchronized (cachedWebmailConnectionByUser) {
                cachedWebmailConnectionByUser.put(user, cachedWebmailConnection);
            }
        } else {
            WebmailConnection newPasswordWebmailConnection = null;
            if (!cachedWebmailConnection.getPassword().equals(password)) {
                newPasswordWebmailConnection = webmail.connect(user, password);
                cachedWebmailConnection.setPassword(password);
            }
            Iterator<Page> pages = cachedWebmailConnection.getPages();
            try {
                if (pages.hasNext()) {
                    pages.next().getMessages();
                }
            } catch (IOException e) {
                if (newPasswordWebmailConnection != null) {
                    cachedWebmailConnection.setDelegate(newPasswordWebmailConnection);
                } else {
                    LOGGER.info("Try to reconnect", e);
                    WebmailConnection connect = webmail.connect(user, password);
                    cachedWebmailConnection.setDelegate(connect);
                }
            }
        }
        return cachedWebmailConnection;
    }

    public void release() {
        synchronized (cachedWebmailConnectionByUser) {
            for (CachedWebmailConnection cachedWebmailConnection : cachedWebmailConnectionByUser.values()) {
                cachedWebmailConnection.release();
            }
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

    public void setAuthent(final PortalConnector portalConnector) {
        webmail.setAuthent(portalConnector);
    }

    public void setProxy(final String host, final int port) {
        webmail.setProxy(host, port);
    }

}
