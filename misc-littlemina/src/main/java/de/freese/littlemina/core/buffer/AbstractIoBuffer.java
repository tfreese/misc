// Created: 16.01.2010
/**
 * 16.01.2010
 */
package de.freese.littlemina.core.buffer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Objects;

/**
 * BasisAdapter für den {@link ByteBuffer} mit zusätzlichen Methoden.<br>
 *
 * @author Thomas Freese
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractIoBuffer
{
    /**
     *
     */
    private static IoBufferAllocator allocator = new SimpleBufferAllocator();

    /**
     * A mask for a byte
     */
    private static final long BYTE_MASK = 0xFFL;

    /**
     * Returns the direct or heap buffer which is capable to store the specified amount of bytes.
     *
     * @param capacity the capacity of the buffer
     * @return {@link AbstractIoBuffer}
     */
    public static AbstractIoBuffer allocate(final int capacity)
    {
        // @see #setUseDirectBuffer(boolean)
        return allocate(capacity, false);
    }

    /**
     * Returns the buffer which is capable of the specified size.
     *
     * @param capacity the capacity of the buffer
     * @param direct <tt>true</tt> to get a direct buffer, <tt>false</tt> to get a heap buffer.
     * @return {@link AbstractIoBuffer}
     */
    public static AbstractIoBuffer allocate(final int capacity, final boolean direct)
    {
        if (capacity < 0)
        {
            throw new IllegalArgumentException("capacity: " + capacity);
        }

        return getAllocator().allocate(capacity, direct);
    }

    /**
     * Increases the capacity of this buffer. If the new capacity is less than or equal to the current capacity, this method returns
     * silently. If the new capacity is greater than the current capacity, the buffer is reallocated while retaining the position, limit,
     * mark and the content of the buffer.
     *
     * @param byteBuffer {@link ByteBuffer}
     * @param newCapacity int
     * @param mark int; 0 as default
     * @return {@link ByteBuffer}
     */
    public static ByteBuffer createNewByteBuffer(final ByteBuffer byteBuffer, final int newCapacity, final int mark)
    {
        // Allocate a new buffer and transfer all settings to it.
        if (newCapacity > byteBuffer.capacity())
        {
            // Expand:
            // Save the state.
            int pos = byteBuffer.position();
            // int limit = byteBuffer.limit();
            ByteOrder bo = byteBuffer.order();

            // // Reallocate.
            ByteBuffer newBuf = getAllocator().allocateNioBuffer(newCapacity, byteBuffer.isDirect());

            byteBuffer.flip();
            // byteBuffer.rewind();
            newBuf.put(byteBuffer);

            // Restore the state.
            // newBuf.limit(limit); // Causes StackOverflowError
            newBuf.limit(newCapacity);

            if (mark >= 0)
            {
                newBuf.position(mark);
                newBuf.mark();
            }

            newBuf.position(pos);
            newBuf.order(bo);

            return newBuf;
        }

        return byteBuffer;
    }

    /**
     * @return {@link IoBufferAllocator}
     */
    public static IoBufferAllocator getAllocator()
    {
        return allocator;
    }

    /**
     * Normalizes the specified capacity of the buffer to power of 2, which is often helpful for optimal memory usage and performance.<br>
     * If it is lower or zero, it returns 1024.<br>
     *
     * @param requestedCapacity int
     * @return int
     */
    public static int normalizeCapacity(final int requestedCapacity)
    {
        if (requestedCapacity <= 0)
        {
            return 1024;
        }

        int newCapacity = Integer.highestOneBit(requestedCapacity);
        newCapacity <<= (newCapacity < requestedCapacity ? 1 : 0);

        // return newCapacity < 0 ? Integer.MAX_VALUE : newCapacity;
        return newCapacity;
    }

    /**
     * @param newAllocator {@link IoBufferAllocator}
     */
    public static void setAllocator(final IoBufferAllocator newAllocator)
    {
        Objects.requireNonNull(newAllocator, "allocator required");

        IoBufferAllocator oldAllocator = allocator;

        allocator = newAllocator;

        if (null != oldAllocator)
        {
            oldAllocator.dispose();
        }
    }

    /**
     * A flag set to true if the buffer can extend automatically
     */
    private boolean autoExpand = true;

    /**
     * A flag set to true if the buffer can shrink automatically
     */
    private boolean autoShrink = true;

    /**
     * We don't have any access to Buffer.markValue(), so we need to track it down, which will cause small extra overhead.
     */
    private int mark = -1;

    /**
     * The minimum number of bytes the IoBuffer can hold
     */
    private final int minimumCapacity;

    /**
     * Erstellt ein neues {@link AbstractIoBuffer} Object.
     *
     * @param minimumCapacity int
     */
    AbstractIoBuffer(final int minimumCapacity)
    {
        super();

        this.minimumCapacity = minimumCapacity;
    }

    /**
     * @return byte[]
     * @see ByteBuffer#array()
     */
    public abstract byte[] array();

    /**
     * @return int
     * @see ByteBuffer#arrayOffset()
     */
    public abstract int arrayOffset();

    /**
     * @return {@link CharBuffer}
     * @see ByteBuffer#asCharBuffer()
     */
    public CharBuffer asCharBuffer()
    {
        return getByteBuffer().asCharBuffer();
    }

    /**
     * @return {@link DoubleBuffer}
     * @see ByteBuffer#asDoubleBuffer()
     */
    public DoubleBuffer asDoubleBuffer()
    {
        return getByteBuffer().asDoubleBuffer();
    }

    /**
     * @return {@link FloatBuffer}
     * @see ByteBuffer#asFloatBuffer()
     */
    public FloatBuffer asFloatBuffer()
    {
        return getByteBuffer().asFloatBuffer();
    }

    /**
     * Returns an {@link InputStream} that reads the data from this buffer. {@link InputStream#read()} returns <tt>-1</tt> if the buffer
     * position reaches to the limit.
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
                return AbstractIoBuffer.this.remaining();
            }

            /**
             * @see java.io.InputStream#mark(int)
             */
            @Override
            public synchronized void mark(final int readlimit)
            {
                AbstractIoBuffer.this.mark();
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
                if (AbstractIoBuffer.this.hasRemaining())
                {
                    return AbstractIoBuffer.this.get() & 0xff;
                }

                return -1;
            }

            /**
             * @see java.io.InputStream#read(byte[], int, int)
             */
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException
            {
                int remaining = AbstractIoBuffer.this.remaining();

                if (remaining > 0)
                {
                    int readBytes = Math.min(remaining, len);
                    AbstractIoBuffer.this.get(b, off, readBytes);

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
                AbstractIoBuffer.this.reset();
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
                    bytes = AbstractIoBuffer.this.remaining();
                }
                else
                {
                    bytes = Math.min(AbstractIoBuffer.this.remaining(), (int) n);
                }

                AbstractIoBuffer.this.skip(bytes);

                return bytes;
            }
        };
    }

    /**
     * @return {@link IntBuffer}
     * @see ByteBuffer#asIntBuffer()
     */
    public IntBuffer asIntBuffer()
    {
        return getByteBuffer().asIntBuffer();
    }

    /**
     * @return {@link LongBuffer}
     * @see ByteBuffer#asLongBuffer()
     */
    public LongBuffer asLongBuffer()
    {
        return getByteBuffer().asLongBuffer();
    }

    /**
     * Returns an {@link OutputStream} that appends the data into this buffer. Please note that the {@link OutputStream#write(int)} will
     * throw a {@link BufferOverflowException} instead of an {@link IOException} in case of buffer overflow. Please set <tt>autoExpand</tt>
     * property by calling {@link #setAutoExpand(boolean)} to prevent the unexpected runtime exception.
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
                AbstractIoBuffer.this.put(b, off, len);
            }

            /**
             * @see java.io.OutputStream#write(int)
             */
            @Override
            public void write(final int b) throws IOException
            {
                AbstractIoBuffer.this.put((byte) b);
            }
        };
    }

    /**
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#asReadOnlyBuffer()
     */
    public abstract AbstractIoBuffer asReadOnlyBuffer();

    /**
     * @return {@link ShortBuffer}
     * @see ByteBuffer#asShortBuffer()
     */
    public ShortBuffer asShortBuffer()
    {
        return getByteBuffer().asShortBuffer();
    }

    /**
     * @return int
     * @see ByteBuffer#capacity()
     */
    public int capacity()
    {
        return getByteBuffer().capacity();
    }

    /**
     * @return {@link AbstractIoBuffer}
     * @see java.nio.Buffer#clear()
     */
    public AbstractIoBuffer clear()
    {
        getByteBuffer().clear();
        this.mark = -1;

        return this;
    }

    /**
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#compact()
     */
    public AbstractIoBuffer compact()
    {
        int remaining = remaining();
        int capacity = capacity();

        if (capacity == 0)
        {
            return this;
        }

        if (isAutoShrink() && (remaining <= (capacity >>> 2)) && (capacity > this.minimumCapacity))
        {
            int newCapacity = capacity;
            int minCapacity = Math.max(this.minimumCapacity, remaining << 1);

            for (;;)
            {
                if ((newCapacity >>> 1) < minCapacity)
                {
                    break;
                }

                newCapacity >>>= 1;
            }

            newCapacity = Math.max(minCapacity, newCapacity);

            if (newCapacity == capacity)
            {
                return this;
            }

            // Shrink and compact:
            // // Save the state.
            ByteOrder bo = order();

            // // Sanity check.
            if (remaining > newCapacity)
            {
                throw new IllegalStateException("The amount of the remaining bytes is greater than " + "the new capacity.");
            }

            // // Reallocate.
            ByteBuffer oldBuf = getByteBuffer();
            ByteBuffer newBuf = getAllocator().allocateNioBuffer(newCapacity, isDirect());
            newBuf.put(oldBuf);
            setByteBuffer(newBuf);

            // // Restore the state.
            getByteBuffer().order(bo);
        }
        else
        {
            getByteBuffer().compact();
        }

        this.mark = -1;

        return this;
    }

    /**
     * @return {@link AbstractIoBuffer}
     * @see java.nio.Buffer#flip()
     */
    public AbstractIoBuffer flip()
    {
        getByteBuffer().flip();
        this.mark = -1;

        return this;
    }

    /**
     * @return byte
     * @see ByteBuffer#get()
     */
    public byte get()
    {
        return getByteBuffer().get();
    }

    /**
     * @param dst byte[]
     * @param offset int
     * @param length int
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#get(byte[], int, int)
     */
    public AbstractIoBuffer get(final byte[] dst, final int offset, final int length)
    {
        getByteBuffer().get(dst, offset, length);

        return this;
    }

    /**
     * @param index int
     * @return byte
     * @see ByteBuffer#get(int)
     */
    public byte get(final int index)
    {
        return getByteBuffer().get(index);
    }

    /**
     * @return {@link ByteBuffer}
     */
    public abstract ByteBuffer getByteBuffer();

    /**
     * @return char
     * @see ByteBuffer#getChar()
     */
    public char getChar()
    {
        return getByteBuffer().getChar();
    }

    /**
     * @param index int
     * @return char
     * @see ByteBuffer#getChar(int)
     */
    public char getChar(final int index)
    {
        return getByteBuffer().getChar(index);
    }

    /**
     * @return double
     * @see ByteBuffer#getDouble()
     */
    public double getDouble()
    {
        return getByteBuffer().getDouble();
    }

    /**
     * @param index int
     * @return double
     * @see ByteBuffer#getDouble(int)
     */
    public double getDouble(final int index)
    {
        return getByteBuffer().getDouble(index);
    }

    /**
     * Reads a byte from the buffer and returns the correlating enum constant defined by the specified enum type.
     *
     * @param <E> The enum type to return
     * @param enumClass The enum's class object
     * @return {@link Enum}
     */
    public <E extends Enum<E>> E getEnum(final Class<E> enumClass)
    {
        short i = getUnsigned();

        E[] enumConstants = enumClass.getEnumConstants();

        if (i > enumConstants.length)
        {
            throw new IndexOutOfBoundsException(
                    String.format("%d is too large of an ordinal to convert to the enum %s", i, enumClass.getName()));
        }

        return enumConstants[i];
    }

    /**
     * @return float
     * @see ByteBuffer#getFloat()
     */
    public float getFloat()
    {
        return getByteBuffer().getFloat();
    }

    /**
     * @param index int
     * @return float
     * @see ByteBuffer#getFloat(int)
     */
    public float getFloat(final int index)
    {
        return getByteBuffer().getFloat(index);
    }

    /**
     * Returns hexdump of this buffer. The data and pointer are not changed as a result of this method call.
     *
     * @return hexidecimal representation of this buffer
     */
    public String getHexDump()
    {
        return getHexDump(Integer.MAX_VALUE);
    }

    /**
     * Return hexdump of this buffer with limited length.
     *
     * @param lengthLimit The maximum number of bytes to dump from the current buffer position.
     * @return hexidecimal representation of this buffer
     */
    public String getHexDump(final int lengthLimit)
    {
        return IoBufferHexDumper.getHexdump(this, lengthLimit);
    }

    /**
     * @return int
     * @see ByteBuffer#getInt()
     */
    public int getInt()
    {
        return getByteBuffer().getInt();
    }

    /**
     * @param index int
     * @return int
     * @see ByteBuffer#getInt(int)
     */
    public int getInt(final int index)
    {
        return getByteBuffer().getInt(index);
    }

    /**
     * @return long
     * @see ByteBuffer#getLong()
     */
    public long getLong()
    {
        return getByteBuffer().getLong();
    }

    /**
     * @param index int
     * @return long
     * @see ByteBuffer#getLong(int)
     */
    public long getLong(final int index)
    {
        return getByteBuffer().getLong(index);
    }

    /**
     * Relative <i>get</i> method for reading a medium int value. Reads the next three bytes at this buffer's current position, composing
     * them into an int value according to the current byte order, and then increments the position by three.
     *
     * @return The medium int value at the buffer's current position
     */
    public int getMediumInt()
    {
        byte b1 = get();
        byte b2 = get();
        byte b3 = get();

        if (ByteOrder.BIG_ENDIAN.equals(order()))
        {
            return getMediumInt(b1, b2, b3);
        }

        return getMediumInt(b3, b2, b1);
    }

    /**
     * Absolute <i>get</i> method for reading a medium int value. Reads the next three bytes at this buffer's current position, composing
     * them into an int value according to the current byte order.
     *
     * @param index The index from which the medium int will be read
     * @return The medium int value at the given index
     * @throws IndexOutOfBoundsException If <tt>index</tt> is negative or not smaller than the buffer's limit
     */
    public int getMediumInt(final int index)
    {
        byte b1 = get(index);
        byte b2 = get(index + 1);
        byte b3 = get(index + 2);

        if (ByteOrder.BIG_ENDIAN.equals(order()))
        {
            return getMediumInt(b1, b2, b3);
        }

        return getMediumInt(b3, b2, b1);
    }

    /**
     * Reads a Java object from the buffer using the context {@link ClassLoader} of the current thread.
     *
     * @return Object
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public Object getObject() throws ClassNotFoundException
    {
        return getObject(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Reads a Java object from the buffer using the specified <tt>classLoader</tt>.
     *
     * @param classLoader {@link ClassLoader}
     * @return Object
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public Object getObject(final ClassLoader classLoader) throws ClassNotFoundException
    {
        if (!prefixedDataAvailable(4))
        {
            throw new BufferUnderflowException();
        }

        int length = getInt();

        if (length <= 4)
        {
            throw new RuntimeException("Object length should be greater than 4: " + length);
        }

        int oldLimit = limit();
        limit(position() + length);

        try
        {
            ObjectInputStream in = new ObjectInputStream(asInputStream())
            {
                /**
                 * @see java.io.ObjectInputStream#readClassDescriptor()
                 */
                @Override
                protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException
                {
                    int type = read();

                    if (type < 0)
                    {
                        throw new EOFException();
                    }

                    switch (type)
                    {
                        case 0: // Primitive types
                            return super.readClassDescriptor();
                        case 1: // Non-primitive types
                            String className = readUTF();
                            Class<?> clazz = Class.forName(className, true, classLoader);
                            return ObjectStreamClass.lookup(clazz);
                        default:
                            throw new StreamCorruptedException("Unexpected class descriptor type: " + type);
                    }
                }

                /**
                 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
                 */
                @Override
                protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException
                {
                    String name = desc.getName();

                    try
                    {
                        return Class.forName(name, false, classLoader);
                    }
                    catch (ClassNotFoundException ex)
                    {
                        return super.resolveClass(desc);
                    }
                }
            };

            return in.readObject();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            limit(oldLimit);
        }
    }

    /**
     * @return short
     * @see ByteBuffer#getShort()
     */
    public short getShort()
    {
        return getByteBuffer().getShort();
    }

    /**
     * @param index int
     * @return short
     * @see ByteBuffer#getShort()
     */
    public short getShort(final int index)
    {
        return getByteBuffer().getShort(index);
    }

    /**
     * Reads a <code>NUL</code>-terminated string from this buffer using the specified <code>decoder</code> and returns it. This method
     * reads until the limit of this buffer if no <tt>NUL</tt> is found.
     *
     * @param decoder {@link CharsetDecoder}
     * @return String
     * @throws CharacterCodingException Falls was schief geht.
     */
    public String getString(final CharsetDecoder decoder) throws CharacterCodingException
    {
        if (!hasRemaining())
        {
            return "";
        }

        boolean utf16 = decoder.charset().name().startsWith("UTF-16");

        int oldPos = position();
        int oldLimit = limit();
        int end = -1;
        int newPos = 0;

        if (!utf16)
        {
            end = indexOf((byte) 0x00);

            if (end < 0)
            {
                newPos = end = oldLimit;
            }
            else
            {
                newPos = end + 1;
            }
        }
        else
        {
            int i = oldPos;

            for (;;)
            {
                boolean wasZero = get(i) == 0;
                i++;

                if (i >= oldLimit)
                {
                    break;
                }

                if (get(i) != 0)
                {
                    i++;

                    if (i >= oldLimit)
                    {
                        break;
                    }

                    continue;
                }

                if (wasZero)
                {
                    end = i - 1;
                    break;
                }
            }

            if (end < 0)
            {
                newPos = end = oldPos + ((oldLimit - oldPos) & 0xFFFFFFFE);
            }
            else
            {
                if ((end + 2) <= oldLimit)
                {
                    newPos = end + 2;
                }
                else
                {
                    newPos = end;
                }
            }
        }

        if (oldPos == end)
        {
            position(newPos);

            return "";
        }

        limit(end);
        decoder.reset();

        int expectedLength = (int) (remaining() * decoder.averageCharsPerByte()) + 1;
        CharBuffer out = CharBuffer.allocate(expectedLength);

        for (;;)
        {
            CoderResult cr;

            if (hasRemaining())
            {
                cr = decoder.decode(getByteBuffer(), out, true);
            }
            else
            {
                cr = decoder.flush(out);
            }

            if (cr.isUnderflow())
            {
                break;
            }

            if (cr.isOverflow())
            {
                CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
                out.flip();
                o.put(out);
                out = o;
                continue;
            }

            if (cr.isError())
            {
                // Revert the buffer back to the previous state.
                limit(oldLimit);
                position(oldPos);
                cr.throwException();
            }
        }

        limit(oldLimit);
        position(newPos);

        return out.flip().toString();
    }

    /**
     * Reads one unsigned byte as a short integer.
     *
     * @return short
     */
    public short getUnsigned()
    {
        return (short) (get() & 0xff);
    }

    /**
     * Reads one byte as an unsigned short integer.
     *
     * @param index int
     * @return short
     */
    public short getUnsigned(final int index)
    {
        return (short) (get(index) & 0xff);
    }

    /**
     * Reads four bytes unsigned integer.
     *
     * @return long
     */
    public long getUnsignedInt()
    {
        return getInt() & 0xffffffffL;
    }

    /**
     * Reads four bytes unsigned integer.
     *
     * @param index int
     * @return long
     */
    public long getUnsignedInt(final int index)
    {
        return getInt(index) & 0xffffffffL;
    }

    /**
     * Relative <i>get</i> method for reading an unsigned medium int value. Reads the next three bytes at this buffer's current position,
     * composing them into an int value according to the current byte order, and then increments the position by three.
     *
     * @return The unsigned medium int value at the buffer's current position
     */
    public int getUnsignedMediumInt()
    {
        int b1 = getUnsigned();
        int b2 = getUnsigned();
        int b3 = getUnsigned();

        if (ByteOrder.BIG_ENDIAN.equals(order()))
        {
            return (b1 << 16) | (b2 << 8) | b3;
        }

        return (b3 << 16) | (b2 << 8) | b1;
    }

    /**
     * Absolute <i>get</i> method for reading an unsigned medium int value. Reads the next three bytes at this buffer's current position,
     * composing them into an int value according to the current byte order.
     *
     * @param index The index from which the unsigned medium int will be read
     * @return The unsigned medium int value at the given index
     * @throws IndexOutOfBoundsException If <tt>index</tt> is negative or not smaller than the buffer's limit
     */
    public int getUnsignedMediumInt(final int index)
    {
        int b1 = getUnsigned(index);
        int b2 = getUnsigned(index + 1);
        int b3 = getUnsigned(index + 2);

        if (ByteOrder.BIG_ENDIAN.equals(order()))
        {
            return (b1 << 16) | (b2 << 8) | b3;
        }

        return (b3 << 16) | (b2 << 8) | b1;
    }

    /**
     * Reads two bytes unsigned integer.
     *
     * @return int
     */
    public int getUnsignedShort()
    {
        return getShort() & 0xffff;
    }

    /**
     * Reads two bytes unsigned integer.
     *
     * @param index int
     * @return int
     */
    public int getUnsignedShort(final int index)
    {
        return getShort(index) & 0xffff;
    }

    /**
     * @return boolean
     * @see ByteBuffer#hasArray()
     */
    public abstract boolean hasArray();

    /**
     * @return boolean
     * @see java.nio.Buffer#hasRemaining()
     */
    public boolean hasRemaining()
    {
        return limit() > position();
    }

    /**
     * Returns the first occurence position of the specified byte from the current position to the current limit.
     *
     * @param b byte
     * @return <tt>-1</tt> if the specified byte is not found
     */
    public int indexOf(final byte b)
    {
        if (hasArray())
        {
            int arrayOffset = arrayOffset();
            int beginPos = arrayOffset + position();
            int limit = arrayOffset + limit();
            byte[] array = array();

            for (int i = beginPos; i < limit; i++)
            {
                if (array[i] == b)
                {
                    return i - arrayOffset;
                }
            }
        }
        else
        {
            int beginPos = position();
            int limit = limit();

            for (int i = beginPos; i < limit; i++)
            {
                if (get(i) == b)
                {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Returns <tt>true</tt> if and only if <tt>autoExpand</tt> is turned on.
     *
     * @return boolean
     */
    public boolean isAutoExpand()
    {
        return this.autoExpand;
    }

    /**
     * Returns <tt>true</tt> if and only if <tt>autoShrink</tt> is turned on.
     *
     * @return boolean
     */
    public boolean isAutoShrink()
    {
        return this.autoShrink;
    }

    /**
     * @return boolean
     * @see ByteBuffer#isDirect()
     */
    public boolean isDirect()
    {
        return getByteBuffer().isDirect();
    }

    /**
     * @return int
     * @see java.nio.Buffer#limit()
     */
    public int limit()
    {
        return getByteBuffer().limit();
    }

    /**
     * @param newLimit int
     * @return {@link AbstractIoBuffer}
     * @see java.nio.Buffer#limit(int)
     */
    public AbstractIoBuffer limit(final int newLimit)
    {
        autoExpand(newLimit, 0);
        getByteBuffer().limit(newLimit);

        if (this.mark > newLimit)
        {
            this.mark = -1;
        }

        return this;
    }

    /**
     * @return {@link AbstractIoBuffer}
     * @see java.nio.Buffer#mark()
     */
    public AbstractIoBuffer mark()
    {
        getByteBuffer().mark();
        this.mark = position();

        return this;
    }

    /**
     * @return {@link ByteOrder}
     * @see ByteBuffer#order()
     */
    public ByteOrder order()
    {
        return getByteBuffer().order();
    }

    /**
     * @return int
     * @see java.nio.Buffer#position()
     */
    public int position()
    {
        return getByteBuffer().position();
    }

    /**
     * @param newPosition int
     * @return {@link AbstractIoBuffer}
     * @see java.nio.Buffer#position(int)
     */
    public AbstractIoBuffer position(final int newPosition)
    {
        autoExpand(newPosition, 0);
        getByteBuffer().position(newPosition);

        if (this.mark > newPosition)
        {
            this.mark = -1;
        }

        return this;
    }

    /**
     * Returns <tt>true</tt> if this buffer contains a data which has a data length as a prefix and the buffer has remaining data as enough
     * as specified in the data length field. This method is identical with
     * <tt>prefixedDataAvailable( prefixLength, Integer.MAX_VALUE )</tt>. Please not that using this method can allow DoS (Denial of
     * Service) attack in case the remote peer sends too big data length value. It is recommended to use
     * {@link #prefixedDataAvailable(int, int)} instead.
     *
     * @param prefixLength the length of the prefix field (1, 2, or 4)
     * @return boolean
     * @throws IllegalArgumentException if prefixLength is wrong
     * @throws RuntimeException if data length is negative
     */
    public boolean prefixedDataAvailable(final int prefixLength)
    {
        return prefixedDataAvailable(prefixLength, Integer.MAX_VALUE);
    }

    /**
     * Returns <tt>true</tt> if this buffer contains a data which has a data length as a prefix and the buffer has remaining data as enough
     * as specified in the data length field.
     *
     * @param prefixLength the length of the prefix field (1, 2, or 4)
     * @param maxDataLength the allowed maximum of the read data length
     * @return boolean
     * @throws IllegalArgumentException if prefixLength is wrong
     * @throws RuntimeException if data length is negative or greater then <tt>maxDataLength</tt>
     */
    public boolean prefixedDataAvailable(final int prefixLength, final int maxDataLength)
    {
        if (remaining() < prefixLength)
        {
            return false;
        }

        int dataLength;

        switch (prefixLength)
        {
            case 1:
                dataLength = getUnsigned(position());
                break;
            case 2:
                dataLength = getUnsignedShort(position());
                break;
            case 4:
                dataLength = getInt(position());
                break;
            default:
                throw new IllegalArgumentException("prefixLength: " + prefixLength);
        }

        if ((dataLength < 0) || (dataLength > maxDataLength))
        {
            throw new RuntimeException("dataLength: " + dataLength);
        }

        return (remaining() - prefixLength) >= dataLength;
    }

    /**
     * Writes the content of the specified <tt>src</tt> into this buffer.
     *
     * @param src {@link AbstractIoBuffer}
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer put(final AbstractIoBuffer src)
    {
        return put(src.getByteBuffer());
    }

    /**
     * @param b byte
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#put(byte)
     */
    public AbstractIoBuffer put(final byte b)
    {
        autoExpand(1);
        getByteBuffer().put(b);

        return this;
    }

    /**
     * @param src byte[]
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#put(byte[])
     */
    public AbstractIoBuffer put(final byte[] src)
    {
        return put(src, 0, src.length);
    }

    /**
     * @param src byte[]
     * @param offset int
     * @param length int
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#put(byte[], int, int)
     */
    public AbstractIoBuffer put(final byte[] src, final int offset, final int length)
    {
        autoExpand(length);
        getByteBuffer().put(src, offset, length);

        return this;
    }

    /**
     * Writes the content of the specified <tt>src</tt> into this buffer.
     *
     * @param src {@link ByteBuffer}
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer put(final ByteBuffer src)
    {
        autoExpand(src.remaining());
        getByteBuffer().put(src);

        return this;
    }

    /**
     * @param value char
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#putChar(char)
     */
    public AbstractIoBuffer putChar(final char value)
    {
        autoExpand(2);
        getByteBuffer().putChar(value);

        return this;
    }

    /**
     * @param value double
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#putDouble(double)
     */
    public AbstractIoBuffer putDouble(final double value)
    {
        autoExpand(8);
        getByteBuffer().putDouble(value);

        return this;
    }

    /**
     * Writes an enum's ordinal value to the buffer as a byte.
     *
     * @param e The enum to write to the buffer
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer putEnum(final Enum<?> e)
    {
        if (e.ordinal() > BYTE_MASK)
        {
            throw new IllegalArgumentException(enumConversionErrorMessage(e, "byte"));
        }

        return put((byte) e.ordinal());
    }

    /**
     * @param value float
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#putFloat(float)
     */
    public AbstractIoBuffer putFloat(final float value)
    {
        autoExpand(4);
        getByteBuffer().putFloat(value);

        return this;
    }

    /**
     * @param value int
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#putInt(int)
     */
    public AbstractIoBuffer putInt(final int value)
    {
        autoExpand(4);
        getByteBuffer().putInt(value);

        return this;
    }

    /**
     * @param value long
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#putLong(int, long)
     */
    public AbstractIoBuffer putLong(final long value)
    {
        autoExpand(8);
        getByteBuffer().putLong(value);

        return this;
    }

    /**
     * Relative <i>put</i> method for writing a medium int value. Writes three bytes containing the given int value, in the current byte
     * order, into this buffer at the current position, and then increments the position by three.
     *
     * @param value The medium int value to be written
     * @return {@link AbstractIoBuffer}
     * @throws BufferOverflowException If there are fewer than three bytes remaining in this buffer
     */
    public AbstractIoBuffer putMediumInt(final int value)
    {
        byte b1 = (byte) (value >> 16);
        byte b2 = (byte) (value >> 8);
        byte b3 = (byte) value;

        if (ByteOrder.BIG_ENDIAN.equals(order()))
        {
            put(b1).put(b2).put(b3);
        }
        else
        {
            put(b3).put(b2).put(b1);
        }

        return this;
    }

    /**
     * Writes the specified Java object to the buffer.
     *
     * @param o Object
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer putObject(final Object o)
    {
        int oldPos = position();
        skip(4); // Make a room for the length field.

        try
        {
            ObjectOutputStream out = new ObjectOutputStream(asOutputStream())
            {
                /**
                 * @see java.io.ObjectOutputStream#writeClassDescriptor(java.io.ObjectStreamClass)
                 */
                @Override
                protected void writeClassDescriptor(final ObjectStreamClass desc) throws IOException
                {
                    if (desc.forClass().isPrimitive())
                    {
                        write(0);
                        super.writeClassDescriptor(desc);
                    }
                    else
                    {
                        write(1);
                        writeUTF(desc.getName());
                    }
                }
            };

            out.writeObject(o);
            out.flush();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        // Fill the length field
        int newPos = position();
        position(oldPos);
        putInt(newPos - oldPos - 4);
        position(newPos);

        return this;
    }

    /**
     * @param value short
     * @return {@link AbstractIoBuffer}
     * @see ByteBuffer#putShort(short)
     */
    public AbstractIoBuffer putShort(final short value)
    {
        autoExpand(2);
        getByteBuffer().putShort(value);

        return this;
    }

    /**
     * Writes the content of <code>in</code> into this buffer using the specified <code>encoder</code>. This method doesn't terminate string
     * with <tt>NUL</tt>. You have to do it by yourself.
     *
     * @param val {@link CharSequence}
     * @param encoder {@link CharsetEncoder}
     * @return {@link AbstractIoBuffer}
     * @throws CharacterCodingException Falls was schief geht.
     * @throws BufferOverflowException if the specified string doesn't fit
     */
    public AbstractIoBuffer putString(final CharSequence val, final CharsetEncoder encoder) throws CharacterCodingException
    {
        if (val.length() == 0)
        {
            return this;
        }

        CharBuffer in = CharBuffer.wrap(val);
        encoder.reset();

        int expandedState = 0;

        for (;;)
        {
            CoderResult cr;

            if (in.hasRemaining())
            {
                cr = encoder.encode(in, getByteBuffer(), true);
            }
            else
            {
                cr = encoder.flush(getByteBuffer());
            }

            if (cr.isUnderflow())
            {
                break;
            }

            if (cr.isOverflow())
            {
                if (isAutoExpand())
                {
                    switch (expandedState)
                    {
                        case 0:
                            autoExpand((int) Math.ceil(in.remaining() * encoder.averageBytesPerChar()));
                            expandedState++;
                            break;
                        case 1:
                            autoExpand((int) Math.ceil(in.remaining() * encoder.maxBytesPerChar()));
                            expandedState++;
                            break;
                        default:
                            throw new RuntimeException("Expanded by " + (int) Math.ceil(in.remaining() * encoder.maxBytesPerChar())
                                    + " but that wasn't enough for '" + val + "'");
                    }

                    continue;
                }
            }
            else
            {
                expandedState = 0;
            }

            cr.throwException();
        }

        return this;
    }

    /**
     * @return int
     * @see java.nio.Buffer#remaining()
     */
    public int remaining()
    {
        return limit() - position();
    }

    /**
     * @return {@link AbstractIoBuffer}
     * @see java.nio.Buffer#reset()
     */
    public AbstractIoBuffer reset()
    {
        getByteBuffer().reset();

        return this;
    }

    /**
     * Turns on or off <tt>autoExpand</tt>.
     *
     * @param autoExpand boolean
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer setAutoExpand(final boolean autoExpand)
    {
        this.autoExpand = autoExpand;

        return this;
    }

    /**
     * Turns on or off <tt>autoShrink</tt>.
     *
     * @param autoShrink boolean
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer setAutoShrink(final boolean autoShrink)
    {
        this.autoShrink = autoShrink;

        return this;
    }

    /**
     * Forwards the position of this buffer as the specified <code>size</code> bytes.
     *
     * @param size int
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer skip(final int size)
    {
        autoExpand(size);

        return position(position() + size);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        if (isDirect())
        {
            buf.append("DirectBuffer");
        }
        else
        {
            buf.append("HeapBuffer");
        }

        buf.append("[pos=");
        buf.append(position());
        buf.append(" lim=");
        buf.append(limit());
        buf.append(" cap=");
        buf.append(capacity());
        buf.append(": ");
        buf.append(getHexDump(16));
        buf.append(']');

        return buf.toString();
    }

    /**
     * This method forwards the call to {@link #autoExpand(int, int)}.
     *
     * @param expectedRemaining int
     * @return {@link AbstractIoBuffer}
     */
    protected AbstractIoBuffer autoExpand(final int expectedRemaining)
    {
        return autoExpand(position(), expectedRemaining);
    }

    /**
     * Expand the Buffer only when <tt>autoExpand</tt> property is <tt>true</tt>.
     *
     * @param pos int
     * @param expectedRemaining int
     * @return {@link AbstractIoBuffer}
     */
    protected AbstractIoBuffer autoExpand(final int pos, final int expectedRemaining)
    {
        if (!isAutoExpand())
        {
            return this;
        }

        int end = pos + expectedRemaining;
        int newCapacity = normalizeCapacity(end);

        if (newCapacity > capacity())
        {
            // The buffer needs expansion.
            setByteBuffer(createNewByteBuffer(getByteBuffer(), newCapacity, this.mark));
        }

        if (end > limit())
        {
            // We call limit() directly to prevent StackOverflowError
            getByteBuffer().limit(end);
        }

        return this;
    }

    /**
     * @param e {@link Enum}
     * @param type String
     * @return String
     */
    protected String enumConversionErrorMessage(final Enum<?> e, final String type)
    {
        return String.format("%s.%s has an ordinal value too large for a %s", e.getClass().getName(), e.name(), type);
    }

    /**
     * @param b1 byte
     * @param b2 byte
     * @param b3 byte
     * @return int
     */
    protected int getMediumInt(final byte b1, final byte b2, final byte b3)
    {
        int ret = ((b1 << 16) & 0xff0000) | ((b2 << 8) & 0xff00) | (b3 & 0xff);

        // Check to see if the medium int is negative (high bit in b1 set)
        if ((b1 & 0x80) == 0x80)
        {
            // Make the the whole int negative
            ret |= 0xff000000;
        }

        return ret;
    }

    /**
     * Sets the underlying NIO buffer instance.
     *
     * @param newByteBuffer {@link ByteBuffer}
     */
    protected abstract void setByteBuffer(ByteBuffer newByteBuffer);
}
