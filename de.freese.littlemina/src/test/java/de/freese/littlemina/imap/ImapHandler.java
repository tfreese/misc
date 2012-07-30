// Created: 16.01.2010
/**
 * 16.01.2010
 */
package de.freese.littlemina.imap;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.buffer.IoBuffer;
import de.freese.littlemina.core.session.IoSession;

/**
 * {@link IoHandler} fuer das IMAP-Protokoll.
 * 
 * @author Thomas Freese
 */
public class ImapHandler implements IoHandler
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ImapHandler.class);

	/**
	 * The end of line character sequence used by most IETF protocols. That is a carriage return
	 * followed by a newline: "\r\n" (NETASCII_EOL)
	 */
	private static final byte[] CRLF = new byte[]
	{
			0x0D, 0x0A
	};

	/**
	 *
	 */
	private final Charset charset;

	/**
	 *
	 */
	private final CharsetDecoder decoder;

	/**
	 *
	 */
	private final CharsetEncoder encoder;

	/**
	 * Erstellt ein neues {@link ImapHandler} Object.
	 */
	public ImapHandler()
	{
		super();

		// this.charset = Charset.forName("ISO-8859-1");
		this.charset = Charset.forName("US-ASCII");
		this.decoder = this.charset.newDecoder();
		this.encoder = this.charset.newEncoder();
	}

	/**
	 * @see de.freese.littlemina.core.IoHandler#messageReceived(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void messageReceived(final IoSession session) throws Exception
	{
		IoBuffer inputBuffer = session.getBuffer();
		String request = inputBuffer.getString(this.decoder);

		if ((request == null) || (request.length() == 0))
		{
			// Hack fuer das richtige schliessen ohne etwas zu schreiben
			session.write(null);

			session.close();

			return;
		}

		request = request.replace("\r\n", "");
		String[] splits = request.split("[ ]");
		String uid = splits[0];
		String command = "";

		if (splits.length >= 2)
		{
			command = splits[1];
		}

		LOGGER.info(request);

		IoBuffer buffer = IoBuffer.allocate(64);

		// String response = "* BYE Autologout\r\n";

		if (command.startsWith("CAPABILITY"))
		{
			// Meldung des 1und1 Servers
			buffer.putString(
					"* CAPABILITY IMAP4rev1 LITERAL+ ID CHILDREN QUOTA IDLE NAMESPACE UIDPLUS UNSELECT SORT THREAD=ORDEREDSUBJECT ENABLE WITHIN AUTH=LOGIN AUTH=PLAIN",
					this.encoder);
			buffer.put(CRLF);
			buffer.putString(uid, this.encoder);
			buffer.putString(" OK CAPABILITY completed", this.encoder);
			buffer.put(CRLF);
		}
		else
		{
			buffer.putString("* BAD command unknown", this.encoder);
			buffer.put(CRLF);
		}

		buffer.flip();

		session.write(buffer);
	}

	/**
	 * @see de.freese.littlemina.core.IoHandler#sessionOpened(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session) throws Exception
	{
		LOGGER.info(session.toString());

		IoBuffer buffer = IoBuffer.allocate(64);

		String response = "* OK IMAP4rev1 server ready";
		buffer.putString(response, this.encoder);
		buffer.put(CRLF);
		buffer.flip();

		session.write(buffer);
	}

	/**
	 * @see de.freese.littlemina.core.IoHandler#sessionClosed(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(final IoSession session) throws Exception
	{
		LOGGER.info(session.toString());
	}
}
