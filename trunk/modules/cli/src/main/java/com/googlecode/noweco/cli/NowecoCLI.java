package com.googlecode.noweco.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.cli.settings.Lotus;
import com.googlecode.noweco.cli.settings.ObjectFactory;
import com.googlecode.noweco.cli.settings.Proxy;
import com.googlecode.noweco.cli.settings.Settings;
import com.googlecode.noweco.core.seam.PopServerFromHTTPClient;

public class NowecoCLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(NowecoCLI.class);

    public static void main(String[] args) {
        File settingsFile = new File(args[0]);
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
            settingsElement = (JAXBElement<Settings>) unMarshaller.unmarshal(new FileInputStream(settingsFile));
        } catch (FileNotFoundException e) {
            LOGGER.error("Settings file not found");
            System.exit(1);
        } catch (JAXBException e) {
            LOGGER.error("Settings file is not valid", e);
            System.exit(1);
        }

        Settings settings = settingsElement.getValue();

        LOGGER.info("Starting Noweco");
        Proxy proxy = settings.getProxy();
        Lotus lotus = settings.getLotus();
        String url = lotus.getUrl();


        final PopServerFromHTTPClient popServerFromHTTPClient;
        if (proxy == null) {
            popServerFromHTTPClient = new PopServerFromHTTPClient(url);
        } else {
            String protocol = proxy.getProtocol();
            if (!protocol.equals("http")) {
                LOGGER.error("Unsupported proxy protocol : {}", protocol);
                System.exit(1);
            }
            popServerFromHTTPClient = new PopServerFromHTTPClient(url, proxy.getHost(), proxy.getPort());
        }
        try {
            popServerFromHTTPClient.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Noweco started");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    popServerFromHTTPClient.stop();
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
