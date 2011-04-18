package com.googlecode.noweco.core.seam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.googlecode.noweco.core.lotus.INotesConnection;
import com.googlecode.noweco.core.pop.Message;
import com.googlecode.noweco.core.pop.Pop3Transaction;

public class INotesPop3Transaction implements Pop3Transaction {

    private INotesConnection iNotesConnection;

    public INotesPop3Transaction(INotesConnection iNotesConnection) {
        this.iNotesConnection = iNotesConnection;
    }

    public List<? extends Message> getMessages() {
        try {
            return Arrays.asList(new INotesMessage(1, readFull("m1.txt")), new INotesMessage(2, readFull("m2.txt")));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public String readFull(String name) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + name)));
            String line = bufferedReader.readLine();
            while (line != null) {
                result.append(line);
                result.append("\r\n");
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return result.toString();
    }

}
