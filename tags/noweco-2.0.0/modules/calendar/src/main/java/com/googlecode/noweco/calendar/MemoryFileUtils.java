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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gael Lalire
 */
public final class MemoryFileUtils {

    private MemoryFileUtils() {
    }

    public static List<MemoryFile> recursiveList(final MemoryFile memoryFile) {
        List<MemoryFile> memoryFiles = new ArrayList<MemoryFile>();
        addAllMemoryFile(memoryFile, memoryFiles);
        return memoryFiles;
    }

    private static void addAllMemoryFile(final MemoryFile memoryFile, final List<MemoryFile> memoryFiles) {
        memoryFiles.add(memoryFile);
        if (memoryFile.isDir()) {
            for (MemoryFile subMemoryFile : memoryFile.getChildren()) {
                addAllMemoryFile(subMemoryFile, memoryFiles);
            }
        }
    }

    public static MemoryFile locate(final MemoryFile from, final String uri) {
        if (!from.isDir()) {
            throw new IllegalArgumentException("from must be a directory");
        }
        MemoryFile memoryFile = from;
        String[] uriTabs = uri.split("/");

        if (uriTabs.length == 0) {
            return from;
        }

        if (uriTabs[0].length() == 0) {
            // absolute URI
            MemoryFile parent = memoryFile.getParent();
            while (parent != null) {
                memoryFile = parent;
                parent = memoryFile.getParent();
            }
        }

        forlabel: for (int i = 0; i < uriTabs.length; i++) {
            String uriTab = uriTabs[i];
            if (uriTab.length() == 0 || uriTab.equals(".")) {
                // ignore double slash (eg /a/b//c = /a/b/c )
                // "a" "b" "" "c"
                continue;
            }
            if (uriTab.equals("..")) {
                MemoryFile parent = memoryFile.getParent();
                if (parent != null) {
                    memoryFile = parent;
                }
                continue;
            }
            for (MemoryFile subMemoryFile : memoryFile.getChildren()) {
                if (uriTab.equals(subMemoryFile.getName())) {
                    memoryFile = subMemoryFile;
                    continue forlabel;
                }
            }
            // not found
            return null;
        }
        return memoryFile;
    }

}
