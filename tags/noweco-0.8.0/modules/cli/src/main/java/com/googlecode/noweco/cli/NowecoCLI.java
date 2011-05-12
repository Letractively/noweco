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
import com.googlecode.noweco.core.pop.Pop3Server;
import com.googlecode.noweco.core.pop.spi.Pop3Manager;
import com.googlecode.noweco.core.seam.DispatchedPop3Manager;
import com.googlecode.noweco.core.seam.DispatcherPop3Manager;
import com.googlecode.noweco.core.seam.WebmailPop3Manager;
import com.googlecode.noweco.core.webmail.cache.CachedWebmail;
import com.googlecode.noweco.core.webmail.portal.PortalConnector;

public class NowecoCLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(NowecoCLI.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        File homeFile = new File(args[0]);
        Unmarshaller unMarshaller = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            unMarshaller = jc.createUnmarshaller();

            URL xsdURL = NowecoCLI.class.getResource("settings.xsd");
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

        for(Webmail webmail : settings.getPop3Managers().getWebmail()) {
            String webmailClassName = webmail.getClazz();
            Class<?> webmailClass = null;
            try {
                webmailClass = NowecoCLI.class.getClassLoader().loadClass(webmailClassName);
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
                authentClass = NowecoCLI.class.getClassLoader().loadClass(authentClassName);
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

        final DispatcherPop3Manager pop3Manager = new DispatcherPop3Manager(dispatchedPop3Managers);
        final Pop3Server pop3Server = new Pop3Server(pop3Manager, Executors.newFixedThreadPool(3));

        try {
            pop3Server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Noweco started");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    pop3Server.stop();
                    pop3Manager.release();
                } catch (IOException e) {

                } catch (InterruptedException e) {
                    // impossible
                } finally {
                    LOGGER.info("Noweco shutdown");
                }
            }

        });

    }
}
