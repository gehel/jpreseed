/**
 * Copyright (C) 2014 LedCom (guillaume.lederrey@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.io;

import com.google.common.base.Preconditions;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

/**
 * Wrapper from {@link ByteBuffer} to an {@link InputStream} and the
 * {@link DataInput} interface, converting unchecked exceptions from underlying
 * {@link ByteBuffer} calls to appropriate checked {@link IOException}s.
 * <p/>
 * The byte ordering used depends on the ordering set by
 * {@link ByteBuffer#order(java.nio.ByteOrder)}. If that is explicitly set to
 * {@link java.nio.ByteOrder#LITTLE_ENDIAN}, that will be the ordering of returned values
 * spanning more than one byte.
 * <p/>
 * While useful for typical memory buffers, this may be especially useful for
 * {@link java.nio.MappedByteBuffer} instances.
 * <p/>
 * This class was taken from http://code.google.com/p/guava-libraries/issues/detail?id=592.
 *
 * @author Todd Vierling <tv@duh.org>
 */
public class ByteBufferDataInputStream extends InputStream implements DataInput {
    /**
     * If a NIO unchecked exception is thrown, convert it here to an appropriate
     * checked {@link IOException}. If a {@link NullPointerException} is
     * encountered, {@link #buf} has been nulled by {@link #close()}, so throw a
     * reasonable equivalent.
     *
     * @return existing exception, if unconverted, for rethrowing
     */
    private static RuntimeException convertException(RuntimeException e)
            throws IOException {
        if (e instanceof BufferUnderflowException) {
            throw (IOException) new EOFException().initCause(e);
        }

        if (e instanceof NullPointerException) {
            throw (IOException) new ClosedChannelException();
        }

        return e;
    }

    private ByteBuffer buf;

    public ByteBufferDataInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public final int available() throws IOException {
        try {
            return buf.remaining();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final void close() throws IOException {
        buf = null;
    }

    /**
     * Get the buffer backing this instance.
     *
     * @return backing buffer, or <code>null</code> if this stream is closed
     */
    public final ByteBuffer getBuffer() {
        return buf;
    }

    @Override
    public final int read() throws IOException {
        try {
            if (buf.remaining() == 0) {
                return -1;
            }

            return (((int) buf.get()) & 0xff);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final int read(byte[] b, int off, int len) throws IOException {
        Preconditions.checkNotNull(b);

        try {
            int remain = buf.remaining();
            if (remain == 0) {
                return -1;
            }

            int actualLen = Math.min(len, remain);
            buf.get(b, off, actualLen);
            return actualLen;
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final boolean readBoolean() throws IOException {
        try {
            return (buf.get() != 0);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final byte readByte() throws IOException {
        try {
            return buf.get();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final char readChar() throws IOException {
        try {
            return buf.getChar();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final double readDouble() throws IOException {
        try {
            return buf.getDouble();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final float readFloat() throws IOException {
        try {
            return buf.getFloat();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final void readFully(byte[] b) throws IOException {
        try {
            buf.get(b);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final void readFully(byte[] b, int off, int len) throws IOException {
        Preconditions.checkNotNull(b);

        try {
            buf.get(b, off, len);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final int readInt() throws IOException {
        try {
            return buf.getInt();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Deprecated
    @Override
    public final String readLine() throws IOException {
        StringBuilder sbuf = null;

        try {
            byte b;
            while ((b = buf.get()) != '\n') {
                if (sbuf == null) {
                    sbuf = new StringBuilder();
                }

                if (b != '\r') {
                    sbuf.append((char) b);
                }
            }
        } catch (BufferUnderflowException ignore) {
            // EOF reached; fall out of loop.
        } catch (RuntimeException e) {
            throw convertException(e);
        }

        return (sbuf != null ? sbuf.toString() : null);
    }

    @Override
    public final long readLong() throws IOException {
        try {
            return buf.getLong();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final short readShort() throws IOException {
        try {
            return buf.getShort();
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        try {
            return (((int) buf.get()) & 0xff);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        try {
            return (((int) buf.getShort()) & 0xffff);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    @Override
    public final long skip(long n) throws IOException {
        try {
            // remain is always <intmax, so the cast is OK here
            int actualSkip = (int) Math.min(n, buf.remaining());

            buf.position(buf.position() + actualSkip);

            return actualSkip;
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        try {
            int limit = buf.limit();
            int curpos = buf.position();
            int actualn = Math.min(limit, n);

            buf.position(curpos + actualn);
            return actualn;
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }
}
