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

/**
 * BasisAdapter fuer den {@link ByteBuffer} mit zusuetzlichen Methoden.
 * 
 * @author Thomas Freese
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * @see IoBufferAllocator
 */
public abstract class AbstractIoBuffer extends IoBuffer
{
	/** A mask for a byte */
	private static final long BYTE_MASK = 0xFFL;

	/**
	 * A flag set to true if the buffer can extend automatically
	 */
	private boolean autoExpand = true;

	/** A flag set to true if the buffer can shrink automatically */
	private boolean autoShrink = true;

	/**
	 * We don't have any access to Buffer.markValue(), so we need to track it down, which will cause
	 * small extra overhead.
	 */
	private int mark = -1;

	/** The minimum number of bytes the IoBuffer can hold */
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asCharBuffer()
	 */
	@Override
	public CharBuffer asCharBuffer()
	{
		return getByteBuffer().asCharBuffer();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asDoubleBuffer()
	 */
	@Override
	public DoubleBuffer asDoubleBuffer()
	{
		return getByteBuffer().asDoubleBuffer();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asFloatBuffer()
	 */
	@Override
	public FloatBuffer asFloatBuffer()
	{
		return getByteBuffer().asFloatBuffer();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asInputStream()
	 */
	@Override
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asIntBuffer()
	 */
	@Override
	public IntBuffer asIntBuffer()
	{
		return getByteBuffer().asIntBuffer();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asLongBuffer()
	 */
	@Override
	public LongBuffer asLongBuffer()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asOutputStream()
	 */
	@Override
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#asShortBuffer()
	 */
	@Override
	public ShortBuffer asShortBuffer()
	{
		return getByteBuffer().asShortBuffer();
	}

	/**
	 * This method forwards the call to {@link #expand(int)} only when <tt>autoExpand</tt> property
	 * is <tt>true</tt>.
	 * 
	 * @param expectedRemaining int
	 * @return {@link IoBuffer}
	 */
	private IoBuffer autoExpand(final int expectedRemaining)
	{
		if (isAutoExpand())
		{
			expand(expectedRemaining, true);
		}

		return this;
	}

	/**
	 * This method forwards the call to {@link #expand(int)} only when <tt>autoExpand</tt> property
	 * is <tt>true</tt>.
	 * 
	 * @param pos int
	 * @param expectedRemaining int
	 * @return {@link IoBuffer}
	 */
	private IoBuffer autoExpand(final int pos, final int expectedRemaining)
	{
		if (isAutoExpand())
		{
			expand(pos, expectedRemaining, true);
		}

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#capacity()
	 */
	@Override
	public int capacity()
	{
		return getByteBuffer().capacity();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#capacity(int)
	 */
	@Override
	public IoBuffer capacity(final int newCapacity)
	{
		// Allocate a new buffer and transfer all settings to it.
		if (newCapacity > capacity())
		{
			// Expand:
			// // Save the state.
			int pos = position();
			int limit = limit();
			ByteOrder bo = order();

			// // Reallocate.
			ByteBuffer oldBuf = getByteBuffer();
			ByteBuffer newBuf = getAllocator().allocateNioBuffer(newCapacity, isDirect());
			oldBuf.clear();
			newBuf.put(oldBuf);
			setByteBuffer(newBuf);

			// // Restore the state.
			getByteBuffer().limit(limit);

			if (this.mark >= 0)
			{
				getByteBuffer().position(this.mark);
				getByteBuffer().mark();
			}

			getByteBuffer().position(pos);
			getByteBuffer().order(bo);
		}

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#clear()
	 */
	@Override
	public IoBuffer clear()
	{
		getByteBuffer().clear();
		this.mark = -1;

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#compact()
	 */
	@Override
	public IoBuffer compact()
	{
		int remaining = remaining();
		int capacity = capacity();

		if (capacity == 0)
		{
			return this;
		}

		if (isAutoShrink() && (remaining <= capacity >>> 2) && (capacity > this.minimumCapacity))
		{
			int newCapacity = capacity;
			int minCapacity = Math.max(this.minimumCapacity, remaining << 1);

			for (;;)
			{
				if (newCapacity >>> 1 < minCapacity)
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
				throw new IllegalStateException(
						"The amount of the remaining bytes is greater than " + "the new capacity.");
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
	 * @param e {@link Enum}
	 * @param type String
	 * @return String
	 */
	private String enumConversionErrorMessage(final Enum<?> e, final String type)
	{
		return String.format("%s.%s has an ordinal value too large for a %s", e.getClass()
				.getName(), e.name(), type);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#expand(int)
	 */
	@Override
	protected IoBuffer expand(final int expectedRemaining)
	{
		return expand(position(), expectedRemaining, false);
	}

	/**
	 * @param expectedRemaining int
	 * @param autoExpand boolean
	 * @return {@link IoBuffer}
	 */
	private IoBuffer expand(final int expectedRemaining, final boolean autoExpand)
	{
		return expand(position(), expectedRemaining, autoExpand);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#expand(int, int)
	 */
	@Override
	protected IoBuffer expand(final int position, final int expectedRemaining)
	{
		return expand(position, expectedRemaining, false);
	}

	/**
	 * @param pos int
	 * @param expectedRemaining int
	 * @param autoExpand boolean
	 * @return {@link IoBuffer}
	 */
	private IoBuffer expand(final int pos, final int expectedRemaining, final boolean autoExpand)
	{
		int end = pos + expectedRemaining;
		int newCapacity;

		if (autoExpand)
		{
			newCapacity = IoBuffer.normalizeCapacity(end);
		}
		else
		{
			newCapacity = end;
		}

		if (newCapacity > capacity())
		{
			// The buffer needs expansion.
			capacity(newCapacity);
		}

		if (end > limit())
		{
			// We call limit() directly to prevent StackOverflowError
			getByteBuffer().limit(end);
		}

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#flip()
	 */
	@Override
	public IoBuffer flip()
	{
		getByteBuffer().flip();
		this.mark = -1;

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#get()
	 */
	@Override
	public byte get()
	{
		return getByteBuffer().get();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#get(byte[], int, int)
	 */
	@Override
	public IoBuffer get(final byte[] dst, final int offset, final int length)
	{
		getByteBuffer().get(dst, offset, length);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#get(int)
	 */
	@Override
	public byte get(final int index)
	{
		return getByteBuffer().get(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getChar()
	 */
	@Override
	public char getChar()
	{
		return getByteBuffer().getChar();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getChar(int)
	 */
	@Override
	public char getChar(final int index)
	{
		return getByteBuffer().getChar(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getDouble()
	 */
	@Override
	public double getDouble()
	{
		return getByteBuffer().getDouble();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getDouble(int)
	 */
	@Override
	public double getDouble(final int index)
	{
		return getByteBuffer().getDouble(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getEnum(java.lang.Class)
	 */
	@Override
	public <E extends Enum<E>> E getEnum(final Class<E> enumClass)
	{
		return toEnum(enumClass, getUnsigned());
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getFloat()
	 */
	@Override
	public float getFloat()
	{
		return getByteBuffer().getFloat();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getFloat(int)
	 */
	@Override
	public float getFloat(final int index)
	{
		return getByteBuffer().getFloat(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getHexDump()
	 */
	@Override
	public String getHexDump()
	{
		return getHexDump(Integer.MAX_VALUE);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getHexDump(int)
	 */
	@Override
	public String getHexDump(final int lengthLimit)
	{
		return IoBufferHexDumper.getHexdump(this, lengthLimit);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getInt()
	 */
	@Override
	public int getInt()
	{
		return getByteBuffer().getInt();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getInt(int)
	 */
	@Override
	public int getInt(final int index)
	{
		return getByteBuffer().getInt(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getLong()
	 */
	@Override
	public long getLong()
	{
		return getByteBuffer().getLong();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getLong(int)
	 */
	@Override
	public long getLong(final int index)
	{
		return getByteBuffer().getLong(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getMediumInt()
	 */
	@Override
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
	 * @param b1 byte
	 * @param b2 byte
	 * @param b3 byte
	 * @return int
	 */
	private int getMediumInt(final byte b1, final byte b2, final byte b3)
	{
		int ret = b1 << 16 & 0xff0000 | b2 << 8 & 0xff00 | b3 & 0xff;

		// Check to see if the medium int is negative (high bit in b1 set)
		if ((b1 & 0x80) == 0x80)
		{
			// Make the the whole int negative
			ret |= 0xff000000;
		}

		return ret;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getMediumInt(int)
	 */
	@Override
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getObject()
	 */
	@Override
	public Object getObject() throws ClassNotFoundException
	{
		return getObject(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getObject(java.lang.ClassLoader)
	 */
	@Override
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
				@Override
				protected ObjectStreamClass readClassDescriptor()
					throws IOException, ClassNotFoundException
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
							throw new StreamCorruptedException("Unexpected class descriptor type: "
									+ type);
					}
				}

				@Override
				protected Class<?> resolveClass(final ObjectStreamClass desc)
					throws IOException, ClassNotFoundException
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
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			limit(oldLimit);
		}
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getShort()
	 */
	@Override
	public short getShort()
	{
		return getByteBuffer().getShort();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getShort(int)
	 */
	@Override
	public short getShort(final int index)
	{
		return getByteBuffer().getShort(index);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getString(java.nio.charset.CharsetDecoder)
	 */
	@Override
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
		int newPos;

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
				newPos = end = oldPos + (oldLimit - oldPos & 0xFFFFFFFE);
			}
			else
			{
				if (end + 2 <= oldLimit)
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsigned()
	 */
	@Override
	public short getUnsigned()
	{
		return (short) (get() & 0xff);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsigned(int)
	 */
	@Override
	public short getUnsigned(final int index)
	{
		return (short) (get(index) & 0xff);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsignedInt()
	 */
	@Override
	public long getUnsignedInt()
	{
		return getInt() & 0xffffffffL;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsignedInt(int)
	 */
	@Override
	public long getUnsignedInt(final int index)
	{
		return getInt(index) & 0xffffffffL;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsignedMediumInt()
	 */
	@Override
	public int getUnsignedMediumInt()
	{
		int b1 = getUnsigned();
		int b2 = getUnsigned();
		int b3 = getUnsigned();

		if (ByteOrder.BIG_ENDIAN.equals(order()))
		{
			return b1 << 16 | b2 << 8 | b3;
		}

		return b3 << 16 | b2 << 8 | b1;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsignedMediumInt(int)
	 */
	@Override
	public int getUnsignedMediumInt(final int index)
	{
		int b1 = getUnsigned(index);
		int b2 = getUnsigned(index + 1);
		int b3 = getUnsigned(index + 2);

		if (ByteOrder.BIG_ENDIAN.equals(order()))
		{
			return b1 << 16 | b2 << 8 | b3;
		}

		return b3 << 16 | b2 << 8 | b1;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsignedShort()
	 */
	@Override
	public int getUnsignedShort()
	{
		return getShort() & 0xffff;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#getUnsignedShort(int)
	 */
	@Override
	public int getUnsignedShort(final int index)
	{
		return getShort(index) & 0xffff;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#hasRemaining()
	 */
	@Override
	public boolean hasRemaining()
	{
		return limit() > position();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#indexOf(byte)
	 */
	@Override
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#isAutoExpand()
	 */
	@Override
	public boolean isAutoExpand()
	{
		return this.autoExpand;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#isAutoShrink()
	 */
	@Override
	public boolean isAutoShrink()
	{
		return this.autoShrink;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#isDirect()
	 */
	@Override
	public boolean isDirect()
	{
		return getByteBuffer().isDirect();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#limit()
	 */
	@Override
	public int limit()
	{
		return getByteBuffer().limit();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#limit(int)
	 */
	@Override
	public IoBuffer limit(final int newLimit)
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#mark()
	 */
	@Override
	public IoBuffer mark()
	{
		getByteBuffer().mark();
		this.mark = position();

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#order()
	 */
	@Override
	public ByteOrder order()
	{
		return getByteBuffer().order();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#position()
	 */
	@Override
	public int position()
	{
		return getByteBuffer().position();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#position(int)
	 */
	@Override
	public IoBuffer position(final int newPosition)
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#prefixedDataAvailable(int)
	 */
	@Override
	public boolean prefixedDataAvailable(final int prefixLength)
	{
		return prefixedDataAvailable(prefixLength, Integer.MAX_VALUE);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#prefixedDataAvailable(int, int)
	 */
	@Override
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

		return remaining() - prefixLength >= dataLength;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#put(byte)
	 */
	@Override
	public IoBuffer put(final byte b)
	{
		autoExpand(1);
		getByteBuffer().put(b);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#put(byte[])
	 */
	@Override
	public IoBuffer put(final byte[] src)
	{
		return put(src, 0, src.length);
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#put(byte[], int, int)
	 */
	@Override
	public IoBuffer put(final byte[] src, final int offset, final int length)
	{
		autoExpand(length);
		getByteBuffer().put(src, offset, length);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#put(java.nio.ByteBuffer)
	 */
	@Override
	public IoBuffer put(final ByteBuffer src)
	{
		autoExpand(src.remaining());
		getByteBuffer().put(src);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#put(de.freese.littlemina.core.buffer.IoBuffer)
	 */
	@Override
	public IoBuffer put(final IoBuffer src)
	{
		return put(src.getByteBuffer());
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putChar(char)
	 */
	@Override
	public IoBuffer putChar(final char value)
	{
		autoExpand(2);
		getByteBuffer().putChar(value);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putDouble(double)
	 */
	@Override
	public IoBuffer putDouble(final double value)
	{
		autoExpand(8);
		getByteBuffer().putDouble(value);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putEnum(java.lang.Enum)
	 */
	@Override
	public IoBuffer putEnum(final Enum<?> e)
	{
		if (e.ordinal() > BYTE_MASK)
		{
			throw new IllegalArgumentException(enumConversionErrorMessage(e, "byte"));
		}
		return put((byte) e.ordinal());
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putFloat(float)
	 */
	@Override
	public IoBuffer putFloat(final float value)
	{
		autoExpand(4);
		getByteBuffer().putFloat(value);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putInt(int)
	 */
	@Override
	public IoBuffer putInt(final int value)
	{
		autoExpand(4);
		getByteBuffer().putInt(value);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putLong(long)
	 */
	@Override
	public IoBuffer putLong(final long value)
	{
		autoExpand(8);
		getByteBuffer().putLong(value);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putMediumInt(int)
	 */
	@Override
	public IoBuffer putMediumInt(final int value)
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putObject(java.lang.Object)
	 */
	@Override
	public IoBuffer putObject(final Object o)
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
				protected void writeClassDescriptor(final ObjectStreamClass desc)
					throws IOException
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putShort(short)
	 */
	@Override
	public IoBuffer putShort(final short value)
	{
		autoExpand(2);
		getByteBuffer().putShort(value);

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#putString(java.lang.CharSequence,
	 *      java.nio.charset.CharsetEncoder)
	 */
	@Override
	public IoBuffer putString(final CharSequence val, final CharsetEncoder encoder)
		throws CharacterCodingException
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
							autoExpand((int) Math.ceil(in.remaining()
									* encoder.averageBytesPerChar()));
							expandedState++;
							break;
						case 1:
							autoExpand((int) Math.ceil(in.remaining() * encoder.maxBytesPerChar()));
							expandedState++;
							break;
						default:
							throw new RuntimeException("Expanded by "
									+ (int) Math.ceil(in.remaining() * encoder.maxBytesPerChar())
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
	 * @see de.freese.littlemina.core.buffer.IoBuffer#remaining()
	 */
	@Override
	public int remaining()
	{
		return limit() - position();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#reset()
	 */
	@Override
	public IoBuffer reset()
	{
		getByteBuffer().reset();

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#setAutoExpand(boolean)
	 */
	@Override
	public IoBuffer setAutoExpand(final boolean autoExpand)
	{
		this.autoExpand = autoExpand;

		return this;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#setAutoShrink(boolean)
	 */
	@Override
	public IoBuffer setAutoShrink(final boolean autoShrink)
	{
		this.autoShrink = autoShrink;

		return this;
	}

	/**
	 * Sets the underlying NIO buffer instance.
	 * 
	 * @param newByteBuffer {@link ByteBuffer}
	 */
	protected abstract void setByteBuffer(ByteBuffer newByteBuffer);

	/**
	 * @see de.freese.littlemina.core.buffer.IoBuffer#skip(int)
	 */
	@Override
	public IoBuffer skip(final int size)
	{
		autoExpand(size);

		return position(position() + size);
	}

	/**
	 * @param <E> Konkreter Typ des Enums
	 * @param enumClass Class
	 * @param i int
	 * @return Konkreter Typ des Enums
	 */
	private <E> E toEnum(final Class<E> enumClass, final int i)
	{
		E[] enumConstants = enumClass.getEnumConstants();
		if (i > enumConstants.length)
		{
			throw new IndexOutOfBoundsException(String.format(
					"%d is too large of an ordinal to convert to the enum %s", i,
					enumClass.getName()));
		}
		return enumConstants[i];
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
}
