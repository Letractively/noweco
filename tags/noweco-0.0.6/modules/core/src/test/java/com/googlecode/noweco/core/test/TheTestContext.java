package com.googlecode.noweco.core.test;

import junit.framework.AssertionFailedError;

/**
 * Following system properties are needed for tests
 * <ul>
 * <li>NOWECO_LOTUS_USERNAME</li>
 * <li>NOWECO_LOTUS_PASSWORD</li>
 * <li>NOWECO_LOTUS_URL</li>
 * <li>NOWECO_PROXY_HOST</li>
 * <li>NOWECO_PROXY_PORT</li>
 * </ul>
 *
 * In Eclipse you can modify the jvm args to :
 * "-DNOWECO_LOTUS_USERNAME=_ -DNOWECO_LOTUS_PASSWORD=_ -DNOWECO_LOTUS_URL=_ -DNOWECO_PROXY_HOST=_ -DNOWECO_PROXY_PORT=_"
 *
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
            throw new AssertionFailedError("NOWECO_LOTUS_USERNAME undefined");
        }
        return property;
    }

    public static String getLotusURL() {
        String property = System.getProperty("NOWECO_LOTUS_URL");
        if (property == null) {
            throw new AssertionFailedError("NOWECO_LOTUS_USERNAME undefined");
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
