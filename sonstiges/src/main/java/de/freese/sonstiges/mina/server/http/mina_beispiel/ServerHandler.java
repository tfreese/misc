// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.mina.server.http.mina_beispiel;

import java.util.Date;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.mina.server.http.HttpResponseMessage;

/**
 * An {@link IoHandler} for HTTP.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 588178 $, $Date: 2007-10-25 18:28:40 +0900 (?, 25 10? 2007) $
 */
public class ServerHandler extends IoHandlerAdapter
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

	/**
	 * Erstellt ein neues {@link ServerHandler} Object.
	 */
	public ServerHandler()
	{
		super();
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.mina.core.session.IoSession,
	 *      java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(final IoSession session, final Throwable cause)
	{
		LOGGER.warn(null, cause);
		session.close(false);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageReceived(final IoSession session, final Object message)
	{
		// Check that we can service the request context
		HttpResponseMessage response = new HttpResponseMessage();
		// response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		// response.appendBody("CONNECTED");

		response.setContentType("text/html");

		response.appendBody("<html>");
		response.appendBody("<head></head>");
		response.appendBody("<body>");
		response.appendBody("TAESCHDT<br>" + new Date().toString());
		response.appendBody("</body>");
		response.appendBody("</html>");

		// msg.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		// byte[] b = new byte[ta.buffer.limit()];
		// ta.buffer.rewind().get(b);
		// msg.appendBody(b);
		// System.out.println("####################");
		// System.out.println("  GET_TILE RESPONSE SENT - ATTACHMENT GOOD DIAMOND.SI="+d.si+
		// ", "+new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS").format(new
		// java.util.Date()));
		// System.out.println("#################### - status="+ta.state+", index="+message.getIndex());

		// // Unknown request
		// response = new HttpResponseMessage();
		// response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
		// response.appendBody(String.format(
		// "<html><body><h1>UNKNOWN REQUEST %d</h1></body></html>",
		// HttpResponseMessage.HTTP_STATUS_NOT_FOUND));

		session.write(response).addListener(IoFutureListener.CLOSE);

		LOGGER.info("Message written...");
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionIdle(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.core.session.IdleStatus)
	 */
	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status)
	{
		LOGGER.info("Disconnecting the idle.");
		session.close(false);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session)
	{
		// set idle time to 60 seconds
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
	}
}
