package com.googlecode.noweco.core.pop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import com.googlecode.noweco.core.pop.spi.Pop3Manager;

public class Pop3Server implements Runnable {

    private static final int POP3_PORT = 110;

    private List<Pop3Connection> connections;

    private ServerSocket serverSocket;

    private Thread thread;

    private Executor executor;

    private Pop3Manager pop3Manager;

    public Pop3Server(Pop3Manager pop3Manager, Executor executor) {
        this.executor = executor;
        connections = new ArrayList<Pop3Connection>();
        this.pop3Manager = pop3Manager;
    }

    public void start() throws IOException {
        connections.clear();
        serverSocket = new ServerSocket(getPop3Port());
        thread = new Thread(this);
        thread.start();
    }

    public void stop() throws IOException, InterruptedException {
        serverSocket.close();
        for (Pop3Connection connection : connections) {
            connection.stop();
        }
        thread.join();
    }

    public int getPop3Port() {
        return POP3_PORT;
    }

    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket accept = serverSocket.accept();
                Pop3Connection command = new Pop3Connection(pop3Manager, accept);
                connections.add(command);
                executor.execute(command);
                Iterator<Pop3Connection> iterator = connections.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().isFinished()) {
                        iterator.remove();
                    }
                }
            } catch (IOException e) {

            }
        }
    }
}
