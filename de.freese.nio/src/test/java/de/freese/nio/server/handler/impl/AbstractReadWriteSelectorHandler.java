// Created: 18.10.2009
/**
 * 18.10.2009
 */
package de.freese.nio.server.handler.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.nio.server.SelectorThread;
import de.freese.nio.server.handler.ReadWriteSelectorHandler;

/**
 * Basisklasse fuer ReadWriteHandler.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractReadWriteSelectorHandler implements ReadWriteSelectorHandler
{
	/**
	 * The end of line character sequence used by most IETF protocols. That is a carriage return
	 * followed by a newline: "\r\n" (NETASCII_EOL)
	 */
	public static final String CRLF = "\r\n";

	/**
	 *
	 */
	private ByteBuffer byteBufferOut = null;

	/**
	 *
	 */
	private final Charset charsetASCII = Charset.forName("US-ASCII");

	/**
	 *
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private final SelectorThread selectorThread;

	/** 
	 * 
	 */
	private final SocketChannel socketChannel;

	/**
	 * Erstellt ein neues {@link AbstractReadWriteSelectorHandler} Object.
	 * 
	 * @param socketChannel {@link SocketChannel}
	 * @param selectorThread {@link SelectorThread}
	 */
	public AbstractReadWriteSelectorHandler(final SocketChannel socketChannel,
			final SelectorThread selectorThread)
	{
		super();

		this.selectorThread = selectorThread;
		this.socketChannel = socketChannel;
	}

	/**
	 * Schreibt den ByteBufferOut in den SocketChannel.
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	protected void doWrite() throws Exception
	{
		getSocketChannel().write(this.byteBufferOut);
	}

	/**
	 * Setzt den ChannelInterest fuer das asynchrone Lesen.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	protected void enableReadLater() throws IOException
	{
		getSelectorThread().registerChannelInterestLater(getSocketChannel(), SelectionKey.OP_READ,
				this);
	}

	/**
	 * Setzt den ChannelInterest fuer das synchrone Lesen.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	protected void enableReadNow() throws IOException
	{
		getSelectorThread().registerChannelInterestNow(getSocketChannel(), SelectionKey.OP_READ,
				this);
	}

	/**
	 * Setzt den ChannelInterest fuer das asynchrone Schreiben.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	protected void enableWriteLater() throws IOException
	{
		getSelectorThread().registerChannelInterestLater(getSocketChannel(), SelectionKey.OP_WRITE,
				this);
	}

	/**
	 * Setzt den ChannelInterest fuer das synchrone Schreiben.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	protected void enableWriteNow() throws IOException
	{
		getSelectorThread().registerChannelInterestNow(getSocketChannel(), SelectionKey.OP_WRITE,
				this);
	}

	/**
	 * @return {@link Charset}
	 */
	protected Charset getCharsetASCII()
	{
		return this.charsetASCII;
	}

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @return {@link SelectorThread}
	 */
	private SelectorThread getSelectorThread()
	{
		return this.selectorThread;
	}

	/**
	 * @return {@link SocketChannel}
	 */
	protected SocketChannel getSocketChannel()
	{
		return this.socketChannel;
	}

	/**
	 * @param byteBufferOut {@link ByteBuffer}
	 */
	protected void setByteBufferOut(final ByteBuffer byteBufferOut)
	{
		this.byteBufferOut = byteBufferOut;
	}
}
