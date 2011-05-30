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

package com.googlecode.noweco.calendar;

import java.io.IOException;

/**
 * @author Gael Lalire
 */
public class CalendarException extends IOException {

    private static final long serialVersionUID = -2552816172239629657L;

    public CalendarException(final String s, final Throwable throwable) {
        super(s);
        initCause(throwable);
    }

}
