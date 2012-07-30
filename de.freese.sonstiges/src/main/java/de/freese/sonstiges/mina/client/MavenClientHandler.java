/**
 * Created: 27.12.2011
 */

package de.freese.sonstiges.mina.client;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class MavenClientHandler implements IoHandler
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link MavenClientHandler} Object.
	 */
	public MavenClientHandler()
	{
		super();

	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#exceptionCaught(org.apache.mina.core.session.IoSession,
	 *      java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(final IoSession session, final Throwable cause) throws Exception
	{
		getLogger().error(null, cause);
	}

	/**
	 * @return {@link Logger}
	 */
	private Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#messageReceived(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageReceived(final IoSession session, final Object message) throws Exception
	{
		getLogger().info(message.toString());
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#messageSent(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageSent(final IoSession session, final Object message) throws Exception
	{
		getLogger().info(message.toString());
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionClosed(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(final IoSession session) throws Exception
	{
		getLogger().info(null);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionCreated(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionCreated(final IoSession session) throws Exception
	{
		getLogger().info(null);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionIdle(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.core.session.IdleStatus)
	 */
	@Override
	public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception
	{
		getLogger().info(null);

		// Close the connection if reader is idle.
		if (status == IdleStatus.READER_IDLE)
		{
			getLogger().info("close session");
			session.close(true);
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session) throws Exception
	{
		getLogger().info(null);
	}
}
