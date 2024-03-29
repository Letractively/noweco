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

package com.googlecode.noweco.pop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.pop.spi.Pop3Account;
import com.googlecode.noweco.pop.spi.Pop3Manager;
import com.googlecode.noweco.pop.spi.Pop3Message;

/**
 *
 * @author Gael Lalire
 */
public class Pop3Connection implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pop3Connection.class);

    private static final String US_ASCII = "US-ASCII";

    /**
     * @author Gael Lalire
     */
    private static enum Command {
        QUIT, STAT, LIST, RETR, DELE, NOOP, RSET, USER, PASS, TOP, UIDL;
    }

    private Thread thread;

    private Socket socket;

    private boolean finished = false;

    private OutputStream outputStream;

    private BufferedReader reader;

    private Pop3Manager pop3Manager;

    private Object mutex = new Object();

    public Pop3Connection(final Pop3Manager pop3Manager, final Socket socket) throws IOException {
        this.pop3Manager = pop3Manager;
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        outputStream = socket.getOutputStream();
    }

    public void stop() throws IOException, InterruptedException {
        if (!isFinished()) {
            socket.close();
        }
        thread.interrupt();
        synchronized (mutex) {
            while (!finished) {
                mutex.wait();
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    /**
     * @author Gael Lalire
     */
    private static enum State {
        AUTHORIZATION, TRANSACTION, UPDATE;
    };

    public void run() {
        try {
            this.thread = Thread.currentThread();
            Pop3Account pop3Account = null;
            try {
                List<? extends Pop3Message> messages = null;
                boolean[] markForDeletion = null;
                State state = State.AUTHORIZATION;
                writeOK("Server ready.");
                mainloop: while (!socket.isClosed()) {
                    String command = read();
                    switch (state) {
                    case AUTHORIZATION:
                        if (isCommand(Command.QUIT, command)) {
                            writeOK("Bye");
                            break mainloop;
                        }
                        if (!isCommand(Command.USER, command)) {
                            writeErr("USER or QUIT command expected");
                            continue mainloop;
                        }
                        String username = command.substring(Command.USER.name().length() + 1);
                        writeOK("Wait for password");
                        command = read();
                        if (isCommand(Command.QUIT, command)) {
                            writeOK("Bye");
                            break mainloop;
                        }
                        if (!isCommand(Command.PASS, command)) {
                            writeErr("PASS or QUIT command expected");
                            continue mainloop;
                        }
                        String password = command.substring(Command.PASS.name().length() + 1);
                        LOGGER.info("Authent {} user", username);
                        try {
                            pop3Account = pop3Manager.authent(username, password);
                            LOGGER.info("Authent OK");
                            try {
                                messages = pop3Account.getMessages();
                                markForDeletion = new boolean[messages.size()];
                                state = State.TRANSACTION;
                                writeOK("Authentified");
                            } catch (IOException e) {
                                LOGGER.error("Unable to list", e);
                                writeErr("Authent OK, but unable to list");
                            }
                        } catch (UnknownHostException e) {
                            throw new Pop3SocketException("disconnected", e);
                        } catch (IOException e) {
                            LOGGER.info("Authent KO", e);
                            writeErr("Unable to authent");
                        }
                        continue mainloop;
                    case TRANSACTION:
                        if (isCommand(Command.QUIT, command)) {
                            state = State.UPDATE;
                            break mainloop;
                        } else if (isCommand(Command.STAT, command)) {
                            try {
                                int size = 0;
                                int count = 0;
                                int pos = 0;
                                for (Pop3Message message : messages) {
                                    if (!markForDeletion[pos]) {
                                        size += message.getSize();
                                        count++;
                                    }
                                    pos++;
                                }
                                writeOK(count + " " + size);
                            } catch (IOException e) {
                                LOGGER.error("Unable to stat", e);
                                writeErr("Size fetch issue");
                            }
                        } else if (isCommand(Command.LIST, command)) {
                            String arg = command.substring(Command.LIST.name().length()).trim();
                            if (arg.length() == 0) {
                                try {
                                    List<String> lines = new ArrayList<String>();
                                    int pos = 0;
                                    for (Pop3Message message : messages) {
                                        if (!markForDeletion[pos]) {
                                            lines.add((pos + 1) + " " + message.getSize());
                                        }
                                        pos++;
                                    }
                                    writeOK("Begin to list");
                                    for (String line : lines) {
                                        writeLine(line);
                                    }
                                    writeEndOfLines();
                                } catch (IOException e) {
                                    LOGGER.error("Unable to list", e);
                                    writeErr("Size fetch issue");
                                }
                            } else {
                                try {
                                    int id = Integer.parseInt(arg);
                                    int pos = id - 1;
                                    if (pos < 0 || pos >= messages.size() || markForDeletion[pos]) {
                                        writeErr("Message with " + id + " id not found");
                                    } else {
                                        writeOK(id + " " + messages.get(pos).getSize());
                                    }
                                } catch (IOException e) {
                                    LOGGER.error("Unable to list", e);
                                    writeErr("Size fetch issue");
                                } catch (NumberFormatException e) {
                                    LOGGER.error("Unable to list", e);
                                    writeErr("LIST param must be an integer");
                                }
                            }
                        } else if (isCommand(Command.UIDL, command)) {
                            String arg = command.substring(Command.UIDL.name().length()).trim();
                            if (arg.length() == 0) {
                                List<String> lines = new ArrayList<String>();
                                int pos = 0;
                                for (Pop3Message message : messages) {
                                    if (!markForDeletion[pos]) {
                                        lines.add((pos + 1) + " " + message.getUID());
                                    }
                                    pos++;
                                }
                                writeOK("Begin to list");
                                for (String line : lines) {
                                    writeLine(line);
                                }
                                writeEndOfLines();
                            } else {
                                try {
                                    int id = Integer.parseInt(arg);
                                    int pos = id - 1;
                                    if (pos < 0 || pos >= messages.size() || markForDeletion[pos]) {
                                        writeErr("Message with " + id + " id not found");
                                    } else {
                                        writeOK(id + " " + messages.get(pos).getUID());
                                    }
                                } catch (NumberFormatException e) {
                                    LOGGER.error("Unable to uid list", e);
                                    writeErr("UIDL param must be an integer");
                                }
                            }
                        } else if (isCommand(Command.NOOP, command)) {
                            writeOK(null);
                        } else if (isCommand(Command.RSET, command)) {
                            // avoid message deletion
                            Arrays.fill(markForDeletion, false);
                            writeOK(null);
                        } else if (isCommand(Command.DELE, command)) {
                            String arg = command.substring(Command.DELE.name().length()).trim();
                            try {
                                int id = Integer.parseInt(arg);
                                int pos = id - 1;
                                if (pos < 0 || pos >= messages.size() || markForDeletion[pos]) {
                                    writeErr("Message with " + id + " id not found");
                                } else {
                                    markForDeletion[pos] = true;
                                    writeOK("Message deleted");
                                }
                            } catch (NumberFormatException e) {
                                writeErr("DELE param must be an integer");
                            }
                        } else if (isCommand(Command.TOP, command)) {
                            String args = command.substring(Command.TOP.name().length()).trim();
                            try {
                                String[] split = args.split(" ");
                                if (split.length != 2) {
                                    writeErr("TOP needs 2 params : msg and line number");
                                }
                                int id = Integer.parseInt(split[0]);
                                int contentLines = Integer.parseInt(split[1]);
                                int pos = id - 1;
                                if (pos < 0 || pos >= messages.size() || markForDeletion[pos]) {
                                    writeErr("Message with " + id + " id not found");
                                } else {
                                    Pop3Message message = messages.get(pos);

                                    writeOK("Begin headers content");
                                    try {
                                        Pop3InputStreamFilter inputStream = new Pop3InputStreamFilter(message.getContent(), contentLines);
                                        byte[] buffer = new byte[1024];
                                        int read = inputStream.read(buffer);
                                        while (read != -1) {
                                            outputStream.write(buffer, 0, read);
                                            read = inputStream.read(buffer);
                                        }
                                        if (!inputStream.isEOLTerminated()) {
                                            writeLine("");
                                        }
                                        writeEndOfLines();
                                    } catch (IOException e) {
                                        LOGGER.error("Unable to read message", e);
                                        throw new Pop3SocketException(e);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                writeErr("DELE param must be an integer");
                            }
                        } else if (isCommand(Command.RETR, command)) {
                            String arg = command.substring(Command.RETR.name().length()).trim();
                            try {
                                int id = Integer.parseInt(arg);
                                int pos = id - 1;
                                if (pos < 0 || pos >= messages.size() || markForDeletion[pos]) {
                                    writeErr("Message with " + id + " id not found");
                                } else {
                                    Pop3Message message = messages.get(pos);
                                    writeOK("Begin message content");
                                    LOGGER.info("Send content of {}", message.getUID());
                                    try {
                                        Pop3InputStreamFilter inputStream = new Pop3InputStreamFilter(message.getContent());
                                        byte[] buffer = new byte[1024];
                                        int read = inputStream.read(buffer);
                                        while (read != -1) {
                                            outputStream.write(buffer, 0, read);
                                            read = inputStream.read(buffer);
                                        }
                                        if (!inputStream.isEOLTerminated()) {
                                            writeLine("");
                                        }
                                        writeEndOfLines();
                                    } catch (IOException e) {
                                        LOGGER.error("Unable to read message", e);
                                        throw new Pop3SocketException(e);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                writeErr("DELE param must be an integer");
                            }
                        } else {
                            writeErr("Unsupported command");
                        }

                        break;
                    default:
                        break;
                    }
                }
                if (state == State.UPDATE) {
                    try {
                        List<String> uids = new ArrayList<String>();
                        int pos = 0;
                        for (Pop3Message message : messages) {
                            if (markForDeletion[pos]) {
                                uids.add(message.getUID());
                            }
                            pos++;
                        }
                        if (uids.size() != 0) {
                            LOGGER.info("Suppress messages : {}", uids);
                            pop3Account.delete(uids);
                        }
                        writeOK("Updated");
                    } catch (IOException e) {
                        LOGGER.error("Unable to update", e);
                        writeErr("Update of one message failed");
                    }
                    LOGGER.info("Transaction committed");
                }
            } catch (Pop3SocketException e) {
                LOGGER.error("Client connection failed", e);
            } finally {
                if (pop3Account != null) {
                    try {
                        pop3Account.close();
                    } catch (IOException e) {
                        LOGGER.error("Unable to close pop3Account", e);
                    }
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close outputStream", e);
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close reader", e);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close socket", e);
                }
                synchronized (mutex) {
                    finished = true;
                    mutex.notifyAll();
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Uncatched throwable", e);
        }
    }

    public boolean isCommand(final Command command, final String line) {
        if (!line.toUpperCase().startsWith(command.name())) {
            return false;
        }
        if (line.length() > command.name().length()) {
            return line.charAt(command.name().length()) == ' ';
        }
        return true;
    }

    public String read() throws Pop3SocketException {
        try {
            String line = reader.readLine();
            LOGGER.debug("Receive: {}", line);
            if (line == null) {
                throw new Pop3SocketException(new IOException("Client ends connection"));
            }
            return line;
        } catch (IOException e) {
            throw new Pop3SocketException(e);
        }
    }

    public void writeLine(final String line) throws Pop3SocketException {
        LOGGER.trace("Response line : {}", line);
        if (line.length() != 0 && line.charAt(0) == '.') {
            write(".");
        }
        write(line);
        write("\r\n");
    }

    public void writeEndOfLines() throws Pop3SocketException {
        LOGGER.trace("No more lines");
        write(".");
        write("\r\n");
        flush();
    }

    public void writeOK(final String message) throws Pop3SocketException {
        LOGGER.debug("OK Answer : {}", message);
        write("+OK");
        if (message != null) {
            write(" ");
            write(message);
        }
        write("\r\n");
        flush();
    }

    public void writeErr(final String message) throws Pop3SocketException {
        LOGGER.debug("Err Answer : {}", message);
        write("-ERR ");
        write(message);
        write("\r\n");
        flush();
    }

    public void write(final String message) throws Pop3SocketException {
        try {
            outputStream.write(message.getBytes(US_ASCII));
        } catch (IOException e) {
            throw new Pop3SocketException(e);
        }
    }

    public void flush() throws Pop3SocketException {
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new Pop3SocketException(e);
        }
    }

}
