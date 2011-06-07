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

package com.googlecode.noweco.webmail.lotus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.webmail.Message;

/**
 *
 * @author Gael Lalire
 */
public class LotusMessage implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotusMessage.class);

    private String id;

    private LotusWebmailConnection webmail;

    public LotusMessage(final LotusWebmailConnection webmail, final String id) {
        this.webmail = webmail;
        this.id = id;
    }

    public int getSize() throws IOException {
        // HEAD method on the web page do not work, because the message may be
        // transformed
        return fetch().length();
    }

    private static final String DATE_PREFIX = "Date:";

    private static final String NEW_LINE = "\r\n";

    private String fetch() throws IOException {
        StringBuilder content = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new StringReader(webmail.getContent(id)));
        String line = bufferedReader.readLine();

        String dateLine = null;
        boolean receiveHeader = false;
        while (line != null && line.length() != 0) {
            if (line.startsWith("Received:")) {
                receiveHeader = true;
            }
            if (line.startsWith(DATE_PREFIX)) {
                try {
                    dateLine = LotusDateTransformer.convertNotesToRfc822(line);
                    LOGGER.debug("Convert Date header : {} for {}", dateLine, id);
                    content.append(dateLine);
                    content.append(NEW_LINE);
                } catch (ParseException e) {
                    dateLine = line;
                    LOGGER.trace("Not a lotus date", e);
                    content.append(line);
                    content.append(NEW_LINE);
                }
            } else {
                content.append(line);
                content.append(NEW_LINE);
            }
            line = bufferedReader.readLine();
        }
        if (!receiveHeader && dateLine != null) {
            String receivedHeader = "Received: by noweco; " + dateLine.substring(DATE_PREFIX.length()) + NEW_LINE;
            LOGGER.debug("Insert generated Received header : {} for {}", receivedHeader, id);
            content.insert(0, receivedHeader);
        }
        while (line != null) {
            content.append(line);
            content.append(NEW_LINE);
            line = bufferedReader.readLine();
        }
        return content.toString();
    }

    public String getHeader() throws IOException {
        String fetch = fetch();
        int index = fetch.indexOf(NEW_LINE + NEW_LINE);
        if (index == -1) {
            index = fetch.length();
        }
        return fetch.substring(0, index);
    }

    public String getContent() throws IOException {
        return fetch();
    }

    @Override
    public String toString() {
        return getUniqueID();
    }

    public String getUniqueID() {
        return id;
    }

}
