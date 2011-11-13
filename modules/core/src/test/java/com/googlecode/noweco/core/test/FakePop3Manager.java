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

package com.googlecode.noweco.core.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.googlecode.noweco.pop.spi.Message;
import com.googlecode.noweco.pop.spi.Pop3Account;
import com.googlecode.noweco.pop.spi.Pop3Manager;
import com.googlecode.noweco.webmail.lotus.LotusMessageInputStream;

/**
 * @author Gael Lalire
 */
public class FakePop3Manager implements Pop3Manager, Pop3Account {

    public Pop3Account authent(final String username, final String password) throws IOException {
        return this;
    }

    public void release() throws IOException {

    }
    // 343K

    public List<? extends Message> getMessages() throws IOException {
        return Collections.singletonList(new Message() {

            public String getUID() {
                return "15B2505191F3CA630ACD25FA8942A285";
            }

            public long getSize() throws IOException {
                return new File(getClass().getResource("/badfile.txt").getFile()).length();
                // 343*1024;
            }

            public InputStream getContent() throws IOException {
                return new LotusMessageInputStream(null, getClass().getResourceAsStream("/badfile.txt"));
            }
        });
    }

    public void delete(final List<String> uids) throws IOException {
        throw new IOException("casse");
    }

}
