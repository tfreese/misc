// Created: 16.01.2010
/**
 * 16.01.2010
 */
package de.freese.littlemina.core.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
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

/**
 * BasisAdapter fuer den {@link ByteBuffer} mit zusaetzlichen Methoden.<br>
 * 
 * @author Thomas Freese</p>
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class IoBuffer
{
	/**
	 *
	 */
	private static IoBufferAllocator allocator = new SimpleBufferAllocator();

	/**
	 * Returns the direct or heap buffer which is capable to store the specified amount of bytes.
	 * 
	 * @param capacity the capacity of the buffer
	 * @return {@link IoBuffer}
	 */
	public static IoBuffer allocate(final int capacity)
	{
		// @see #setUseDirectBuffer(boolean)
		return allocate(capacity, false);
	}

	/**
	 * Returns the buffer which is capable of the specified size.
	 * 
	 * @param capacity the capacity of the buffer
	 * @param direct <tt>true</tt> to get a direct buffer, <tt>false</tt> to get a heap buffer.
	 * @return {@link IoBuffer}
	 */
	public static IoBuffer allocate(final int capacity, final boolean direct)
	{
		if (capacity < 0)
		{
			throw new IllegalArgumentException("capacity: " + capacity);
		}

		return allocator.allocate(capacity, direct);
	}

	/**
	 * @return {@link IoBufferAllocator}
	 */
	public static IoBufferAllocator getAllocator()
	{
		return allocator;
	}

	/**
	 * Normalizes the specified capacity of the buffer to power of 2, which is often helpful for
	 * optimal memory usage and performance. If it is greater than or equal to
	 * {@link Integer#MAX_VALUE}, it returns {@link Integer#MAX_VALUE}. If it is zero, it returns
	 * zero.
	 * 
	 * @param requestedCapacity int
	 * @return int
	 */
	protected static int normalizeCapacity(final int requestedCapacity)
	{
		if (requestedCapacity < 0)
		{
			return Integer.MAX_VALUE;
		}

		int newCapacity = Integer.highestOneBit(requestedCapacity);
		newCapacity <<= (newCapacity < requestedCapacity ? 1 : 0);

		return newCapacity < 0 ? Integer.MAX_VALUE : newCapacity;
	}

	/**
	 * @param newAllocator {@link IoBufferAllocator}
	 */
	public static void setAllocator(final IoBufferAllocator newAllocator)
	{
		if (newAllocator == null)
		{
			throw new NullPointerException("allocator");
		}

		IoBufferAllocator oldAllocator = allocator;

		allocator = newAllocator;

		if (null != oldAllocator)
		{
			oldAllocator.dispose();
		}
	}

	/**
	 * Erstellt ein neues {@link IoBuffer} Object.
	 */
	IoBuffer()
	{
		super();
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
	public abstract CharBuffer asCharBuffer();

	/**
	 * @return {@link DoubleBuffer}
	 * @see ByteBuffer#asDoubleBuffer()
	 */
	public abstract DoubleBuffer asDoubleBuffer();

	/**
	 * @return {@link FloatBuffer}
	 * @see ByteBuffer#asFloatBuffer()
	 */
	public abstract FloatBuffer asFloatBuffer();

	/**
	 * Returns an {@link InputStream} that reads the data from this buffer.
	 * {@link InputStream#read()} returns <tt>-1</tt> if the buffer position reaches to the limit.
	 * 
	 * @return {@link InputStream}
	 */
	public abstract InputStream asInputStream();

	/**
	 * @return {@link IntBuffer}
	 * @see ByteBuffer#asIntBuffer()
	 */
	public abstract IntBuffer asIntBuffer();

	/**
	 * @return {@link LongBuffer}
	 * @see ByteBuffer#asLongBuffer()
	 */
	public abstract LongBuffer asLongBuffer();

	/**
	 * Returns an {@link OutputStream} that appends the data into this buffer. Please note that the
	 * {@link OutputStream#write(int)} will throw a {@link BufferOverflowException} instead of an
	 * {@link IOException} in case of buffer overflow. Please set <tt>autoExpand</tt> property by
	 * calling {@link #setAutoExpand(boolean)} to prevent the unexpected runtime exception.
	 * 
	 * @return {@link OutputStream}
	 */
	public abstract OutputStream asOutputStream();

	/**
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#asReadOnlyBuffer()
	 */
	public abstract IoBuffer asReadOnlyBuffer();

	/**
	 * @return {@link ShortBuffer}
	 * @see ByteBuffer#asShortBuffer()
	 */
	public abstract ShortBuffer asShortBuffer();

	/**
	 * @return int
	 * @see ByteBuffer#capacity()
	 */
	public abstract int capacity();

	/**
	 * Increases the capacity of this buffer. If the new capacity is less than or equal to the
	 * current capacity, this method returns silently. If the new capacity is greater than the
	 * current capacity, the buffer is reallocated while retaining the position, limit, mark and the
	 * content of the buffer.
	 * 
	 * @param newCapacity int
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer capacity(int newCapacity);

	/**
	 * @return {@link IoBuffer}
	 * @see java.nio.Buffer#clear()
	 */
	public abstract IoBuffer clear();

	/**
	 * @return IoBuffer
	 * @see ByteBuffer#compact()
	 */
	public abstract IoBuffer compact();

	/**
	 * Changes the capacity and limit of this buffer so this buffer get the specified
	 * <tt>expectedRemaining</tt> room from the current position. This method works even if you
	 * didn't set <tt>autoExpand</tt> to <tt>true</tt>.
	 * 
	 * @param expectedRemaining int
	 * @return {@link IoBuffer}
	 */
	protected abstract IoBuffer expand(int expectedRemaining);

	/**
	 * Changes the capacity and limit of this buffer so this buffer get the specified
	 * <tt>expectedRemaining</tt> room from the specified <tt>position</tt>. This method works even
	 * if you didn't set <tt>autoExpand</tt> to <tt>true</tt>.
	 * 
	 * @param position int
	 * @param expectedRemaining int
	 * @return {@link IoBuffer}
	 */
	protected abstract IoBuffer expand(int position, int expectedRemaining);

	/**
	 * @return {@link IoBuffer}
	 * @see java.nio.Buffer#flip()
	 */
	public abstract IoBuffer flip();

	/**
	 * @return byte
	 * @see ByteBuffer#get()
	 */
	public abstract byte get();

	/**
	 * @param dst byte[]
	 * @param offset int
	 * @param length int
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#get(byte[], int, int)
	 */
	public abstract IoBuffer get(byte[] dst, int offset, int length);

	/**
	 * @param index int
	 * @return byte
	 * @see ByteBuffer#get(int)
	 */
	public abstract byte get(int index);

	/**
	 * @return {@link ByteBuffer}
	 */
	public abstract ByteBuffer getByteBuffer();

	/**
	 * @return char
	 * @see ByteBuffer#getChar()
	 */
	public abstract char getChar();

	/**
	 * @param index int
	 * @return char
	 * @see ByteBuffer#getChar(int)
	 */
	public abstract char getChar(int index);

	/**
	 * @return double
	 * @see ByteBuffer#getDouble()
	 */
	public abstract double getDouble();

	/**
	 * @param index int
	 * @return double
	 * @see ByteBuffer#getDouble(int)
	 */
	public abstract double getDouble(int index);

	/**
	 * Reads a byte from the buffer and returns the correlating enum constant defined by the
	 * specified enum type.
	 * 
	 * @param <E> The enum type to return
	 * @param enumClass The enum's class object
	 * @return {@link Enum}
	 */
	public abstract <E extends Enum<E>> E getEnum(Class<E> enumClass);

	/**
	 * @return float
	 * @see ByteBuffer#getFloat()
	 */
	public abstract float getFloat();

	/**
	 * @param index int
	 * @return float
	 * @see ByteBuffer#getFloat(int)
	 */
	public abstract float getFloat(int index);

	/**
	 * Returns hexdump of this buffer. The data and pointer are not changed as a result of this
	 * method call.
	 * 
	 * @return hexidecimal representation of this buffer
	 */
	public abstract String getHexDump();

	/**
	 * Return hexdump of this buffer with limited length.
	 * 
	 * @param lengthLimit The maximum number of bytes to dump from the current buffer position.
	 * @return hexidecimal representation of this buffer
	 */
	public abstract String getHexDump(int lengthLimit);

	/**
	 * @return int
	 * @see ByteBuffer#getInt()
	 */
	public abstract int getInt();

	/**
	 * @param index int
	 * @return int
	 * @see ByteBuffer#getInt(int)
	 */
	public abstract int getInt(int index);

	/**
	 * @return long
	 * @see ByteBuffer#getLong()
	 */
	public abstract long getLong();

	/**
	 * @param index int
	 * @return long
	 * @see ByteBuffer#getLong(int)
	 */
	public abstract long getLong(int index);

	/**
	 * Relative <i>get</i> method for reading a medium int value.
	 * <p>
	 * Reads the next three bytes at this buffer's current position, composing them into an int
	 * value according to the current byte order, and then increments the position by three.
	 * </p>
	 * 
	 * @return The medium int value at the buffer's current position
	 */
	public abstract int getMediumInt();

	/**
	 * Absolute <i>get</i> method for reading a medium int value.
	 * <p>
	 * Reads the next three bytes at this buffer's current position, composing them into an int
	 * value according to the current byte order.
	 * </p>
	 * 
	 * @param index The index from which the medium int will be read
	 * @return The medium int value at the given index
	 * @throws IndexOutOfBoundsException If <tt>index</tt> is negative or not smaller than the
	 *             buffer's limit
	 */
	public abstract int getMediumInt(int index);

	/**
	 * Reads a Java object from the buffer using the context {@link ClassLoader} of the current
	 * thread.
	 * 
	 * @return Object
	 * @throws ClassNotFoundException Falls was schief geht.
	 */
	public abstract Object getObject() throws ClassNotFoundException;

	/**
	 * Reads a Java object from the buffer using the specified <tt>classLoader</tt>.
	 * 
	 * @param classLoader {@link ClassLoader}
	 * @return Object
	 * @throws ClassNotFoundException Falls was schief geht.
	 */
	public abstract Object getObject(final ClassLoader classLoader) throws ClassNotFoundException;

	/**
	 * @return short
	 * @see ByteBuffer#getShort()
	 */
	public abstract short getShort();

	/**
	 * @param index int
	 * @return short
	 * @see ByteBuffer#getShort()
	 */
	public abstract short getShort(int index);

	/**
	 * Reads a <code>NUL</code>-terminated string from this buffer using the specified
	 * <code>decoder</code> and returns it. This method reads until the limit of this buffer if no
	 * <tt>NUL</tt> is found.
	 * 
	 * @param decoder {@link CharsetDecoder}
	 * @return String
	 * @throws CharacterCodingException Falls was schief geht.
	 */
	public abstract String getString(CharsetDecoder decoder) throws CharacterCodingException;

	/**
	 * Reads one unsigned byte as a short integer.
	 * 
	 * @return short
	 */
	public abstract short getUnsigned();

	/**
	 * Reads one byte as an unsigned short integer.
	 * 
	 * @param index int
	 * @return short
	 */
	public abstract short getUnsigned(int index);

	/**
	 * Reads four bytes unsigned integer.
	 * 
	 * @return long
	 */
	public abstract long getUnsignedInt();

	/**
	 * Reads four bytes unsigned integer.
	 * 
	 * @param index int
	 * @return long
	 */
	public abstract long getUnsignedInt(int index);

	/**
	 * Relative <i>get</i> method for reading an unsigned medium int value.
	 * <p>
	 * Reads the next three bytes at this buffer's current position, composing them into an int
	 * value according to the current byte order, and then increments the position by three.
	 * </p>
	 * 
	 * @return The unsigned medium int value at the buffer's current position
	 */
	public abstract int getUnsignedMediumInt();

	/**
	 * Absolute <i>get</i> method for reading an unsigned medium int value.
	 * <p>
	 * Reads the next three bytes at this buffer's current position, composing them into an int
	 * value according to the current byte order.
	 * </p>
	 * 
	 * @param index The index from which the unsigned medium int will be read
	 * @return The unsigned medium int value at the given index
	 * @throws IndexOutOfBoundsException If <tt>index</tt> is negative or not smaller than the
	 *             buffer's limit
	 */
	public abstract int getUnsignedMediumInt(int index);

	/**
	 * Reads two bytes unsigned integer.
	 * 
	 * @return int
	 */
	public abstract int getUnsignedShort();

	/**
	 * Reads two bytes unsigned integer.
	 * 
	 * @param index int
	 * @return int
	 */
	public abstract int getUnsignedShort(int index);

	/**
	 * @return boolean
	 * @see ByteBuffer#hasArray()
	 */
	public abstract boolean hasArray();

	/**
	 * @return boolean
	 * @see java.nio.Buffer#hasRemaining()
	 */
	public abstract boolean hasRemaining();

	/**
	 * Returns the first occurence position of the specified byte from the current position to the
	 * current limit.
	 * 
	 * @param b byte
	 * @return <tt>-1</tt> if the specified byte is not found
	 */
	public abstract int indexOf(byte b);

	/**
	 * Returns <tt>true</tt> if and only if <tt>autoExpand</tt> is turned on.
	 * 
	 * @return boolean
	 */
	public abstract boolean isAutoExpand();

	/**
	 * Returns <tt>true</tt> if and only if <tt>autoShrink</tt> is turned on.
	 * 
	 * @return boolean
	 */
	public abstract boolean isAutoShrink();

	/**
	 * @return boolean
	 * @see ByteBuffer#isDirect()
	 */
	public abstract boolean isDirect();

	/**
	 * @return int
	 * @see java.nio.Buffer#limit()
	 */
	public abstract int limit();

	/**
	 * @param newLimit int
	 * @return {@link IoBuffer}
	 * @see java.nio.Buffer#limit(int)
	 */
	public abstract IoBuffer limit(int newLimit);

	/**
	 * @return {@link IoBuffer}
	 * @see java.nio.Buffer#mark()
	 */
	public abstract IoBuffer mark();

	/**
	 * @return {@link ByteOrder}
	 * @see ByteBuffer#order()
	 */
	public abstract ByteOrder order();

	/**
	 * @return int
	 * @see java.nio.Buffer#position()
	 */
	public abstract int position();

	/**
	 * @param newPosition int
	 * @return {@link IoBuffer}
	 * @see java.nio.Buffer#position(int)
	 */
	public abstract IoBuffer position(int newPosition);

	/**
	 * Returns <tt>true</tt> if this buffer contains a data which has a data length as a prefix and
	 * the buffer has remaining data as enough as specified in the data length field. This method is
	 * identical with <tt>prefixedDataAvailable( prefixLength, Integer.MAX_VALUE )</tt>. Please not
	 * that using this method can allow DoS (Denial of Service) attack in case the remote peer sends
	 * too big data length value. It is recommended to use {@link #prefixedDataAvailable(int, int)}
	 * instead.
	 * 
	 * @param prefixLength the length of the prefix field (1, 2, or 4)
	 * @return boolean
	 * @throws IllegalArgumentException if prefixLength is wrong
	 * @throws RuntimeException if data length is negative
	 */
	public abstract boolean prefixedDataAvailable(int prefixLength);

	/**
	 * Returns <tt>true</tt> if this buffer contains a data which has a data length as a prefix and
	 * the buffer has remaining data as enough as specified in the data length field.
	 * 
	 * @param prefixLength the length of the prefix field (1, 2, or 4)
	 * @param maxDataLength the allowed maximum of the read data length
	 * @return boolean
	 * @throws IllegalArgumentException if prefixLength is wrong
	 * @throws RuntimeException if data length is negative or greater then <tt>maxDataLength</tt>
	 */
	public abstract boolean prefixedDataAvailable(int prefixLength, int maxDataLength);

	/**
	 * @param b byte
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#put(byte)
	 */
	public abstract IoBuffer put(byte b);

	/**
	 * @param src byte[]
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#put(byte[])
	 */
	public abstract IoBuffer put(byte[] src);

	/**
	 * @param src byte[]
	 * @param offset int
	 * @param length int
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#put(byte[], int, int)
	 */
	public abstract IoBuffer put(byte[] src, int offset, int length);

	/**
	 * Writes the content of the specified <tt>src</tt> into this buffer.
	 * 
	 * @param src {@link ByteBuffer}
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer put(ByteBuffer src);

	/**
	 * Writes the content of the specified <tt>src</tt> into this buffer.
	 * 
	 * @param src {@link IoBuffer}
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer put(IoBuffer src);

	/**
	 * @param value char
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#putChar(char)
	 */
	public abstract IoBuffer putChar(char value);

	/**
	 * @param value double
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#putDouble(double)
	 */
	public abstract IoBuffer putDouble(double value);

	/**
	 * Writes an enum's ordinal value to the buffer as a byte.
	 * 
	 * @param e The enum to write to the buffer
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer putEnum(Enum<?> e);

	/**
	 * @param value float
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#putFloat(float)
	 */
	public abstract IoBuffer putFloat(float value);

	/**
	 * @param value int
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#putInt(int)
	 */
	public abstract IoBuffer putInt(int value);

	/**
	 * @param value long
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#putLong(int, long)
	 */
	public abstract IoBuffer putLong(long value);

	/**
	 * Relative <i>put</i> method for writing a medium int value.
	 * <p>
	 * Writes three bytes containing the given int value, in the current byte order, into this
	 * buffer at the current position, and then increments the position by three.
	 * </p>
	 * 
	 * @param value The medium int value to be written
	 * @return This buffer
	 * @throws BufferOverflowException If there are fewer than three bytes remaining in this buffer
	 */
	public abstract IoBuffer putMediumInt(int value);

	/**
	 * Writes the specified Java object to the buffer.
	 * 
	 * @param o Object
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer putObject(Object o);

	/**
	 * @param value short
	 * @return {@link IoBuffer}
	 * @see ByteBuffer#putShort(short)
	 */
	public abstract IoBuffer putShort(short value);

	/**
	 * Writes the content of <code>in</code> into this buffer using the specified
	 * <code>encoder</code>. This method doesn't terminate string with <tt>NUL</tt>. You have to do
	 * it by yourself.
	 * 
	 * @param val {@link CharSequence}
	 * @param encoder {@link CharsetEncoder}
	 * @return {@link IoBuffer}
	 * @throws CharacterCodingException Falls was schief geht.
	 * @throws BufferOverflowException if the specified string doesn't fit
	 */
	public abstract IoBuffer putString(CharSequence val, CharsetEncoder encoder)
		throws CharacterCodingException;

	/**
	 * @return int
	 * @see java.nio.Buffer#remaining()
	 */
	public abstract int remaining();

	/**
	 * @return {@link IoBuffer}
	 * @see java.nio.Buffer#reset()
	 */
	public abstract IoBuffer reset();

	/**
	 * Turns on or off <tt>autoExpand</tt>.
	 * 
	 * @param autoExpand boolean
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer setAutoExpand(boolean autoExpand);

	/**
	 * Turns on or off <tt>autoShrink</tt>.
	 * 
	 * @param autoShrink boolean
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer setAutoShrink(boolean autoShrink);

	/**
	 * Forwards the position of this buffer as the specified <code>size</code> bytes.
	 * 
	 * @param size int
	 * @return {@link IoBuffer}
	 */
	public abstract IoBuffer skip(int size);
}
