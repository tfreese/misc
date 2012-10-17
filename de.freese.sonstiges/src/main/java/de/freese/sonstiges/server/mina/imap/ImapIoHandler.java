package de.freese.sonstiges.server.mina.imap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class ImapIoHandler extends IoHandlerAdapter
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ImapIoHandler.class);

	/**
	 * Erstellt ein neues {@link ImapIoHandler} Object.
	 */
	public ImapIoHandler()
	{
		super();
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession iosession) throws Exception
	{
		String response = "* OK IMAP4rev1 server ready\r\n";
		iosession.write(response);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageReceived(final IoSession iosession, final Object obj) throws Exception
	{
		String request = (String) obj;
		String[] splits = request.split("[ ]");
		String uid = splits[0];
		String command = splits[1];

		LOGGER.info(request);

		String response = "* BAD command unknown\r\n";
		// String response = "* BYE Autologout\r\n";

		if (command.startsWith("CAPABILITY"))
		{
			// Meldung des 1und1 Servers
			response =
					"* CAPABILITY IMAP4rev1 LITERAL+ ID CHILDREN QUOTA IDLE NAMESPACE UIDPLUS UNSELECT SORT THREAD=ORDEREDSUBJECT ENABLE WITHIN AUTH=LOGIN AUTH=PLAIN\r\n";

			response += uid + " OK CAPABILITY completed\r\n";
		}

		iosession.write(response);

		// * CAPABILITY IMAP4rev1 AUTH=KERBEROS_V4 XPIG-LATIN
		// S: a441 OK CAPABILITY completed
	}
}
