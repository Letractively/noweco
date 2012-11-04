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

import com.googlecode.noweco.calendar.caldav.Prop;

/**
 * @author Gael Lalire
 */
public class MemoryFile {

    private MemoryFile parent;

    private String name;

    private List<MemoryFile> children;

    private Prop prop;

    private byte[] content;

    public MemoryFile(final MemoryFile parent, final String name, final boolean dir) {
        if (parent != null) {
            parent.getChildren().add(this);
        }
        this.parent = parent;
        this.name = name;
        if (dir) {
            this.children = new ArrayList<MemoryFile>();
        }
        this.prop = new Prop();
    }

    public MemoryFile getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public boolean isDir() {
        return children != null;
    }

    public List<MemoryFile> getChildren() {
        return children;
    }

    public Prop getProp() {
        return prop;
    }

    public String getURI() {
        if (parent == null) {
            return "/" + name;
        }
        if (isDir()) {
            return parent.getURI() + name + "/";
        }
        return parent.getURI() + name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

}
