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

package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.util.List;

import com.googlecode.noweco.core.pop.spi.Pop3Account;
import com.googlecode.noweco.core.pop.spi.Pop3Manager;

/**
 *
 * @author Gael Lalire
 */
public class DispatcherPop3Manager implements Pop3Manager {

    private List<DispatchedPop3Manager> dispatchedPop3Managers;

    public DispatcherPop3Manager(final List<DispatchedPop3Manager> dispatchedPop3Managers) {
        this.dispatchedPop3Managers = dispatchedPop3Managers;
    }

    public void release() throws IOException {
        for (DispatchedPop3Manager dest : dispatchedPop3Managers) {
            dest.getPop3Manager().release();
        }
    }

    public Pop3Account authent(final String username, final String password) throws IOException {
        for (DispatchedPop3Manager dest : dispatchedPop3Managers) {
            if (dest.getPattern().matcher(username).matches()) {
                return dest.getPop3Manager().authent(username, password);
            }
        }
        throw new IOException("No manager found for " + username);
    }

}
