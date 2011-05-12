package com.googlecode.noweco.core.seam;

import java.io.IOException;
import java.util.List;

import com.googlecode.noweco.core.pop.spi.Pop3Manager;
import com.googlecode.noweco.core.pop.spi.Pop3Account;

public class DispatcherPop3Manager implements Pop3Manager {

    private List<DispatchedPop3Manager> dispatchedPop3Managers;

    public DispatcherPop3Manager(List<DispatchedPop3Manager> dispatchedPop3Managers) {
        this.dispatchedPop3Managers = dispatchedPop3Managers;
    }

    public void release() throws IOException {
        for (DispatchedPop3Manager dest : dispatchedPop3Managers) {
            dest.getPop3Manager().release();
        }
    }

    public Pop3Account authent(String username, String password) throws IOException {
        for (DispatchedPop3Manager dest : dispatchedPop3Managers) {
            if (dest.getPattern().matcher(username).matches()) {
                return dest.getPop3Manager().authent(username, password);
            }
        }
        throw new IOException("No manager found for " + username);
    }

}
