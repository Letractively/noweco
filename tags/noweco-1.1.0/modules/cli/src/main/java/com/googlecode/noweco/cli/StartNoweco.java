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
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXPrincipal;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.cli.settings.JMX;
import com.googlecode.noweco.cli.settings.ObjectFactory;
import com.googlecode.noweco.cli.settings.Proxy;
import com.googlecode.noweco.cli.settings.Settings;
import com.googlecode.noweco.cli.settings.User;
import com.googlecode.noweco.cli.settings.Webmail;
import com.googlecode.noweco.core.pop.Pop3Server;
import com.googlecode.noweco.core.pop.spi.Pop3Manager;
import com.googlecode.noweco.core.seam.DispatchedPop3Manager;
import com.googlecode.noweco.core.seam.DispatcherPop3Manager;
import com.googlecode.noweco.core.seam.WebmailPop3Manager;
import com.googlecode.noweco.core.webmail.cache.CachedWebmail;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

/**
 *
 * @author Gael Lalire
 */
public final class StartNoweco {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartNoweco.class);

    private StartNoweco() {
    }

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) {
        File homeFile = new File(System.getProperty("noweco.home"));
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
            System.exit(1);
        }

        JAXBElement<Settings> settingsElement = null;
        try {
            settingsElement = (JAXBElement<Settings>) unMarshaller.unmarshal(new FileInputStream(new File(homeFile, "settings.xml")));
        } catch (FileNotFoundException e) {
            LOGGER.error("Settings file not found");
            System.exit(1);
        } catch (JAXBException e) {
            LOGGER.error("Settings file is not valid", e);
            System.exit(1);
        }

        Settings settings = settingsElement.getValue();

        LOGGER.info("Starting Noweco");

        File data = new File(homeFile, "data");
        if (!data.exists()) {
            data.mkdir();
        }

        Map<String, Pop3Manager> map = new HashMap<String, Pop3Manager>();

        for (Webmail webmail : settings.getPop3Managers().getWebmail()) {
            String webmailClassName = webmail.getClazz();
            Class<?> webmailClass = null;
            try {
                webmailClass = StartNoweco.class.getClassLoader().loadClass(webmailClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Class {} not found", webmailClassName);
                System.exit(1);
            }
            if (!com.googlecode.noweco.core.webmail.Webmail.class.isAssignableFrom(webmailClass)) {
                LOGGER.error("{} is not a subclass of Pop3Manager", webmailClassName);
                System.exit(1);
            }
            com.googlecode.noweco.core.webmail.Webmail webmailInstance = null;
            try {
                webmailInstance = (com.googlecode.noweco.core.webmail.Webmail) webmailClass.newInstance();
            } catch (InstantiationException e) {
                LOGGER.error("InstantiationException", e);
                System.exit(1);
            } catch (IllegalAccessException e) {
                LOGGER.error("IllegalAccessException", e);
                System.exit(1);
            }
            String id = webmail.getId();
            webmailInstance = new CachedWebmail(webmailInstance, new File(data, id + ".data"));
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
                System.exit(1);
            }
            if (!PortalConnector.class.isAssignableFrom(authentClass)) {
                LOGGER.error("{} is not a subclass of PortalConnector", authentClassName);
                System.exit(1);
            }
            PortalConnector portalConnector = null;
            try {
                portalConnector = (PortalConnector) authentClass.newInstance();
            } catch (InstantiationException e) {
                LOGGER.error("InstantiationException", e);
                System.exit(1);
            } catch (IllegalAccessException e) {
                LOGGER.error("IllegalAccessException", e);
                System.exit(1);
            }
            webmailInstance.setAuthent(portalConnector);

            map.put(id, pop3Manager);
        }

        List<DispatchedPop3Manager> dispatchedPop3Managers = new ArrayList<DispatchedPop3Manager>();

        for (User user : settings.getPopAccounts().getUser()) {
            String pop3Manager = user.getPop3Manager();
            dispatchedPop3Managers.add(new DispatchedPop3Manager(pop3Manager, map.get(pop3Manager), Pattern.compile(user.getMatches())));
        }

        int localPort = settings.getRegistryPort();
        JMXConnectorServer newJMXConnectorServer = null;
        final JMX jmx = settings.getJmx();
        Admin admin = new Admin();
        try {
            LocateRegistry.createRegistry(localPort);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName(AdminMBean.class.getPackage().getName() + ":type=" + AdminMBean.class.getSimpleName());
            mbs.registerMBean(admin, objectName);
            JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi://127.0.0.1:" + localPort + "/jndi/rmi://127.0.0.1:" + localPort + "/jmxrmi");
            Map<String, Object> env = new HashMap<String, Object>();
            env.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator() {

                public Subject authenticate(final Object credentials) {
                    if (!(credentials instanceof String[])) {
                        if (credentials == null) {
                            throw new SecurityException("Credentials required");
                        }
                        throw new SecurityException("Credentials should be String[]");
                    }
                    final String[] aCredentials = (String[]) credentials;
                    if (aCredentials.length != 2) {
                        throw new SecurityException("Credentials should have 2 elements");
                    }
                    String username = aCredentials[0];
                    String password = aCredentials[1];
                    if (jmx.getUser().equals(username) && jmx.getPassword().equals(password)) {
                        return new Subject(false, Collections.singleton(new JMXPrincipal(username)), Collections.EMPTY_SET, Collections.EMPTY_SET);
                    }
                    throw new SecurityException("Invalid credentials");
                }
            });
            newJMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, env, mbs);
            newJMXConnectorServer.start();
        } catch (Exception e) {
            LOGGER.error("Unable to start Noweco Admin", e);
            System.exit(1);
        }

        final DispatcherPop3Manager pop3Manager = new DispatcherPop3Manager(dispatchedPop3Managers);
        final Pop3Server pop3Server = new Pop3Server(pop3Manager, Executors.newCachedThreadPool());
        final JMXConnectorServer jmxConnectorServer = newJMXConnectorServer;

        Runnable stopAction = new Runnable() {

            private boolean stopped = false;

            public synchronized void run() {
                if (stopped) {
                    return;
                }
                stopped = true;
                try {
                    pop3Server.stop();
                    pop3Manager.release();
                    jmxConnectorServer.stop();
                } catch (IOException e) {
                    LOGGER.info("Noweco stop issue", e);
                } catch (InterruptedException e) {
                    // unreachable
                } finally {
                    LOGGER.info("Noweco shutdown");
                }
            }
        };

        try {
            pop3Server.start();
        } catch (IOException e) {
            LOGGER.error("Unable to start Noweco", e);
            System.exit(1);
        }

        admin.setStopAction(stopAction);
        Runtime.getRuntime().addShutdownHook(new Thread(stopAction));

        LOGGER.info("Noweco started");

    }
}
