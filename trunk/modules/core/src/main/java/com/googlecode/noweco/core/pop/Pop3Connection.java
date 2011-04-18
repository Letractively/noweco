package com.googlecode.noweco.core.pop;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pop3Connection implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pop3Connection.class);

    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    public enum Command {
        QUIT, STAT, LIST, RETR, DELE, NOOP, RSET, USER, PASS
    }

    private Socket socket;

    private boolean finished = false;

    private DataOutputStream outputStream;

    private BufferedReader reader;

    private Pop3Manager pop3Manager;

    private Object mutex = new Object();

    public Pop3Connection(Pop3Manager pop3Manager, Socket socket) throws IOException {
        this.pop3Manager = pop3Manager;
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void stop() throws IOException, InterruptedException {
        if (!isFinished()) {
            socket.close();
        }
        synchronized (mutex) {
            while (!finished) {
                mutex.wait();
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    enum State {
        AUTHORIZATION, TRANSACTION, UPDATE;
    };

    public void run() {
        try {
            Pop3Transaction transaction = null;
            State state = State.AUTHORIZATION;
            writeOK("Server ready.");
            mainloop: while (!socket.isClosed()) {
                String command = read();
                LOGGER.info("Receive: {}", command);
                switch (state) {
                case AUTHORIZATION:
                    if (!isCommand(Command.USER, command)) {
                        writeErr("USER command expected");
                        continue mainloop;
                    }
                    String username = command.substring(Command.USER.name().length() + 1);
                    writeOK("Wait for password");
                    command = read();
                    if (isCommand(Command.QUIT, command)) {
                        writeOK("Bye");
                        break mainloop;
                    }
                    if (isCommand(Command.PASS, command)) {
                        String password = command.substring(Command.PASS.name().length() + 1);
                        LOGGER.info("Authent with user {} and password {}", username, password);
                        transaction = pop3Manager.authent(username, password);
                        if (transaction != null) {
                            LOGGER.info("Authent OK");
                            state = State.TRANSACTION;
                            writeOK("Authentifed");
                        } else {
                            LOGGER.info("Authent KO");
                            writeErr("Unable to authent");
                        }
                        continue mainloop;
                    }
                    break;
                case TRANSACTION:
                    if (isCommand(Command.QUIT, command)) {
                        writeOK("Bye");
                        state = State.UPDATE;
                        continue mainloop;
                    } else if (isCommand(Command.STAT, command)) {
                        List<? extends Message> messages = transaction.getMessages();
                        int size = 0;
                        int count = 0;
                        for (Message message : messages) {
                            if (!message.isMarkedForDeletion()) {
                                size += message.getSize();
                                count++;
                            }
                        }
                        writeOK(count + " " + size);
                    } else if (isCommand(Command.LIST, command)) {
                        String arg = command.substring(Command.LIST.name().length()).trim();
                        List<? extends Message> messages = transaction.getMessages();
                        if (arg.length() == 0) {
                            writeOK("Begin to list");
                            for (Message message : messages) {
                                if (!message.isMarkedForDeletion()) {
                                    writeLine(message.getId() + " " + message.getSize());
                                }
                            }
                            writeEndOfLines();
                        } else {
                            try {
                                int id = Integer.parseInt(arg);
                                boolean found = false;
                                for (Message message : messages) {
                                    if (message.getId() == id) {
                                        if (message.isMarkedForDeletion()) {
                                            break;
                                        }
                                        writeOK(message.getId() + " " + message.getSize());
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    writeErr("Message with " + id + " id not found");
                                }
                            } catch (NumberFormatException e) {
                                writeErr("LIST param must be an integer");
                            }
                        }
                    } else if (isCommand(Command.NOOP, command)) {
                        writeOK(null);
                    } else if (isCommand(Command.RSET, command)) {
                        // avoid message deletion
                        List<? extends Message> messages = transaction.getMessages();
                        for (Message message : messages) {
                            if (message.isMarkedForDeletion()) {
                                message.setMarkedForDeletion(false);
                            }
                        }
                        writeOK(null);
                    } else if (isCommand(Command.DELE, command)) {
                        String arg = command.substring(Command.DELE.name().length()).trim();
                        try {
                            int id = Integer.parseInt(arg);
                            boolean found = false;
                            List<? extends Message> messages = transaction.getMessages();
                            for (Message message : messages) {
                                if (message.getId() == id) {
                                    if (message.isMarkedForDeletion()) {
                                        break;
                                    }
                                    message.setMarkedForDeletion(true);
                                    writeOK("Message deleted");
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                writeErr("Message with " + id + " id not found");
                            }
                        } catch (NumberFormatException e) {
                            writeErr("DELE param must be an integer");
                        }
                    } else if (isCommand(Command.RETR, command)) {
                        String arg = command.substring(Command.RETR.name().length()).trim();
                        try {
                            int id = Integer.parseInt(arg);
                            boolean found = false;
                            List<? extends Message> messages = transaction.getMessages();
                            for (Message message : messages) {
                                if (message.getId() == id) {
                                    if (message.isMarkedForDeletion()) {
                                        break;
                                    }
                                    message.setMarkedForDeletion(true);
                                    writeOK("Begin message content");
                                    BufferedReader bufferedReader = new BufferedReader(message.getContent());
                                    String line = bufferedReader.readLine();
                                    while (line != null) {
                                        writeLine(line);
                                        line = bufferedReader.readLine();
                                    }
                                    writeEndOfLines();
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                writeErr("Message with " + id + " id not found");
                            }
                        } catch (NumberFormatException e) {
                            writeErr("DELE param must be an integer");
                        }
                    } else {
                        writeErr("Unsupported command");
                    }

                    break;
                case UPDATE:
                    List<? extends Message> messages = transaction.getMessages();
                    for (Message message : messages) {
                        if (message.isMarkedForDeletion()) {
                            message.update();
                        }
                    }
                    break mainloop;
                default:
                    break;
                }
            }
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            synchronized (mutex) {
                finished = true;
                mutex.notifyAll();
            }
        }
    }

    public boolean isCommand(Command command, String line) {
        if (!line.toUpperCase().startsWith(command.name())) {
            return false;
        }
        if (line.length() > command.name().length()) {
            return line.charAt(command.name().length()) == ' ';
        }
        return true;
    }

    public String read() throws IOException {
        return reader.readLine();
    }

    public void writeLine(String line) throws IOException {
        LOGGER.info("Response line : {}", line);
        if (!line.isEmpty() && line.charAt(0) == '.') {
            write(".");
        }
        write(line);
        write("\r\n");
    }

    public void writeEndOfLines() throws IOException {
        LOGGER.info("No more lines");
        write(".");
        write("\r\n");
        outputStream.flush();
    }

    public void writeOK(String message) throws IOException {
        LOGGER.info("OK Answer : {}", message);
        write("+OK");
        if (message != null) {
            write(" ");
            write(message);
        }
        write("\r\n");
        outputStream.flush();
    }

    public void writeErr(String message) throws IOException {
        LOGGER.info("Err Answer : {}", message);
        write("-ERR ");
        write(message);
        write("\r\n");
        outputStream.flush();
    }

    public void write(String message) throws IOException {
        outputStream.write(message.getBytes(US_ASCII));
    }

}
