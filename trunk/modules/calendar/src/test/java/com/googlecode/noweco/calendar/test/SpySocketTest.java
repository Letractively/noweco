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

package com.googlecode.noweco.calendar.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gael Lalire
 */
public class SpySocketTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpySocketTest.class);

    /**
     * @author Gael Lalire
     */
    class Spy extends Thread {

        private String prefix;

        private InputStream inputStream;

        private OutputStream outputStream;

        private StringBuilder requestLines = new StringBuilder();

        public Spy(final String prefix, final InputStream inputStream, final OutputStream outputStream) {
            this.prefix = prefix;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            byte[] buff = new byte[1024];
            try {
                int read = inputStream.read(buff);

                while (read != -1) {
                    InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(buff, 0, read));
                    synchronized (requestLines) {
                        int c = reader.read();
                        while (c != -1) {
                            requestLines.append((char) c);
                            c = reader.read();
                        }
                    }
                    outputStream.write(buff, 0, read);
                    outputStream.flush();
                    read = inputStream.read(buff);
                }
                printAndClear();
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                printAndClear();
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e2) {
                    LOGGER.error("Error on closing", e2);
                }
            }
        }

        public void printAndClear() {
            synchronized (requestLines) {
                if (requestLines.length() != 0) {
                    LOGGER.info("{}{}", prefix, requestLines.toString());
                    requestLines.setLength(0);
                }
            }
        }

    }

    @Test
    @Ignore
    public void test() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8088);
        int i = 0;
        while (true) {
            Socket accept = serverSocket.accept();
            Socket destSocket = new Socket("localhost", 8008);
            final Spy requestSpy = new Spy("[" + i + "]Request:\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n", accept.getInputStream(),
                    destSocket.getOutputStream());
            requestSpy.start();
            new Spy("[" + i + "]Response:\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n", destSocket.getInputStream(),
                    accept.getOutputStream()) {
                @Override
                public void printAndClear() {
                    requestSpy.printAndClear();
                    super.printAndClear();
                }
            }.start();
            i++;
        }

    }
}
