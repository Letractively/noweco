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

package com.googlecode.noweco.core.webmail.lotus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.webmail.Message;

/**
 *
 * @author Gael Lalire
 */
public class LotusMessage implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(LotusMessage.class);

    private String id;

    private int size;

    private String headers;

    private String content;

    private LotusWebmailConnection webmail;

    public LotusMessage(final LotusWebmailConnection webmail, final String id, final int size) {
        this.webmail = webmail;
        this.id = id;
        this.size = size;
        content = null;
    }

    public int getSize() {
        return size;
    }

    private static final String DATE_PREFIX = "Date:";

    private synchronized void fetch() throws IOException {
        if (content == null) {
            content = webmail.getContent(id);
            int indexOfHeaderEnd = content.indexOf("\r\n\r\n");
            if (indexOfHeaderEnd == -1) {
                indexOfHeaderEnd = content.indexOf("\n\n");
                indexOfHeaderEnd++;
            } else {
                indexOfHeaderEnd += 2;
            }
            String header = content.substring(0, indexOfHeaderEnd);
            StringBuilder newHeader = new StringBuilder();

            BufferedReader bufferedReader = new BufferedReader(new StringReader(header));
            String line = bufferedReader.readLine();
            while (line != null && line.length() != 0) {
                if (line.startsWith(DATE_PREFIX)) {
                    try {
                        newHeader.append(LotusDateTransformer.convertNotesToRfc822(line));
                        newHeader.append('\n');
                    } catch (ParseException e) {
                        LOGGER.trace("Not a lotus date", e);
                        newHeader.append(line);
                        newHeader.append('\n');
                    }
                } else {
                    newHeader.append(line);
                    newHeader.append('\n');
                }
                line = bufferedReader.readLine();
            }
            headers = newHeader.toString();
            content = headers + content.substring(indexOfHeaderEnd);
        }
    }

    public String getHeader() throws IOException {
        fetch();
        return headers;
    }

    public String getContent() throws IOException {
        fetch();
        return content;
    }

    @Override
    public String toString() {
        return getUniqueID();
    }

    public String getUniqueID() {
        return id;
    }

}
