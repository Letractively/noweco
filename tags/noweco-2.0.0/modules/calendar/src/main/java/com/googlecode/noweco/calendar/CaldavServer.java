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

package com.googlecode.noweco.calendar;

import java.io.IOException;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * @author Gael Lalire
 */
public class CaldavServer {

    public static final int HTTP_PORT = 80;

    public static final int HTTPS_PORT = 443;

    public int getHttpPort() {
        return HTTP_PORT;
    }

    public int getHttpsPort() {
        return HTTPS_PORT;
    }

    public void start() throws IOException {
        Server server = new Server();
        Connector connector = new SocketConnector();
        connector.setPort(getHttpPort());
        SslSocketConnector sslConnector = new SslSocketConnector();
        sslConnector.setKeystore(CaldavServer.class.getResource("noweco.keystore").toString());
        sslConnector.setPassword("noweco");
        sslConnector.setKeyPassword("noweco");
        sslConnector.setPort(getHttpsPort());
        server.setConnectors(new Connector[] { connector, sslConnector });

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(new CaldavServlet()), "/");
        server.addHandler(servletHandler);
        try {
            server.start();
        } catch (Exception e) {
            throw new CalendarException("Unable to start jetty server", e);
        }
    }

}
