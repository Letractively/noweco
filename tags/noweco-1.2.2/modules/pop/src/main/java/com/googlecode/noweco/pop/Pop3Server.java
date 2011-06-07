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

package com.googlecode.noweco.pop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.pop.spi.Pop3Manager;

/**
 *
 * @author Gael Lalire
 */
public class Pop3Server implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pop3Server.class);

    private static final int POP3_PORT = 110;

    private List<Pop3Connection> connections;

    private ServerSocket serverSocket;

    private Thread thread;

    private Executor executor;

    private Pop3Manager pop3Manager;

    public Pop3Server(final Pop3Manager pop3Manager, final Executor executor) {
        this.executor = executor;
        connections = new ArrayList<Pop3Connection>();
        this.pop3Manager = pop3Manager;
    }

    public void start() throws IOException {
        connections.clear();
        serverSocket = new ServerSocket(getPop3Port());
        thread = new Thread(this);
        thread.start();
    }

    public void stop() throws IOException, InterruptedException {
        Thread thread = this.thread;
        this.thread = null;
        serverSocket.close();
        synchronized (connections) {
            for (Pop3Connection connection : connections) {
                connection.stop();
            }
        }
        thread.join();
    }

    public int getPop3Port() {
        return POP3_PORT;
    }

    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                try {
                    accept(serverSocket.accept());
                } catch (IOException e) {
                    if (thread == null) {
                        LOGGER.trace("POP3Server stopped", e);
                    } else {
                        LOGGER.error("Exception on POP3Server", e);
                    }
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error("Uncatched exception", e);
        }
    }

    public void accept(final Socket socket) {
        try {
            Pop3Connection command = new Pop3Connection(pop3Manager, socket);
            try {
                executor.execute(command);
                synchronized (connections) {
                    connections.add(command);
                }
            } catch (RejectedExecutionException e) {
                LOGGER.warn("No thread available", e);
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.info("Cannot create connection", e);
        }
        synchronized (connections) {
            Iterator<Pop3Connection> iterator = connections.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isFinished()) {
                    iterator.remove();
                }
            }
        }
    }

}
