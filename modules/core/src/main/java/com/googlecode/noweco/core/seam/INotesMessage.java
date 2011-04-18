package com.googlecode.noweco.core.seam;

import java.io.Reader;
import java.io.StringReader;

import com.googlecode.noweco.core.pop.Message;

public class INotesMessage implements Message {

    private int id;

    private String message;

    private boolean markedForDeletion = false;

    public INotesMessage(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return message.length();
    }

    public Reader getContent() {
        return new StringReader(message);
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void update() {
        if (markedForDeletion) {
// DO DELETE
        }
    }

}
