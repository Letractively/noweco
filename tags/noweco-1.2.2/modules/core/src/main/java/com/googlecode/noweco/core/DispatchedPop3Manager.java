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

package com.googlecode.noweco.core;

import java.util.regex.Pattern;

import com.googlecode.noweco.pop.spi.Pop3Manager;

/**
 *
 * @author Gael Lalire
 */
public class DispatchedPop3Manager {

    private String id;

    private Pop3Manager pop3Manager;

    private Pattern pattern;

    public DispatchedPop3Manager(final String id, final Pop3Manager pop3Manager, final Pattern pattern) {
        this.id = id;
        this.pop3Manager = pop3Manager;
        this.pattern = pattern;
    }

    public String getId() {
        return id;
    }

    public Pop3Manager getPop3Manager() {
        return pop3Manager;
    }

    public Pattern getPattern() {
        return pattern;
    }

}
