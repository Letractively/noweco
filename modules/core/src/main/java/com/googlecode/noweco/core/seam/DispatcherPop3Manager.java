package com.googlecode.noweco.core.seam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.googlecode.noweco.core.pop.Pop3Server;
import com.googlecode.noweco.core.pop.spi.Pop3Manager;
import com.googlecode.noweco.core.pop.spi.Pop3Transaction;
import com.googlecode.noweco.core.webmail.cache.CachedWebmail;
import com.googlecode.noweco.core.webmail.lotus.LotusWebmail;

public class DispatcherPop3Manager implements Pop3Manager {

    private Pop3Server pop3Server;

    private List<Dest> dests = new ArrayList<Dest>();

    private CachedWebmail cachedWebmail;

    public DispatcherPop3Manager(File data) {
        pop3Server = new Pop3Server(this, Executors.newFixedThreadPool(3));
//        String id = "horde";
    //    cachedWebmail = new CachedWebmail(new HordeWebmail(), new File(data, id + ".data"));
        String id = "lotus";
        cachedWebmail = new CachedWebmail(new LotusWebmail(), new File(data, id + ".data"));
        dests.add(new Dest(Pattern.compile(".*"), new Pop3ManagerFromWebmail(cachedWebmail), id));
    }

    public void start() throws IOException {
        pop3Server.start();
    }

    public void stop() throws IOException, InterruptedException {
        pop3Server.stop();
        cachedWebmail.release();
    }

    private class Dest {
        Pattern pattern;
        Pop3Manager pop3Manager;
        String id;

        public Dest(Pattern pattern, Pop3Manager pop3Manager, String id) {
            this.pattern = pattern;
            this.pop3Manager = pop3Manager;
            this.id = id;
        }

    }

    public Pop3Transaction authent(String username, String password) throws IOException {
        for (Dest dest : dests) {
            if (dest.pattern.matcher(username).matches()) {
                return dest.pop3Manager.authent(username, password);
            }
        }
        throw new IOException("No manager found for " + username);
    }

}
