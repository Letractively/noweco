package com.googlecode.noweco.webmail.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gael Lalire
 */
public class CachedByteBuffer implements ByteBuffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedByteBuffer.class);

    private long writePosition = 0;

    // if writePosition >= buffer.length then buffer is full
    // buffer[writePosition] is empty
    private byte[] buffer = new byte[1024 * 1024];

    private RandomAccessFile randomAccessFile;

    public CachedByteBuffer(final File file) throws IOException {
        writePosition = file.length();
        randomAccessFile = new RandomAccessFile(file, "rw");
    }

    public void write(final byte c) throws IOException {
        if (writePosition < buffer.length) {
            buffer[(int) writePosition] = c;
        } else {
            long filePosition = writePosition - buffer.length;
            randomAccessFile.seek(filePosition);
            randomAccessFile.writeByte(c);
        }
        writePosition++;
    }

    public void write(final byte[] buff, final int off, final int len) throws IOException {
        int maxWrite = Math.min(len, buff.length - off);
        int toBuff;
        long filePosition = writePosition - buffer.length;
        if (filePosition < 0) {
            toBuff = Math.min(-(int) filePosition, maxWrite);
            filePosition = 0;
            System.arraycopy(buff, off, buffer, (int) writePosition, toBuff);
        } else {
            toBuff = 0;
        }
        if (toBuff != maxWrite) {
            randomAccessFile.seek(filePosition);
            for (int i = toBuff; i < maxWrite; i++) {
                randomAccessFile.writeByte(buff[i]);
            }
        }
        writePosition += maxWrite;
    }

    public int read(final long readPosition) throws IOException {
        long remain = writePosition - readPosition;
        if (remain <= 0) {
            return -1;
        }
        long filePosition = readPosition - buffer.length;
        if (filePosition < 0) {
            return buffer[(int) readPosition];
        } else {
            randomAccessFile.seek(filePosition);
            return randomAccessFile.readByte();
        }
    }

    public int read(final long readPosition, final byte[] buff, final int offset, final int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        long remain = writePosition - readPosition;
        if (remain <= 0) {
            return -1;
        }
        int maxRead = Math.min(length, buff.length - offset);
        if (remain < maxRead) {
            maxRead = (int) remain;
        }
        int read = 0;
        long filePosition = readPosition - buffer.length;
        int buffLastIndex;
        int buffFirstIndex;
        if (filePosition < 0) {
            // from buffer
            int fromBuffer = Math.min(-(int) filePosition, maxRead - read);
            System.arraycopy(buffer, (int) readPosition, buff, offset, fromBuffer);
            read += fromBuffer;
            filePosition = 0;
            buffFirstIndex = offset + fromBuffer;
            buffLastIndex = maxRead + offset;
        } else {
            buffFirstIndex = offset;
            buffLastIndex = maxRead + offset;
        }
        if (buffFirstIndex < buffLastIndex) {
            randomAccessFile.seek(filePosition);
            for (int i = buffFirstIndex; i < buffLastIndex; i++) {
                buff[i] = randomAccessFile.readByte();
            }
            read += buffLastIndex - buffFirstIndex;
        }
        return read;
    }

    public void detachFile() throws IOException {
        randomAccessFile.close();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            LOGGER.error("Unable to close cached file", e);
        }
    }

    public long getLength() {
        return writePosition;
    }

    public void setLength(final long length) throws IOException {
        writePosition = length;
        if (length > buffer.length) {
            randomAccessFile.setLength(length - buffer.length);
        } else {
            randomAccessFile.setLength(0);
        }
    }

    public void write(final byte[] buff) throws IOException {
        write(buff, 0, buff.length);
    }

    public int read(final long readPosition, final byte[] buff) throws IOException {
        return read(readPosition, buff, 0, buff.length);
    }
}
