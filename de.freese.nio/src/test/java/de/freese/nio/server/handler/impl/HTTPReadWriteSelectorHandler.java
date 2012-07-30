// Created: 18.07.2009
package de.freese.nio.server.handler.impl;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.Date;

import de.freese.nio.server.SelectorThread;
import de.freese.nio.server.ThreadQueue;

/**
 * @author Thomas Freese
 */
public class HTTPReadWriteSelectorHandler extends AbstractReadWriteSelectorHandler
{
	/**
	 * Erstellt ein neues {@link HTTPReadWriteSelectorHandler} Object.
	 * 
	 * @param socketChannel {@link SocketChannel}
	 * @param selectorThread {@link SelectorThread}
	 */
	public HTTPReadWriteSelectorHandler(final SocketChannel socketChannel,
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
		enableReadNow();
	}

	/**
	 * 
	 */
	private void doRead()
	{
		ByteBuffer buf = ByteBuffer.allocate(1024);

		try
		{
			// int nBytes =
			getSocketChannel().read(buf);
			buf.flip();
			CharsetDecoder decoder = getCharsetASCII().newDecoder();
			CharBuffer charBuffer = decoder.decode(buf);

			System.out.println(charBuffer.toString());
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
				cb.put("HTTP/1.0 ").put("200 OK").put(CRLF);
				cb.put("Server: MEINER !").put(CRLF);
				cb.put("Content-type: ").put("text/html").put(CRLF);
				cb.put("Content-length: ").put(Long.toString(0)).put(CRLF);
				cb.put(CRLF);
				cb.put("<html>").put(CRLF);
				cb.put("<head></head>").put(CRLF);
				cb.put("<body>TAESCHDT<br>").put(new Date().toString()).put(CRLF);
				cb.put("</body>").put(CRLF);
				cb.put("</html>").put(CRLF);

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

		try
		{
			enableWriteLater();
		}
		catch (Exception ex)
		{
			getLogger().error(null, ex);
		}
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
				execute();
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
					doWrite();

					getSocketChannel().close();
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
