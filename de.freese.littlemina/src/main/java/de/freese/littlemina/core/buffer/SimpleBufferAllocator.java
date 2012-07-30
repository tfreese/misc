// Created: 16.01.2010
/**
 * 16.01.2010
 */
package de.freese.littlemina.core.buffer;

import java.nio.ByteBuffer;

/**
 * A simplistic {@link IoBufferAllocator} which simply allocates a new buffer every time.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SimpleBufferAllocator implements IoBufferAllocator
{
	/**
	 * @author Thomas Freese
	 */
	private class SimpleBuffer extends AbstractIoBuffer
	{
		/**
		 *
		 */
		private ByteBuffer byteBuffer = null;

		/**
		 * Erstellt ein neues {@link SimpleBuffer} Object.
		 * 
		 * @param byteBuffer {@link ByteBuffer}
		 */
		public SimpleBuffer(final ByteBuffer byteBuffer)
		{
			super(byteBuffer.capacity());

			this.byteBuffer = byteBuffer;
		}

		/**
		 * @see de.freese.littlemina.core.buffer.IoBuffer#array()
		 */
		@Override
		public byte[] array()
		{
			return this.byteBuffer.array();
		}

		/**
		 * @see de.freese.littlemina.core.buffer.IoBuffer#arrayOffset()
		 */
		@Override
		public int arrayOffset()
		{
			return this.byteBuffer.arrayOffset();
		}

		/**
		 * @see de.freese.littlemina.core.buffer.IoBuffer#asReadOnlyBuffer()
		 */
		@Override
		public IoBuffer asReadOnlyBuffer()
		{
			return new SimpleBuffer(this.byteBuffer.asReadOnlyBuffer());
		}

		/**
		 * @see de.freese.littlemina.core.buffer.IoBuffer#getByteBuffer()
		 */
		@Override
		public ByteBuffer getByteBuffer()
		{
			return this.byteBuffer;
		}

		/**
		 * @see de.freese.littlemina.core.buffer.IoBuffer#hasArray()
		 */
		@Override
		public boolean hasArray()
		{
			return this.byteBuffer.hasArray();
		}

		/**
		 * @see de.freese.littlemina.core.buffer.AbstractIoBuffer#setByteBuffer(java.nio.ByteBuffer)
		 */
		@Override
		protected void setByteBuffer(final ByteBuffer newByteBuffer)
		{
			this.byteBuffer = newByteBuffer;
		}
	}

	/**
	 * Erstellt ein neues {@link SimpleBufferAllocator} Object.
	 */
	SimpleBufferAllocator()
	{
		super();
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBufferAllocator#allocate(int, boolean)
	 */
	@Override
	public IoBuffer allocate(final int capacity, final boolean direct)
	{
		return wrap(allocateNioBuffer(capacity, direct));
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBufferAllocator#allocateNioBuffer(int, boolean)
	 */
	@Override
	public ByteBuffer allocateNioBuffer(final int capacity, final boolean direct)
	{
		ByteBuffer nioBuffer;

		if (direct)
		{
			nioBuffer = ByteBuffer.allocateDirect(capacity);
		}
		else
		{
			nioBuffer = ByteBuffer.allocate(capacity);
		}

		return nioBuffer;
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBufferAllocator#dispose()
	 */
	@Override
	public void dispose()
	{
		// Ignore
	}

	/**
	 * @see de.freese.littlemina.core.buffer.IoBufferAllocator#wrap(java.nio.ByteBuffer)
	 */
	@Override
	public IoBuffer wrap(final ByteBuffer nioBuffer)
	{
		return new SimpleBuffer(nioBuffer);
	}
}
