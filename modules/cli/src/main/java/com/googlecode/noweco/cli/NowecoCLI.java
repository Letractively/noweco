package com.googlecode.noweco.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.cli.settings.ObjectFactory;
import com.googlecode.noweco.core.seam.DispatcherPop3Manager;

public class NowecoCLI {

    private static final Logger LOGGER = LoggerFactory.getLogger(NowecoCLI.class);

    private static final Pattern PATTERN = Pattern.compile("http(s)?://([^/:]*)(?::\\d+)?(.*)");

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

//        JAXBElement<Settings> settingsElement = null;
//        try {
//            settingsElement = (JAXBElement<Settings>) unMarshaller.unmarshal(new FileInputStream(new File(homeFile, "settings.xml")));
//        } catch (FileNotFoundException e) {
//            LOGGER.error("Settings file not found");
//            System.exit(1);
//        } catch (JAXBException e) {
//            LOGGER.error("Settings file is not valid", e);
//            System.exit(1);
//        }

//        Settings settings = settingsElement.getValue();

        LOGGER.info("Starting Noweco");
//        Proxy proxy = settings.getProxy();
//        Lotus lotus = settings.getLotus();
//        String url = lotus.getUrl();

//        boolean secure;
//        Matcher matcher = PATTERN.matcher(url);
//        if (!matcher.matches()) {
//            throw new IllegalArgumentException("Unsupported lotus url");
//        }
//        if (matcher.group(1).length() == 0) {
//            secure = false;
//        } else {
//            secure = true;
//        }
//        String host = matcher.group(2);
//        // String path = matcher.group(3);
//
//        Webmail webmail;
//        if (proxy == null) {
//            webmail = new HordeWebmail(secure, host);
//        } else {
//            String protocol = proxy.getProtocol();
//            if (!protocol.equals("http")) {
//                LOGGER.error("Unsupported proxy protocol : {}", protocol);
//                System.exit(1);
//            }
//            webmail = new HordeWebmail(proxy.getHost(), proxy.getPort(), secure, host);
//        }

        File data = new File(homeFile, "data");
        if (!data.exists()) {
            data.mkdir();
        }
        final DispatcherPop3Manager pop3Manager = new DispatcherPop3Manager(data);

        try {
            pop3Manager.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Noweco started");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    pop3Manager.stop();
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
