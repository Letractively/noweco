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
import java.net.URL;
import java.util.Collections;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
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
import com.googlecode.noweco.cli.settings.Settings;

/**
 * @author Gael Lalire
 */
public final class StopNoweco {

    private StopNoweco() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StopNoweco.class);

    public static void main(final String[] args) {
        int exitCode = stop(new File(args[0]));
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    @SuppressWarnings("unchecked")
    public static int stop(final File homeFile) {
        Unmarshaller unMarshaller = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            unMarshaller = jc.createUnmarshaller();

            URL xsdURL = StopNoweco.class.getResource("settings.xsd");
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = schemaFactory.newSchema(xsdURL);
            unMarshaller.setSchema(schema);
        } catch (Exception e) {
            LOGGER.error("Unable to initialize settings parser", e);
            return 1;
        }

        JAXBElement<Settings> settingsElement = null;
        try {
            settingsElement = (JAXBElement<Settings>) unMarshaller.unmarshal(new FileInputStream(new File(homeFile, "settings.xml")));
        } catch (FileNotFoundException e) {
            LOGGER.error("Settings file not found");
            return 1;
        } catch (JAXBException e) {
            LOGGER.error("Settings file is not valid", e);
            return 1;
        }

        Settings settings = settingsElement.getValue();
        final JMX jmx = settings.getJmx();

        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:" + settings.getRegistryPort() + "/jmxrmi");
            JMXConnector connect = JMXConnectorFactory.connect(jmxServiceURL, Collections.singletonMap(JMXConnector.CREDENTIALS, new String[] {jmx.getUser(), jmx.getPassword()}));
            MBeanServerConnection connection = connect.getMBeanServerConnection();
            ObjectName objectName = new ObjectName(AdminMBean.class.getPackage().getName() + ":type=" + AdminMBean.class.getSimpleName());
            connection.invoke(objectName, "stop", null, new String[0]);
            connect.close();
        } catch (Exception e) {
            LOGGER.error("Unable to stop Noweco via Admin", e);
            return 1;
        }

        return 0;
    }

}
