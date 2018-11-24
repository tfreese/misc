// Created: 03.11.2016
package de.freese.sonstiges.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

/**
 * Adapter für den {@link ByteBuffer} mit AutoExpand-Funktion.<br>
 *
 * @author Thomas Freese
 */
public final class AutoExpandByteBuffer extends AbstractAutoExpandBuffer<ByteBuffer>
{
    /**
     * @author Thomas Freese
     */
    public static class Builder extends AbstractBuilder<AutoExpandByteBuffer>
    {
        /**
         *
         */
        private final boolean direct;

        /**
         * Erzeugt eine neue Instanz von {@link Builder}
         *
         * @param capacity int
         * @param direct boolean
         */
        private Builder(final int capacity, final boolean direct)
        {
            super(capacity);

            this.direct = direct;
        }

        /**
         * @see de.freese.sonstiges.buffer.AbstractAutoExpandBuffer.AbstractBuilder#createAutoExpandBuffer(int)
         */
        @Override
        protected AutoExpandByteBuffer createAutoExpandBuffer(final int capacity)
        {
            ByteBuffer byteBuffer = this.direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);

            return new AutoExpandByteBuffer(byteBuffer);
        }
    }

    /**
     *
     */
    private static final byte[] CRLF = new byte[]
    {
            0x0D, 0x0A
    };

    /**
     * Erzeugt einen neuen Builder.
     *
     * @param capacity int
     * @param direct boolean
     * @return {@link Builder}
     */
    public static Builder builder(final int capacity, final boolean direct)
    {
        return new Builder(capacity, direct);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AutoExpandByteBuffer}
     *
     * @param buffer {@link ByteBuffer}
     */
    private AutoExpandByteBuffer(final ByteBuffer buffer)
    {
        super(buffer);
    }

    /**
     * Liefert einen {@link InputStream}, der aus dem Buffer liest.<br>
     * {@link InputStream#read()} liefert <tt>-1</tt>, wenn der Buffer sein Limit erreicht..
     *
     * @return {@link InputStream}
     */
    public InputStream asInputStream()
    {
        return new InputStream()
        {
            /**
             * @see java.io.InputStream#available()
             */
            @Override
            public int available() throws IOException
            {
                return AutoExpandByteBuffer.this.remaining();
            }

            /**
             * @see java.io.InputStream#mark(int)
             */
            @Override
            public synchronized void mark(final int readlimit)
            {
                AutoExpandByteBuffer.this.mark();
            }

            /**
             * @see java.io.InputStream#markSupported()
             */
            @Override
            public boolean markSupported()
            {
                return true;
            }

            /**
             * @see java.io.InputStream#read()
             */
            @Override
            public int read() throws IOException
            {
                if (AutoExpandByteBuffer.this.hasRemaining())
                {
                    return AutoExpandByteBuffer.this.get() & 0xff;
                }

                return -1;
            }

            /**
             * @see java.io.InputStream#read(byte[], int, int)
             */
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException
            {
                int remaining = AutoExpandByteBuffer.this.remaining();

                if (remaining > 0)
                {
                    int readBytes = Math.min(remaining, len);
                    AutoExpandByteBuffer.this.get(b, off, readBytes);

                    return readBytes;
                }

                return -1;
            }

            /**
             * @see java.io.InputStream#reset()
             */
            @Override
            public synchronized void reset() throws IOException
            {
                AutoExpandByteBuffer.this.reset();
            }

            /**
             * @see java.io.InputStream#skip(long)
             */
            @Override
            public long skip(final long n) throws IOException
            {
                int bytes;

                if (n > Integer.MAX_VALUE)
                {
                    bytes = AutoExpandByteBuffer.this.remaining();
                }
                else
                {
                    bytes = Math.min(AutoExpandByteBuffer.this.remaining(), (int) n);
                }

                AutoExpandByteBuffer.this.skip(bytes);

                return bytes;
            }
        };
    }

    /**
     * Liefert einen {@link OutputStream}, der in de Buffer schreibt.<br>
     *
     * @return {@link OutputStream}
     */
    public OutputStream asOutputStream()
    {
        return new OutputStream()
        {
            /**
             * @see java.io.OutputStream#write(byte[], int, int)
             */
            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException
            {
                AutoExpandByteBuffer.this.put(b, off, len);
            }

            /**
             * @see java.io.OutputStream#write(int)
             */
            @Override
            public void write(final int b) throws IOException
            {
                AutoExpandByteBuffer.this.put((byte) b);
            }
        };
    }

    /**
     * @see de.freese.sonstiges.buffer.AbstractAutoExpandBuffer#createNewBuffer(java.nio.Buffer, int, int)
     */
    @Override
    protected ByteBuffer createNewBuffer(final ByteBuffer buffer, final int newCapacity, final int mark)
    {
        if (newCapacity > buffer.capacity())
        {
            // Alten Zustand speichern.
            int pos = buffer.position();
            ByteOrder bo = buffer.order();

            // // Reallocate.
            ByteBuffer newBuffer = buffer.isDirect() ? ByteBuffer.allocateDirect(newCapacity) : ByteBuffer.allocate(newCapacity);

            buffer.flip();
            newBuffer.put(buffer);

            // Alten Zustand wiederherstellen.
            newBuffer.limit(newCapacity);

            if (mark >= 0)
            {
                newBuffer.position(mark);
                newBuffer.mark();
            }

            newBuffer.position(pos);
            newBuffer.order(bo);

            return newBuffer;
        }

        return buffer;
    }

    /**
     * @param decoder {@link CharsetDecoder}
     * @return {@link CharBuffer}
     * @throws CharacterCodingException Falls was schief geht.
     */
    public CharBuffer decode(final CharsetDecoder decoder) throws CharacterCodingException
    {
        return decoder.reset().decode(getBuffer());
    }

    /**
     * @see ByteBuffer#get()
     * @return byte
     */
    public byte get()
    {
        return getBuffer().get();
    }

    /**
     * @see ByteBuffer#get(byte[], int, int)
     * @param dst byte[]
     * @param offset int
     * @param length int
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer get(final byte[] dst, final int offset, final int length)
    {
        getBuffer().get(dst, offset, length);

        return this;
    }

    /**
     * @see ByteBuffer#getChar()
     * @return char
     */
    public char getChar()
    {
        return getBuffer().getChar();
    }

    /**
     * @see ByteBuffer#getDouble()
     * @return double
     */
    public double getDouble()
    {
        return getBuffer().getDouble();
    }

    /**
     * @see ByteBuffer#getFloat()
     * @return float
     */
    public float getFloat()
    {
        return getBuffer().getFloat();
    }

    /**
     * Liefert die Hexadezimal Darsellung des {@link ByteBuffer}.
     *
     * @return String
     */
    public String getHexDump()
    {
        return getHexDump(Integer.MAX_VALUE);
    }

    /**
     * Liefert die Hexadezimal Darsellung des {@link ByteBuffer}.
     *
     * @param lengthLimit int
     * @return String
     */
    public String getHexDump(final int lengthLimit)
    {
        if (lengthLimit == 0)
        {
            throw new IllegalArgumentException("lengthLimit: " + lengthLimit + " (expected: 1+)");
        }

        ByteBuffer buffer = getBuffer();

        boolean truncate = buffer.remaining() > lengthLimit;
        int size = 0;

        if (truncate)
        {
            size = lengthLimit;
        }
        else
        {
            size = buffer.remaining();
        }

        if (size == 0)
        {
            return "empty";
        }

        int position = buffer.position();

        char[] hexCode = "0123456789ABCDEF".toCharArray();

        StringBuilder sb = new StringBuilder(size * 2);

        for (; size > 0; size--)
        {
            int byteValue = buffer.get() & 0xFF;

            sb.append(hexCode[byteValue >> 4]);
            sb.append(hexCode[byteValue & 0xF]);

            // sb.append(Integer.toString((buffer.get() & 0xFF) + 0x100, 16).substring(1));
            // DatatypeConverter.printHexBinary(checksum);
            // String hex = String.format("%02x", buffer.get());

            // String hex = Integer.toHexString(byteValue);
            // if (hex.length() == 1) {
            // sb.append('0');
            // }
            // sb.append(hex);
        }

        buffer.position(position);

        if (truncate)
        {
            sb.append("...");
        }

        return sb.toString();
    }

    /**
     * @see ByteBuffer#getInt()
     * @return int
     */
    public int getInt()
    {
        return getBuffer().getInt();
    }

    /**
     * @see ByteBuffer#getLong()
     * @return long
     */
    public long getLong()
    {
        return getBuffer().getLong();
    }

    /**
     * @see ByteBuffer#getShort()
     * @return short
     */
    public short getShort()
    {
        return getBuffer().getShort();
    }

    /**
     * @see ByteBuffer#put(byte)
     * @param b byte
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer put(final byte b)
    {
        autoExpand(1);

        getBuffer().put(b);

        return this;
    }

    /**
     * @see ByteBuffer#put(byte[])
     * @param src byte[]
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer put(final byte[] src)
    {
        return put(src, 0, src.length);
    }

    /**
     * @see ByteBuffer#put(byte[], int, int)
     * @param src byte[]
     * @param offset int
     * @param length int
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer put(final byte[] src, final int offset, final int length)
    {
        autoExpand(length - offset);

        getBuffer().put(src, offset, length);

        return this;
    }

    /**
     * @see ByteBuffer#put(ByteBuffer)
     * @param src {@link ByteBuffer}
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer put(final ByteBuffer src)
    {
        autoExpand(src.remaining());
        getBuffer().put(src);

        return this;
    }

    /**
     * @see ByteBuffer#putChar(char)
     * @param value char
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putChar(final char value)
    {
        autoExpand(2);

        getBuffer().putChar(value);

        return this;
    }

    /**
     * @see ByteBuffer#putDouble(double)
     * @param value double
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putDouble(final double value)
    {
        autoExpand(8);

        getBuffer().putDouble(value);

        return this;
    }

    /**
     * @see ByteBuffer#putFloat(float)
     * @param value float
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putFloat(final float value)
    {
        autoExpand(4);

        getBuffer().putFloat(value);

        return this;
    }

    /**
     * @see ByteBuffer#putInt(int)
     * @param value int
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putInt(final int value)
    {
        autoExpand(4);

        getBuffer().putInt(value);

        return this;
    }

    /**
     * Fügt eine Leerzeile hinzu.<br>
     * Default: "\r\n"
     *
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putln()
    {
        return put(CRLF, 0, CRLF.length);
    }

    /**
     * @see ByteBuffer#putLong(long)
     * @param value long
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putLong(final long value)
    {
        autoExpand(8);

        getBuffer().putLong(value);

        return this;
    }

    /**
     * @see ByteBuffer#putShort(short)
     * @param value short
     * @return {@link AutoExpandByteBuffer}
     */
    public AutoExpandByteBuffer putShort(final short value)
    {
        autoExpand(2);

        getBuffer().putShort(value);

        return this;
    }
}
