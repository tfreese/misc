// Created: 18.07.2009
package de.freese.nio.server.handler.impl;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;

import de.freese.nio.server.SelectorThread;
import de.freese.nio.server.ThreadQueue;

/**
 * @author Thomas Freese
 */
public class POP3ReadWriteSelectorHandler extends AbstractReadWriteSelectorHandler
{
	/**
	 * @author Thomas Freese
	 */
	private static enum STATE
	{
		/**
		 *
		 */
		CONNECT,
		/**
		 *
		 */
		USER,
		/**
		 *
		 */
		PASS;
	}

	/**
	 *
	 */
	private String lastRequest = null;

	/**
	 *
	 */
	private STATE state = STATE.CONNECT;

	/**
	 * Erstellt ein neues {@link POP3ReadWriteSelectorHandler} Object.
	 * 
	 * @param socketChannel {@link SocketChannel}
	 * @param selectorThread {@link SelectorThread}
	 */
	public POP3ReadWriteSelectorHandler(final SocketChannel socketChannel,
			final SelectorThread selectorThread)
	{
		super(socketChannel, selectorThread);
	}

	/**
	 * @see de.freese.nio.server.handler.ReadWriteSelectorHandler#configHandle()
	 */
	@Override
	public void configHandle() throws IOException
	{
		enableWriteNow();
	}

	/**
	 * 
	 */
	private void doRead()
	{
		ByteBuffer buf = ByteBuffer.allocate(1024);

		try
		{
			int nBytes = getSocketChannel().read(buf);

			if (nBytes == -1)
			{
				getSocketChannel().close();
				return;
			}

			buf.flip();
			CharsetDecoder decoder = getCharsetASCII().newDecoder();
			CharBuffer charBuffer = decoder.decode(buf);

			this.lastRequest = charBuffer.toString();
			getLogger().info(this.lastRequest);
		}
		catch (IOException ex)
		{
			getLogger().error(null, ex);
		}
	}

	/**
	 * Antwort zusammenbauen
	 */
	private void execute()
	{
		CharBuffer cb = CharBuffer.allocate(1024);

		for (;;)
		{
			try
			{
				if (this.state == STATE.CONNECT)
				{
					cb.put("+OK POP3Connection ready");

					this.state = STATE.USER;
				}
				else if ((this.state == STATE.USER)
						|| ((this.lastRequest != null) && this.lastRequest.startsWith("CAPA")))
				{
					cb.put("+OK");

					this.state = STATE.PASS;

					if ((this.lastRequest != null) && this.lastRequest.startsWith("CAPA"))
					{
						cb.put("USER");
						cb.put(".");
					}
				}
				else if (this.state == STATE.PASS)
				{
					cb.put("+OK send password for ").put(this.lastRequest);
					// cb.put("-ERR unrecognized command");

					this.state = STATE.CONNECT;
				}
				else
				{
					cb.put("-ERR unrecognized command");
				}

				cb.put(CRLF);

				break;
			}
			catch (BufferOverflowException x)
			{
				// assert (cb.capacity() < (1 << 16));
				cb = CharBuffer.allocate(cb.capacity() * 2);

				continue;
			}
		}

		cb.flip();
		setByteBufferOut(getCharsetASCII().encode(cb));
	}

	/**
	 * @see de.freese.nio.server.handler.ReadWriteSelectorHandler#handleRead()
	 */
	@Override
	public void handleRead()
	{
		Runnable runnable = new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				doRead();

				try
				{
					if (getSocketChannel().isOpen())
					{
						enableWriteLater();
					}
				}
				catch (Exception ex)
				{
					getLogger().error(null, ex);
				}
			}
		};

		ThreadQueue.getInstance().execute(runnable);
	}

	/**
	 * @see de.freese.nio.server.handler.ReadWriteSelectorHandler#handleWrite()
	 */
	@Override
	public void handleWrite()
	{
		Runnable runnable = new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				try
				{
					execute();

					doWrite();

					enableReadLater();
				}
				catch (Exception ex)
				{
					getLogger().error(null, ex);
				}
			}
		};

		ThreadQueue.getInstance().execute(runnable);
	}
}
