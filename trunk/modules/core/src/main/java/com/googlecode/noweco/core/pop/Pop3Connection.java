package com.googlecode.noweco.core.pop;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.noweco.core.pop.spi.Message;
import com.googlecode.noweco.core.pop.spi.Pop3Account;
import com.googlecode.noweco.core.pop.spi.Pop3Manager;

public class Pop3Connection implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pop3Connection.class);

    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    public enum Command {
        QUIT, STAT, LIST, RETR, DELE, NOOP, RSET, USER, PASS, TOP, UIDL;
    }

    private Thread thread;

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

    enum State {
        AUTHORIZATION, TRANSACTION, UPDATE;
    };

    public void run() {
        this.thread = Thread.currentThread();
        try {
            Pop3Account pop3Account = null;
            List<? extends Message> messages = null;
            boolean[] markForDeletion = null;
            State state = State.AUTHORIZATION;
            writeOK("Server ready.");
            mainloop: while (!socket.isClosed()) {
                String command = read();
                LOGGER.debug("Receive: {}", command);
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
                            for (Message message : messages) {
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
                                for (Message message : messages) {
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
                            for (Message message : messages) {
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
                            int id = Integer.parseInt(split[0]);
                            int contentLines = Integer.parseInt(split[1]);
                            int pos = id - 1;
                            if (pos < 0 || pos >= messages.size() || markForDeletion[pos]) {
                                writeErr("Message with " + id + " id not found");
                            } else {
                                Message message = messages.get(pos);

                                writeOK("Begin headers content");
                                try {
                                    BufferedReader bufferedReader;
                                    if (contentLines <= 0) {
                                        bufferedReader = new BufferedReader(message.getHeaders());
                                    } else {
                                        bufferedReader = new BufferedReader(message.getContent());
                                    }
                                    String line = bufferedReader.readLine();
                                    while (line != null && line.length() != 0) {
                                        writeLine(line);
                                        line = bufferedReader.readLine();
                                    }
                                    writeLine("");
                                    if (contentLines != 0 && line != null) {
                                        line = bufferedReader.readLine();
                                        while (line != null && contentLines > 0) {
                                            writeLine(line);
                                            line = bufferedReader.readLine();
                                            contentLines--;
                                        }
                                    }
                                    writeEndOfLines();
                                } catch (IOException e) {
                                    LOGGER.error("Unable to read message", e);
                                    throw new PopSocketException(e);
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
                                Message message = messages.get(pos);
                                writeOK("Begin message content");
                                LOGGER.info("Send content of {}", message.getUID());
                                try {
                                    BufferedReader bufferedReader = new BufferedReader(message.getContent());
                                    String line = bufferedReader.readLine();
                                    while (line != null) {
                                        writeLine(line);
                                        line = bufferedReader.readLine();
                                    }
                                    writeEndOfLines();
                                } catch (IOException e) {
                                    LOGGER.error("Unable to read message", e);
                                    throw new PopSocketException(e);
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
                    for (Message message : messages) {
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
        } catch (PopSocketException e) {
            LOGGER.error("Client connection failed", e);
        } finally {
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

    public String read() throws PopSocketException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new PopSocketException(e);
        }
    }

    public void writeLine(String line) throws PopSocketException {
        LOGGER.trace("Response line : {}", line);
        if (!line.isEmpty() && line.charAt(0) == '.') {
            write(".");
        }
        write(line);
        write("\r\n");
    }

    public void writeEndOfLines() throws PopSocketException {
        LOGGER.trace("No more lines");
        write(".");
        write("\r\n");
        flush();
    }

    public void writeOK(String message) throws PopSocketException {
        LOGGER.debug("OK Answer : {}", message);
        write("+OK");
        if (message != null) {
            write(" ");
            write(message);
        }
        write("\r\n");
        flush();
    }

    public void writeErr(String message) throws PopSocketException {
        LOGGER.debug("Err Answer : {}", message);
        write("-ERR ");
        write(message);
        write("\r\n");
        flush();
    }

    public void write(String message) throws PopSocketException {
        try {
            outputStream.write(message.getBytes(US_ASCII));
        } catch (IOException e) {
            throw new PopSocketException(e);
        }
    }

    public void flush() throws PopSocketException {
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new PopSocketException(e);
        }
    }

}
