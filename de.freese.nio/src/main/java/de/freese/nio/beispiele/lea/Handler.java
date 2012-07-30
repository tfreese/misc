// Created: 13.07.2009
/**
 * 13.07.2009
 */
package de.freese.nio.beispiele.lea;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author Thomas Freese
 */
public class Handler implements Runnable
{
	/**
	 * 
	 */
	private final SocketChannel socketChannel;

	/**
	 * 
	 */
	private final SelectionKey selectionKey;

	/**
	 * 
	 */
	private ByteBuffer input = ByteBuffer.allocate(1024);

	/**
	 * 
	 */
	private ByteBuffer output = ByteBuffer.allocate(1024);

	/**
	 * 
	 */
	static final int READING = 0;

	/**
	 * 
	 */
	static final int SENDING = 1;

	/**
	 * 
	 */
	private int state = READING;

	/**
	 * Erstellt ein neues {@link Handler} Object.
	 * 
	 * @param selector {@link Selector}
	 * @param socketChannel {@link SocketChannel}
	 * @throws IOException Falls was schief geht.
	 */
	public Handler(final Selector selector, final SocketChannel socketChannel) throws IOException
	{
		super();

		this.socketChannel = socketChannel;
		this.socketChannel.configureBlocking(false);
		// Optionally try first read now
		this.selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ, this);
		// this.selectionKey.attach(this);
		// this.selectionKey.interestOps(SelectionKey.OP_READ);
		selector.wakeup();

		System.out.println("Handler.Handler()");
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			if (this.state == READING)
			{
				read();
			}
			else if (this.state == SENDING)
			{
				send();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * @throws IOException Falls was schief geht.
	 */
	void read() throws IOException
	{
		this.socketChannel.read(this.input);

		if (inputIsComplete())
		{
			process();
			this.state = SENDING;
			// Normally also do first write now
			this.selectionKey.interestOps(SelectionKey.OP_WRITE);
		}
	}

	/**
	 * @throws IOException Falls was schief geht.
	 */
	void send() throws IOException
	{
		this.socketChannel.write(this.output);

		if (outputIsComplete())
		{
			this.selectionKey.cancel();
			this.socketChannel.close();
		}
	}

	/**
	 * @return boolean
	 * @throws IOException Falls was schief geht.
	 */
	boolean inputIsComplete() throws IOException
	{
		return this.socketChannel.read(this.input) == 0;
	}

	/**
	 * @return boolean
	 * @throws IOException Falls was schief geht.
	 */
	boolean outputIsComplete() throws IOException
	{
		return true;
	}

	/**
	 * 
	 */
	void process()
	{
		try
		{
			this.input.flip();
			CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
			CharBuffer charBuffer = decoder.decode(this.input);

			System.out.println(charBuffer.toString());

			// antwort bauen
			CharBuffer cb = CharBuffer.allocate(1024);

			for (;;)
			{
				try
				{
					cb.put("HTTP/1.0 ").put(400 + " Bad Request").put("\r\n");
					cb.put("Server: MEINER !").put("\r\n");
					cb.put("Content-type: ").put("text/plain").put("\r\n");
					cb.put("Content-length: ").put(Long.toString(0)).put("\r\n");
					cb.put("\r\n");

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
			this.output = Charset.forName("US-ASCII").encode(cb);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
