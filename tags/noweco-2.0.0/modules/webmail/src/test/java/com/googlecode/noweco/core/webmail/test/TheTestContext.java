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

package com.googlecode.noweco.core.webmail.test;

import junit.framework.AssertionFailedError;

/**
 * Accessor to test context.
 * Following system properties are needed for tests
 * <ul>
 * <li>NOWECO_LOTUS_USERNAME</li>
 * <li>NOWECO_LOTUS_PASSWORD</li>
 * <li>NOWECO_PROXY_HOST</li>
 * <li>NOWECO_PROXY_PORT</li>
 * </ul>
 *
 * In Eclipse you can modify the jvm args to :
 * "-DNOWECO_LOTUS_USERNAME=_ -DNOWECO_LOTUS_PASSWORD=_ -DNOWECO_PROXY_HOST=_ -DNOWECO_PROXY_PORT=_"
 *
 * @author Gael Lalire
 */
public final class TheTestContext {

    private TheTestContext() {
    }

    public static String getLotusUserName() {
        String property = System.getProperty("NOWECO_LOTUS_USERNAME");
        if (property == null) {
            throw new AssertionFailedError("NOWECO_LOTUS_USERNAME undefined");
        }
        return property;
    }

    public static String getLotusPassword() {
        String property = System.getProperty("NOWECO_LOTUS_PASSWORD");
        if (property == null) {
            throw new AssertionFailedError("NOWECO_LOTUS_PASSWORD undefined");
        }
        return property;
    }

    public static String getLotusPortal() {
        String property = System.getProperty("NOWECO_LOTUS_PORTAL");
        if (property == null) {
            throw new AssertionFailedError("NOWECO_LOTUS_PORTAL undefined");
        }
        return property;
    }

    public static String getProxyHost() {
        return System.getProperty("NOWECO_PROXY_HOST");
    }

    public static int getProxyPort() {
        return Integer.parseInt(System.getProperty("NOWECO_PROXY_PORT"));
    }

}
