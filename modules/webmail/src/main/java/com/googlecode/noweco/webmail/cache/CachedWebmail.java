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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.Webmail;
import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.WebmailPages;
import com.googlecode.noweco.webmail.portal.PortalConnector;

/**
 *
 * @author Gael Lalire
 */
public class CachedWebmail implements Webmail {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedWebmail.class);

    private Webmail webmail;

    private File data;

    /**
     * @author gaellalire
     */
    private static class SerializableWebmail implements Serializable {

        private static final long serialVersionUID = -6604221551946319734L;

        private Map<String, CachedWebmailConnection> cachedWebmailConnectionByUser = new HashMap<String, CachedWebmailConnection>();

        private IDGenerator generator = new IDGenerator();

    }

    private SerializableWebmail serializableWebmail;

    // STEP 0 : nothing exists
    // STEP 1 : _webmail.ser exists but contains uncomplete data

    // STEP 2 : _webmail.ser exists and contains complete data
    // STEP 3 : webmail.ser exists and contains complete data
    // STEP 4 : webmail.ser exists and contains complete data, _webmail.ser exists but contains uncomplete data
    // STEP 5 : webmail.ser exists and contains complete data, _webmail.ser exists and contains complete data
    // return to STEP 2

    public CachedWebmail(final Webmail webmail, final File data) {
        File file = new File(data, "webmail.ser");
        if (!file.exists()) {
            File tmpFile = new File(data, "_webmail.ser");
            if (tmpFile.exists()) {
                tmpFile.renameTo(file);
            }
        }
        if (file.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                serializableWebmail = (SerializableWebmail) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException e) {
                file.delete();
                LOGGER.error("Unable to restore cached data", e);
            } catch (ClassNotFoundException e) {
                file.delete();
                LOGGER.error("Unable to restore cached data", e);
            }
        }
        if (serializableWebmail == null) {
            serializableWebmail = new SerializableWebmail();
        }
        this.webmail = webmail;
        this.data = data;
    }

    public CachedWebmailConnection connect(final String user, final String password) throws IOException {
        CachedWebmailConnection cachedWebmailConnection;
        synchronized (serializableWebmail.cachedWebmailConnectionByUser) {
            cachedWebmailConnection = serializableWebmail.cachedWebmailConnectionByUser.get(user);
        }
        if (cachedWebmailConnection == null) {
            // unknown user
            WebmailConnection connect = webmail.connect(user, password);
            CachedWebmailConnection newCachedWebmailConnection = new CachedWebmailConnection(connect, data, serializableWebmail.generator, password);
            synchronized (serializableWebmail.cachedWebmailConnectionByUser) {
                cachedWebmailConnection = serializableWebmail.cachedWebmailConnectionByUser.get(user);
                if (cachedWebmailConnection != null) {
                    newCachedWebmailConnection.close();
                } else {
                    cachedWebmailConnection = newCachedWebmailConnection;
                    serializableWebmail.cachedWebmailConnectionByUser.put(user, cachedWebmailConnection);
                }
            }
        } else {
            WebmailConnection newPasswordWebmailConnection = null;
            if (!cachedWebmailConnection.getPassword().equals(password)) {
                newPasswordWebmailConnection = webmail.connect(user, password);
                cachedWebmailConnection.setPassword(password);
            }
            try {
                WebmailPages pages = cachedWebmailConnection.getPages();
                if (pages.hasNextPage()) {
                    pages.getNextPageMessages();
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

    public void save() throws IOException {
        synchronized (serializableWebmail.cachedWebmailConnectionByUser) {
            for (CachedWebmailConnection cachedWebmailConnection : serializableWebmail.cachedWebmailConnectionByUser.values()) {
                cachedWebmailConnection.shutdown();
            }
        }
        File tmpFile = new File(data, "_webmail.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(tmpFile));
        objectOutputStream.writeObject(serializableWebmail);
        objectOutputStream.flush();
        objectOutputStream.close();
        File file = new File(data, "webmail.ser");
        file.delete();
        tmpFile.renameTo(file);
    }

    public void setAuthent(final PortalConnector portalConnector) {
        webmail.setAuthent(portalConnector);
    }

    public void setProxy(final String host, final int port) {
        webmail.setProxy(host, port);
    }

}
