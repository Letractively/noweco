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

package com.googlecode.noweco.webmail.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gael Lalire
 */
public class IDGenerator implements Serializable {

    private static final long serialVersionUID = -865303356681083918L;

    private List<Boolean> ids = new ArrayList<Boolean>();

    public int takeID() {
        int i = 0;
        synchronized (ids) {
            while (i < ids.size()) {
                if (ids.get(i)) {
                    ids.set(i, Boolean.FALSE);
                    return i;
                }
                i++;
            }
            ids.add(Boolean.FALSE);
            return i;
        }
    }

    public void releaseID(final int id) {
        synchronized (ids) {
            int last = ids.size() - 1;
            if (last == id) {
                ids.remove(id);
                last--;
                while (last > 0 && ids.get(last)) {
                    ids.remove(last);
                    last--;
                }
            } else {
                ids.set(id, Boolean.TRUE);
            }
        }
    }

}
