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

package com.googlecode.noweco.pop.test;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.noweco.pop.Pop3Server;

/**
 * @author Gael Lalire
 */
public class TestPop3Server {

    @Test
    @Ignore
    public void test() throws IOException, InterruptedException {
        Pop3Server pop3Server = new Pop3Server(new FakePop3Manager(), Executors.newCachedThreadPool()) {
            @Override
            public int getPop3Port() {
                return 1100;
            }
        };
        pop3Server.start();
        synchronized (this) {
            wait();
        }
    }

}
