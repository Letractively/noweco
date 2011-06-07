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

import java.io.IOException;

import org.apache.http.HttpHost;

import com.googlecode.noweco.webmail.Webmail;
import com.googlecode.noweco.webmail.WebmailConnection;
import com.googlecode.noweco.webmail.portal.PortalConnection;
import com.googlecode.noweco.webmail.portal.PortalConnector;

/**
 *
 * @author Gael Lalire
 */
public class LotusWebmail implements Webmail {

    private HttpHost proxy;

    private PortalConnector portalConnector;

    public WebmailConnection connect(final String user, final String password) throws IOException {
        PortalConnection connect = portalConnector.connect(proxy, user, password);
        return new LotusWebmailConnection(connect.getHttpClient(), connect.getHttpHost(), connect.getPath());
    }

    public void release() {
    }

    public void setAuthent(final PortalConnector portalConnector) {
        this.portalConnector = portalConnector;
    }

    public void setProxy(final String host, final int port) {
        this.proxy = new HttpHost(host, port, "http");
    }

}
