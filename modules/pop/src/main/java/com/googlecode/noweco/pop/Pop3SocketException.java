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

package com.googlecode.noweco.pop;

import java.io.IOException;

/**
 *
 * @author Gael Lalire
 */
public class Pop3SocketException extends Exception {

    private static final long serialVersionUID = -2486751912349485990L;

    public Pop3SocketException(final String message, final IOException cause) {
        super(message, cause);
    }

    public Pop3SocketException(final IOException cause) {
        super("Pop socket issue", cause);
    }

}
