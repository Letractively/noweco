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

package com.googlecode.noweco.core.webmail.portal;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;

/**
 *
 * @author Gael Lalire
 */
public class DefaultPortalConnection implements PortalConnection {

    private HttpClient httpClient;

    private HttpHost httpHost;

    private String path;

    public DefaultPortalConnection(final HttpClient httpClient, final HttpHost httpHost, final String path) {
        this.httpClient = httpClient;
        this.httpHost = httpHost;
        this.path = path;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public String getPath() {
        return path;
    }

}
