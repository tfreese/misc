package de.freese.sonstiges.mina.server.http;

import java.util.Date;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class HttpProtocolHandler extends IoHandlerAdapter
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpProtocolHandler.class);

	/**
	 * The end of line character sequence used by most IETF protocols. That is a carriage return
	 * followed by a newline: "\r\n" (NETASCII_EOL)
	 */
	private static final String CRLF = "\r\n";

	/**
	 * Erstellt ein neues {@link HttpProtocolHandler} Object.
	 */
	public HttpProtocolHandler()
	{
		super();
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageReceived(final IoSession iosession, final Object obj) throws Exception
	{
		HttpResponseMessage response = new HttpResponseMessage();
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		response.setContentType("text/html");
		response.appendBody("<html>");
		response.appendBody("<head></head>");
		response.appendBody("<body>");
		response.appendBody("TAESCHDT<br>" + new Date().toString());
		response.appendBody("</body>");
		response.appendBody("</html>");

		// if (response != null)
		{
			iosession.write(response).addListener(IoFutureListener.CLOSE);
			// ((AbstractIoSession) iosession).getProcessor().flush(iosession);
		}
		// String request = obj.toString();
		//
		// System.out.println(request);
		//
		// // Header im HttpProtocolEncoder verschoben
		// StringBuilder builder = new StringBuilder();
		// builder.append("<html>").append(CRLF);
		// builder.append("<head></head>").append(CRLF);
		// builder.append("<body>").append(CRLF);
		// builder.append("TAESCHDT<br>").append(new Date().toString()).append(CRLF);
		// builder.append("</body>").append(CRLF);
		// builder.append("</html>").append(CRLF);
		//
		// // Kein Plan warum das nur mit Listener funzt
		// iosession.write(builder.toString()).addListener(IoFutureListener.CLOSE);
		// // iosession.close(true);
		//
		LOGGER.info("Message written... " + iosession.getId());
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionCreated(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionCreated(final IoSession session) throws Exception
	{
		super.sessionCreated(session);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.core.session.IdleStatus)
	 */
	@Override
	public void sessionIdle(final IoSession iosession, final IdleStatus idlestatus)
		throws Exception
	{
		// Close the connection if reader is idle.
		if (idlestatus == IdleStatus.READER_IDLE)
		{
			iosession.close(true);
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session) throws Exception
	{
		super.sessionOpened(session);
	}
}
