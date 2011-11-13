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

package com.googlecode.noweco.core.webmail.test.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.webmail.Message;
import com.googlecode.noweco.webmail.cache.CachedMessage;

/**
 * @author Gael Lalire
 */
public class TestCachedMessage {

    // This test may failed
    @Test
    @Ignore
    public void test() throws IOException, ClassNotFoundException {
        final String content = "The content";
        CachedMessage cachedMessage = new CachedMessage(new Message() {

            public String getUniqueID() {
                return "123";
            }

            public long getSize() throws IOException {
                return 0;
            }

            public InputStream getContent() throws IOException {
                return new ByteArrayInputStream(content.getBytes());
            }
        }, File.createTempFile("test", ".test"));
        Assert.assertTrue(cachedMessage.getContent().equals(content));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(cachedMessage);
        objectOutputStream.close();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        CachedMessage readObject = (CachedMessage) objectInputStream.readObject();
        Assert.assertTrue(readObject.getContent().equals(content));
    }

}
