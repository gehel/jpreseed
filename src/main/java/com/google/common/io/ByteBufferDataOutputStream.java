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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import com.google.common.base.*;

/**
 * Wrapper from {@link ByteBuffer} to an {@link OutputStream} and the
 * {@link DataOutput} interface, converting unchecked exceptions from underlying
 * {@link ByteBuffer} calls to appropriate checked {@link IOException}s. In
 * particular, an overflow of the buffer will result in an {@link EOFException}
 * being thrown (which is not normally thrown by {@link DataOutputStream}).
 * <p>
 * The byte ordering used depends on the ordering set by
 * {@link ByteBuffer#order(ByteOrder)}. If that is explicitly set to
 * {@link ByteOrder#LITTLE_ENDIAN}, that will be the ordering of written values
 * spanning more than one byte.
 * <p>
 * While useful for typical memory buffers, this may be especially useful for
 * {@link MappedByteBuffer} instances.
 *
 * This class was taken from http://code.google.com/p/guava-libraries/issues/detail?id=592.
 *
 * @author Todd Vierling <tv@duh.org>
 */
public class ByteBufferDataOutputStream extends OutputStream implements
        DataOutput {
    private ByteBuffer buf;
    private DataOutput utfEncoder;

    public ByteBufferDataOutputStream(ByteBuffer buf) {
        Preconditions.checkArgument(!buf.isReadOnly(), "read-only buffer");

        this.buf = buf;
    }

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
        if (e instanceof BufferOverflowException)
            throw (IOException) new EOFException().initCause(e);

        if (e instanceof NullPointerException)
            throw (IOException) new ClosedChannelException();

        return e;
    }

    @Override
    public void close() throws IOException {
        try {
            flush();
        } finally {
            buf = null;
            utfEncoder = null;
        }
    }

    /**
     * If the underlying buffer is an instance of {@link MappedByteBuffer},
     * flushes written data to disk by calling {@link MappedByteBuffer#force()}.
     */
    @Override
    public void flush() throws IOException {
        if (buf instanceof MappedByteBuffer)
            try {
                ((MappedByteBuffer) buf).force();
            } catch (RuntimeException e) {
                throw convertException(e);
            }
    }

    /**
     * Get the buffer backing this instance.
     *
     * @return backing buffer, or <code>null</code> if this stream is closed
     */
    public ByteBuffer getBuffer() {
        return buf;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        Preconditions.checkNotNull(b);

        try {
            buf.put(b, off, len);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        try {
            buf.put((byte) b);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        write(v ? 1 : 0);
    }

    @Override
    public void writeByte(int v) throws IOException {
        write(v);
    }

    @Deprecated
    @Override
    public void writeBytes(String s) throws IOException {
        Preconditions.checkNotNull(s);

        try {
            int len = s.length();

            for (int i = 0; i < len; ++i)
                buf.put((byte) s.charAt(i));
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try {
            buf.putChar((char) v);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeChars(String s) throws IOException {
        Preconditions.checkNotNull(s);

        try {
            int len = s.length();

            for (int i = 0; i < len; ++i)
                buf.putChar(s.charAt(i));
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        try {
            buf.putDouble(v);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        try {
            buf.putFloat(v);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try {
            buf.putInt(v);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try {
            buf.putLong(v);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        try {
            buf.putShort((short) v);
        } catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    @Override
    public void writeUTF(String s) throws IOException {
        Preconditions.checkNotNull(s);

        /*
         * Unfortunately the internal method for this isn't exposed, so it must
         * be done through a wrapper instance.
         */
        if (utfEncoder == null)
            utfEncoder = new DataOutputStream(this);

        utfEncoder.writeUTF(s);
    }
}
