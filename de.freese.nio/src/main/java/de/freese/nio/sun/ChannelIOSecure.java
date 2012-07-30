package de.freese.nio.sun;

/*
 * @(#)ChannelIOSecure.java 1.4 06/04/11 Copyright (c) 2006 Sun Microsystems, Inc. All Rights
 * Reserved. Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: -Redistribution of source code must
 * retain the above copyright notice, this list of conditions and the following disclaimer.
 * -Redistribution in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither the name of Sun Microsystems, Inc. or the names of contributors may be used
 * to endorse or promote products derived from this software without specific prior written
 * permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR
 * IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO
 * EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this software is
 * not designed, licensed or intended for use in the design, construction, operation or maintenance
 * of any nuclear facility.
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;

/**
 * A helper class which performs I/O using the SSLEngine API.
 * <P>
 * Each connection has a SocketChannel and a SSLEngine that is used through the lifetime of the
 * Channel. We allocate byte buffers for use as the outbound and inbound network buffers.
 * 
 * <PRE>
 *               Application Data
 *               src      requestBB
 *                |           ^
 *                |     |     |
 *                v     |     |
 *           +----+-----|-----+----+
 *           |          |          |
 *           |       SSL|Engine    |
 *   wrap()  |          |          |  unwrap()
 *           | OUTBOUND | INBOUND  |
 *           |          |          |
 *           +----+-----|-----+----+
 *                |     |     ^
 *                |     |     |
 *                v           |
 *            outNetBB     inNetBB
 *                   Net data
 * </PRE>
 * 
 * These buffers handle all of the intermediary data for the SSL connection. To make things easy,
 * we'll require outNetBB be completely flushed before trying to wrap any more data, but we could
 * certainly remove that restriction by using larger buffers.
 * <P>
 * There are many, many ways to handle compute and I/O strategies. What follows is a relatively
 * simple one. The reader is encouraged to develop the strategy that best fits the application.
 * <P>
 * In most of the non-blocking operations in this class, we let the Selector tell us when we're
 * ready to attempt an I/O operation (by the application repeatedly calling our methods). Another
 * option would be to attempt the operation and return from the method when no forward progress can
 * be made.
 * <P>
 * There's lots of room for enhancements and improvement in this example.
 * <P>
 * We're checking for SSL/TLS end-of-stream truncation attacks via sslEngine.closeInbound(). When
 * you reach the end of a input stream via a read() returning -1 or an IOException, we call
 * sslEngine.closeInbound() to signal to the sslEngine that no more input will be available. If the
 * peer's close_notify message has not yet been received, this could indicate a trucation attack, in
 * which an attacker is trying to prematurely close the connection. The closeInbound() will throw an
 * exception if this condition were present.
 * 
 * @author Brad R. Wetmore
 * @author Mark Reinhold
 * @version 1.4, 06/04/11
 */
class ChannelIOSecure extends ChannelIO
{
	/**
	 * An empty ByteBuffer for use when one isn't available, say as a source buffer during initial
	 * handshake wraps or for close operations.
	 */
	private static ByteBuffer hsBB = ByteBuffer.allocate(0);

	/**
	 * Static factory method for creating a secure ChannelIO object.
	 * <P>
	 * We need to allocate different sized application data buffers based on whether we're secure or
	 * not. We can't determine this until our sslEngine is created.
	 * 
	 * @param sc {@link SocketChannel}
	 * @param blocking boolean
	 * @param sslc {@link SSLContext}
	 * @return {@link ChannelIOSecure}
	 * @throws IOException Falls was schief geht.
	 */
	static ChannelIOSecure getInstance(final SocketChannel sc, final boolean blocking,
										final SSLContext sslc) throws IOException
	{
		ChannelIOSecure cio = new ChannelIOSecure(sc, blocking, sslc);

		// Create a buffer using the normal expected application size we'll
		// be getting. This may change, depending on the peer's
		// SSL implementation.
		cio.appBBSize = cio.sslEngine.getSession().getApplicationBufferSize();
		cio.requestBB = ByteBuffer.allocate(cio.appBBSize);

		return cio;
	}

	/**
	 * 
	 */
	private int appBBSize;

	/**
	 * The FileChannel we're currently transferTo'ing (reading).
	 */
	private ByteBuffer fileChannelBB = null;

	/**
	 * 
	 */
	private boolean initialHSComplete;

	/**
	 * During our initial handshake, keep track of the next SSLEngine operation that needs to occur:
	 * NEED_WRAP/NEED_UNWRAP Once the initial handshake has completed, we can short circuit
	 * handshake checks with initialHSComplete.
	 */
	private HandshakeStatus initialHSStatus;

	/**
	 * All I/O goes through these buffers.
	 * <P>
	 * It might be nice to use a cache of ByteBuffers so we're not alloc/dealloc'ing ByteBuffer's
	 * for each new SSLEngine.
	 * <P>
	 * We use our superclass' requestBB for our application input buffer. Outbound application data
	 * is supplied to us by our callers.
	 */
	private ByteBuffer inNetBB;

	/**
	 * 
	 */
	private int netBBSize;

	/**
	 * 
	 */
	private ByteBuffer outNetBB;

	/**
	 * We have received the shutdown request by our caller, and have closed our outbound side.
	 */
	private boolean shutdown = false;

	/**
	 * 
	 */
	private SSLEngine sslEngine = null;

	/**
	 * Constructor for a secure ChannelIO variant.
	 * 
	 * @param sc {@link SocketChannel}
	 * @param blocking boolean
	 * @param sslc {@link SSLContext}
	 * @throws IOException Falls was schief geht.
	 */
	protected ChannelIOSecure(final SocketChannel sc, final boolean blocking, final SSLContext sslc)
		throws IOException
	{
		super(sc, blocking);

		/*
		 * We're a server, so no need to use host/port variant. The first call for a server is a
		 * NEED_UNWRAP.
		 */
		this.sslEngine = sslc.createSSLEngine();
		this.sslEngine.setUseClientMode(false);
		this.initialHSStatus = HandshakeStatus.NEED_UNWRAP;
		this.initialHSComplete = false;

		// Create a buffer using the normal expected packet size we'll
		// be getting. This may change, depending on the peer's
		// SSL implementation.
		this.netBBSize = this.sslEngine.getSession().getPacketBufferSize();
		this.inNetBB = ByteBuffer.allocate(this.netBBSize);
		this.outNetBB = ByteBuffer.allocate(this.netBBSize);
		this.outNetBB.position(0);
		this.outNetBB.limit(0);
	}

	/**
	 * Flush any remaining data.
	 * <P>
	 * Return true when the fileChannelBB and outNetBB are empty.
	 * 
	 * @see de.freese.nio.sun.ChannelIO#dataFlush()
	 */
	@Override
	boolean dataFlush() throws IOException
	{
		boolean fileFlushed = true;

		if ((this.fileChannelBB != null) && this.fileChannelBB.hasRemaining())
		{
			doWrite(this.fileChannelBB);
			fileFlushed = !this.fileChannelBB.hasRemaining();
		}
		else if (this.outNetBB.hasRemaining())
		{
			tryFlush(this.outNetBB);
		}

		return (fileFlushed && !this.outNetBB.hasRemaining());
	}

	/**
	 * Perform any handshaking processing.
	 * <P>
	 * This variant is for Servers without SelectionKeys (e.g. blocking).
	 * 
	 * @see de.freese.nio.sun.ChannelIO#doHandshake()
	 */
	@Override
	boolean doHandshake() throws IOException
	{
		return doHandshake(null);
	}

	/**
	 * Perform any handshaking processing.
	 * <P>
	 * If a SelectionKey is passed, register for selectable operations.
	 * <P>
	 * In the blocking case, our caller will keep calling us until we finish the handshake. Our
	 * reads/writes will block as expected.
	 * <P>
	 * In the non-blocking case, we just received the selection notification that this channel is
	 * ready for whatever the operation is, so give it a try.
	 * <P>
	 * return: true when handshake is done. false while handshake is in progress
	 * 
	 * @see de.freese.nio.sun.ChannelIO#doHandshake(java.nio.channels.SelectionKey)
	 */
	@SuppressWarnings(
	{
			"incomplete-switch", "fallthrough"
	})
	@Override
	boolean doHandshake(final SelectionKey sk) throws IOException
	{
		SSLEngineResult result;

		if (this.initialHSComplete)
		{
			return this.initialHSComplete;
		}

		/*
		 * Flush out the outgoing buffer, if there's anything left in it.
		 */
		if (this.outNetBB.hasRemaining())
		{
			if (!tryFlush(this.outNetBB))
			{
				return false;
			}

			// See if we need to switch from write to read mode.

			switch (this.initialHSStatus)
			{
			/*
			 * Is this the last buffer?
			 */
				case FINISHED:
				{
					this.initialHSComplete = true;
					// Fall-through to reregister need for a Read.
				}

				case NEED_UNWRAP:
				{
					if (sk != null)
					{
						sk.interestOps(SelectionKey.OP_READ);
					}

					break;
				}
			}

			return this.initialHSComplete;
		}

		switch (this.initialHSStatus)
		{
			case NEED_UNWRAP:
			{
				if (this.sc.read(this.inNetBB) == -1)
				{
					this.sslEngine.closeInbound();

					return this.initialHSComplete;
				}

				needIO: while (this.initialHSStatus == HandshakeStatus.NEED_UNWRAP)
				{
					resizeRequestBB(); // expected room for unwrap
					this.inNetBB.flip();
					result = this.sslEngine.unwrap(this.inNetBB, this.requestBB);
					this.inNetBB.compact();

					this.initialHSStatus = result.getHandshakeStatus();

					switch (result.getStatus())
					{
						case OK:
						{
							switch (this.initialHSStatus)
							{
								case NOT_HANDSHAKING:
								{
									throw new IOException(
											"Not handshaking during initial handshake");
								}

								case NEED_TASK:
								{
									this.initialHSStatus = doTasks();

									break;
								}

								case FINISHED:
								{
									this.initialHSComplete = true;

									break needIO;
								}
							}

							break;
						}

						case BUFFER_UNDERFLOW:
						{
							// Resize buffer if needed.
							this.netBBSize = this.sslEngine.getSession().getPacketBufferSize();
							if (this.netBBSize > this.inNetBB.capacity())
							{
								resizeResponseBB();
							}

							/*
							 * Need to go reread the Channel for more data.
							 */
							if (sk != null)
							{
								sk.interestOps(SelectionKey.OP_READ);
							}

							break needIO;
						}

						case BUFFER_OVERFLOW:
						{
							// Reset the application buffer size.
							this.appBBSize = this.sslEngine.getSession().getApplicationBufferSize();

							break;
						}

						default:
						{
							// CLOSED:
							throw new IOException("Received" + result.getStatus()
									+ "during initial handshaking");
						}
					}
				} // "needIO" block.

				/*
				 * Just transitioned from read to write.
				 */
				if (this.initialHSStatus != HandshakeStatus.NEED_WRAP)
				{
					break;
				}
			}

			// Fall through and fill the write buffers.

			case NEED_WRAP:
			{
				/*
				 * The flush above guarantees the out buffer to be empty
				 */
				this.outNetBB.clear();
				result = this.sslEngine.wrap(hsBB, this.outNetBB);
				this.outNetBB.flip();

				this.initialHSStatus = result.getHandshakeStatus();

				switch (result.getStatus())
				{
					case OK:
					{
						if (this.initialHSStatus == HandshakeStatus.NEED_TASK)
						{
							this.initialHSStatus = doTasks();
						}

						if (sk != null)
						{
							sk.interestOps(SelectionKey.OP_WRITE);
						}

						break;
					}

					default:
					{
						// BUFFER_OVERFLOW/BUFFER_UNDERFLOW/CLOSED:
						throw new IOException("Received" + result.getStatus()
								+ "during initial handshaking");
					}
				}

				break;
			}

			default:
			{
				// NOT_HANDSHAKING/NEED_TASK/FINISHED
				throw new RuntimeException("Invalid Handshaking State" + this.initialHSStatus);
			}
		} // switch

		return this.initialHSComplete;
	}

	/**
	 * Do all the outstanding handshake tasks in the current Thread.
	 * 
	 * @return {@link SSLEngineResult}
	 */
	private SSLEngineResult.HandshakeStatus doTasks()
	{
		Runnable runnable;

		/*
		 * We could run this in a separate thread, but do in the current for now.
		 */
		while ((runnable = this.sslEngine.getDelegatedTask()) != null)
		{
			runnable.run();
		}

		return this.sslEngine.getHandshakeStatus();
	}

	/**
	 * Try to flush out any existing outbound data, then try to wrap anything new contained in the
	 * src buffer.
	 * <P>
	 * Return the number of bytes actually consumed from the buffer, but the data may actually be
	 * still sitting in the output buffer, waiting to be flushed.
	 * 
	 * @param src {@link ByteBuffer}
	 * @return int
	 * @throws IOException Falls was schief geht
	 */
	private int doWrite(final ByteBuffer src) throws IOException
	{
		int retValue = 0;

		if (this.outNetBB.hasRemaining() && !tryFlush(this.outNetBB))
		{
			return retValue;
		}

		/*
		 * The data buffer is empty, we can reuse the entire buffer.
		 */
		this.outNetBB.clear();

		SSLEngineResult result = this.sslEngine.wrap(src, this.outNetBB);

		retValue = result.bytesConsumed();

		this.outNetBB.flip();

		switch (result.getStatus())
		{
			case OK:
			{
				if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK)
				{
					doTasks();
				}

				break;
			}

			default:
			{
				throw new IOException("sslEngine error during data write: " + result.getStatus());
			}
		}

		/*
		 * Try to flush the data, regardless of whether or not it's been selected. Odds of a write
		 * buffer being full is less than a read buffer being empty.
		 */
		if (this.outNetBB.hasRemaining())
		{
			tryFlush(this.outNetBB);
		}

		return retValue;
	}

	/**
	 * Read the channel for more information, then unwrap the (hopefully application) data we get.
	 * <P>
	 * If we run out of data, we'll return to our caller (possibly using a Selector) to get
	 * notification that more is available.
	 * <P>
	 * Each call to this method will perform at most one underlying read().
	 * 
	 * @see de.freese.nio.sun.ChannelIO#read()
	 */
	@SuppressWarnings("fallthrough")
	@Override
	int read() throws IOException
	{
		SSLEngineResult result;

		if (!this.initialHSComplete)
		{
			throw new IllegalStateException();
		}

		int pos = this.requestBB.position();

		if (this.sc.read(this.inNetBB) == -1)
		{
			this.sslEngine.closeInbound(); // probably throws exception

			return -1;
		}

		do
		{
			resizeRequestBB(); // expected room for unwrap
			this.inNetBB.flip();
			result = this.sslEngine.unwrap(this.inNetBB, this.requestBB);
			this.inNetBB.compact();

			/*
			 * Could check here for a renegotation, but we're only doing a simple read/write, and
			 * won't have enough state transitions to do a complete handshake, so ignore that
			 * possibility.
			 */
			switch (result.getStatus())
			{
				case BUFFER_OVERFLOW:
				{
					// Reset the application buffer size.
					this.appBBSize = this.sslEngine.getSession().getApplicationBufferSize();

					break;
				}

				case BUFFER_UNDERFLOW:
				{
					// Resize buffer if needed.
					this.netBBSize = this.sslEngine.getSession().getPacketBufferSize();
					if (this.netBBSize > this.inNetBB.capacity())
					{
						resizeResponseBB();

						break; // break, next read will support larger buffer.
					}
				}

				case OK:
				{
					if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK)
					{
						doTasks();
					}

					break;
				}

				default:
				{
					throw new IOException("sslEngine error during data read: " + result.getStatus());
				}
			}
		}
		while ((this.inNetBB.position() != 0) && (result.getStatus() != Status.BUFFER_UNDERFLOW));

		return (this.requestBB.position() - pos);
	}

	/**
	 * Calls up to the superclass to adjust the buffer size by an appropriate increment.
	 */
	protected void resizeRequestBB()
	{
		resizeRequestBB(this.appBBSize);
	}

	/**
	 * Adjust the inbount network buffer to an appropriate size.
	 */
	private void resizeResponseBB()
	{
		ByteBuffer bb = ByteBuffer.allocate(this.netBBSize);

		this.inNetBB.flip();
		bb.put(this.inNetBB);
		this.inNetBB = bb;
	}

	/**
	 * Begin the shutdown process.
	 * <P>
	 * Close out the SSLEngine if not already done so, then wrap our outgoing close_notify message
	 * and try to send it on.
	 * <P>
	 * Return true when we're done passing the shutdown messsages.
	 * 
	 * @see de.freese.nio.sun.ChannelIO#shutdown()
	 */
	@Override
	boolean shutdown() throws IOException
	{
		if (!this.shutdown)
		{
			this.sslEngine.closeOutbound();
			this.shutdown = true;
		}

		if (this.outNetBB.hasRemaining() && tryFlush(this.outNetBB))
		{
			return false;
		}

		/*
		 * By RFC 2616, we can "fire and forget" our close_notify message, so that's what we'll do
		 * here.
		 */
		this.outNetBB.clear();
		SSLEngineResult result = this.sslEngine.wrap(hsBB, this.outNetBB);

		if (result.getStatus() != Status.CLOSED)
		{
			throw new SSLException("Improper close state");
		}

		this.outNetBB.flip();

		/*
		 * We won't wait for a select here, but if this doesn't work, we'll cycle back through on
		 * the next select.
		 */
		if (this.outNetBB.hasRemaining())
		{
			tryFlush(this.outNetBB);
		}

		return (!this.outNetBB.hasRemaining() && (result.getHandshakeStatus() != HandshakeStatus.NEED_WRAP));
	}

	/**
	 * Perform a FileChannel.TransferTo on the socket channel.
	 * <P>
	 * We have to copy the data into an intermediary app ByteBuffer first, then send it through the
	 * SSLEngine.
	 * <P>
	 * We return the number of bytes actually read out of the filechannel. However, the data may
	 * actually be stuck in the fileChannelBB or the outNetBB. The caller is responsible for making
	 * sure to call dataFlush() before shutting down.
	 * 
	 * @see de.freese.nio.sun.ChannelIO#transferTo(java.nio.channels.FileChannel, long, long)
	 */
	@Override
	long transferTo(final FileChannel fc, final long pos, final long len) throws IOException
	{
		if (!this.initialHSComplete)
		{
			throw new IllegalStateException();
		}

		if (this.fileChannelBB == null)
		{
			this.fileChannelBB = ByteBuffer.allocate(this.appBBSize);
			this.fileChannelBB.limit(0);
		}

		this.fileChannelBB.compact();
		int fileRead = fc.read(this.fileChannelBB);

		this.fileChannelBB.flip();

		/*
		 * We ignore the return value here, we return the number of bytes actually consumed from the
		 * the file. We'll flush the output buffer before we start shutting down.
		 */
		doWrite(this.fileChannelBB);

		return fileRead;
	}

	/**
	 * Writes bb to the SocketChannel.
	 * <P>
	 * Returns true when the ByteBuffer has no remaining data.
	 * 
	 * @param bb {@link ByteBuffer}
	 * @return boolean
	 * @throws IOException Falls was schief geht
	 */
	private boolean tryFlush(final ByteBuffer bb) throws IOException
	{
		super.write(bb);

		return !bb.hasRemaining();
	}

	/**
	 * @see de.freese.nio.sun.ChannelIO#write(java.nio.ByteBuffer)
	 */
	@Override
	int write(final ByteBuffer src) throws IOException
	{
		if (!this.initialHSComplete)
		{
			throw new IllegalStateException();
		}

		return doWrite(src);
	}

	/*
	 * close() is not overridden
	 */
}
