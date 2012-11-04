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

package com.googlecode.noweco.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.cli.settings.ObjectFactory;
import com.googlecode.noweco.cli.settings.Proxy;
import com.googlecode.noweco.cli.settings.Settings;
import com.googlecode.noweco.cli.settings.User;
import com.googlecode.noweco.cli.settings.Webmail;
import com.googlecode.noweco.core.DispatchedPop3Manager;
import com.googlecode.noweco.core.DispatcherPop3Manager;
import com.googlecode.noweco.core.WebmailPop3Manager;
import com.googlecode.noweco.pop.Pop3Server;
import com.googlecode.noweco.pop.spi.Pop3Manager;
import com.googlecode.noweco.webmail.cache.CachedWebmail;
import com.googlecode.noweco.webmail.portal.PortalConnector;

/**
 * @author Gael Lalire
 */
public final class StartNoweco implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartNoweco.class);

    private File homeFile;

    public StartNoweco(final File homeFile) {
        this.homeFile = homeFile;
    }

    public static void main(final String[] args) {
        final Thread currentThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                currentThread.interrupt();
                try {
                    currentThread.join();
                } catch (InterruptedException e) {
                    LOGGER.error("ShutdownHook interrupted", e);
                }
            }
        });
        try {
            new StartNoweco(new File(args[0])).run();
        } catch (Throwable e) {
            LOGGER.error("Uncatched throwable", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void run() {
        Unmarshaller unMarshaller = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            unMarshaller = jc.createUnmarshaller();

            URL xsdURL = StartNoweco.class.getResource("settings.xsd");
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = schemaFactory.newSchema(xsdURL);
            unMarshaller.setSchema(schema);
        } catch (Exception e) {
            LOGGER.error("Unable to initialize settings parser", e);
            return;
        }

        JAXBElement<Settings> settingsElement = null;
        try {
            settingsElement = (JAXBElement<Settings>) unMarshaller.unmarshal(new FileInputStream(new File(homeFile,
                    "settings.xml")));
        } catch (FileNotFoundException e) {
            LOGGER.error("Settings file not found");
            return;
        } catch (JAXBException e) {
            LOGGER.error("Settings file is not valid", e);
            return;
        }

        Settings settings = settingsElement.getValue();

        LOGGER.info("Starting Noweco");

        File data = new File(homeFile, "data");
        if (!data.exists()) {
            data.mkdir();
        }

        Map<String, Pop3Manager> map = new HashMap<String, Pop3Manager>();

        List<CachedWebmail> cachedWebmails = new ArrayList<CachedWebmail>();

        for (Webmail webmail : settings.getPop3Managers().getWebmail()) {
            String webmailClassName = webmail.getClazz();
            Class<?> webmailClass = null;
            try {
                webmailClass = StartNoweco.class.getClassLoader().loadClass(webmailClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Class {} not found", webmailClassName);
                return;
            }
            if (!com.googlecode.noweco.webmail.Webmail.class.isAssignableFrom(webmailClass)) {
                LOGGER.error("{} is not a subclass of Pop3Manager", webmailClassName);
                return;
            }
            com.googlecode.noweco.webmail.Webmail webmailInstance = null;
            try {
                webmailInstance = (com.googlecode.noweco.webmail.Webmail) webmailClass.newInstance();
            } catch (InstantiationException e) {
                LOGGER.error("InstantiationException", e);
                return;
            } catch (IllegalAccessException e) {
                LOGGER.error("IllegalAccessException", e);
                return;
            }
            String id = webmail.getId();
            File popManagerData = new File(data, id);
            popManagerData.mkdir();
            CachedWebmail cachedWebmail = new CachedWebmail(webmailInstance, popManagerData);
            webmailInstance = cachedWebmail;
            cachedWebmails.add(cachedWebmail);
            Pop3Manager pop3Manager = new WebmailPop3Manager(webmailInstance);

            Proxy proxy = webmail.getProxy();
            if (proxy != null) {
                webmailInstance.setProxy(proxy.getHost(), proxy.getPort());
            }
            String authentClassName = webmail.getAuthent().getClazz();
            Class<?> authentClass = null;
            try {
                authentClass = StartNoweco.class.getClassLoader().loadClass(authentClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Class {} not found", authentClassName);
                return;
            }
            if (!PortalConnector.class.isAssignableFrom(authentClass)) {
                LOGGER.error("{} is not a subclass of PortalConnector", authentClassName);
                return;
            }
            PortalConnector portalConnector = null;
            try {
                portalConnector = (PortalConnector) authentClass.newInstance();
            } catch (InstantiationException e) {
                LOGGER.error("InstantiationException", e);
                return;
            } catch (IllegalAccessException e) {
                LOGGER.error("IllegalAccessException", e);
                return;
            }
            webmailInstance.setAuthent(portalConnector);

            map.put(id, pop3Manager);
        }

        List<DispatchedPop3Manager> dispatchedPop3Managers = new ArrayList<DispatchedPop3Manager>();

        for (User user : settings.getPopAccounts().getUser()) {
            String pop3Manager = user.getPop3Manager();
            dispatchedPop3Managers.add(new DispatchedPop3Manager(pop3Manager, map.get(pop3Manager), Pattern.compile(user
                    .getMatches())));
        }

        // int localPort = settings.getRegistryPort();
        // final JMX jmx = settings.getJmx();
        // Admin admin = new Admin();
        // final JMXConnectorServer newJMXConnectorServer;
        // final Registry createRegistry;
        // final MBeanServer mbs;
        // final ObjectName objectName;
        // try {
        // createRegistry = LocateRegistry.createRegistry(localPort);
        // mbs = ManagementFactory.getPlatformMBeanServer();
        // objectName = new ObjectName(AdminMBean.class.getPackage().getName() +
        // ":type=" + AdminMBean.class.getSimpleName());
        // mbs.registerMBean(admin, objectName);
        // JMXServiceURL jmxServiceURL = new
        // JMXServiceURL("service:jmx:rmi://localhost:" + localPort +
        // "/jndi/rmi://localhost:" + localPort + "/jmxrmi");
        // Map<String, Object> env = new HashMap<String, Object>();
        // env.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator() {
        //
        // public Subject authenticate(final Object credentials) {
        // if (!(credentials instanceof String[])) {
        // if (credentials == null) {
        // throw new SecurityException("Credentials required");
        // }
        // throw new SecurityException("Credentials should be String[]");
        // }
        // final String[] aCredentials = (String[]) credentials;
        // if (aCredentials.length != 2) {
        // throw new SecurityException("Credentials should have 2 elements");
        // }
        // String username = aCredentials[0];
        // String password = aCredentials[1];
        // if (jmx.getUser().equals(username) &&
        // jmx.getPassword().equals(password)) {
        // return new Subject(false, Collections.singleton(new
        // JMXPrincipal(username)), Collections.EMPTY_SET,
        // Collections.EMPTY_SET);
        // }
        // throw new SecurityException("Invalid credentials");
        // }
        // });
        // newJMXConnectorServer =
        // JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, env,
        // mbs);
        // newJMXConnectorServer.start();
        // } catch (Exception e) {
        // LOGGER.error("Unable to start Noweco Admin", e);
        // return 1;
        // }

        final DispatcherPop3Manager pop3Manager = new DispatcherPop3Manager(dispatchedPop3Managers);
        final Pop3Server pop3Server = new Pop3Server(pop3Manager, Executors.newCachedThreadPool());

        try {
            pop3Server.start();
        } catch (IOException e) {
            LOGGER.error("Unable to start Noweco", e);
            return;
        }
        try {
            LOGGER.info("Noweco started");
            synchronized (this) {
                try {
                    while (true) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    LOGGER.info("Noweco stopping");
                }
            }
        } finally {
            try {
                boolean stopped = false;
                do {
                    try {
                        pop3Server.stop();
                        stopped = true;
                    } catch (InterruptedException e) {
                        LOGGER.warn("Interrupted while stopping server");
                    }
                } while (!stopped);
            } catch (IOException e) {
                LOGGER.warn("Stopping server failed", e);
                return;
            }
        }
        for (CachedWebmail cachedWebmail : cachedWebmails) {
            try {
                cachedWebmail.save();
            } catch (IOException e) {
                LOGGER.warn("Unable to save data", e);
            }
        }
        // newJMXConnectorServer.stop();
        // mbs.unregisterMBean(objectName);
        // UnicastRemoteObject.unexportObject(createRegistry, true);
        LOGGER.info("Noweco successfully stopped");
    }

}
