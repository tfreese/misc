package de.freese.sonstiges.server.mina.http;

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
	 * Erstellt ein neues {@link HttpProtocolHandler} Object.
	 */
	public HttpProtocolHandler()
	{
		super();
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.mina.core.session.IoSession,
	 *      java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(final IoSession session, final Throwable cause) throws Exception
	{
		LOGGER.warn(null, cause);
		session.close(false);
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

		iosession.write(response).addListener(IoFutureListener.CLOSE);

		LOGGER.info("Message written... " + iosession.getId());
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
			LOGGER.info("Disconnecting the idle.");

			iosession.close(true);
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session) throws Exception
	{
		// set idle time to 60 seconds
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
	}
}
