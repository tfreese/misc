/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.handler;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;
import de.freese.maven.proxy.repository.VirtualRepository;

/**
 * Handler für Requests an den Maven Proxy.
 * 
 * @author Thomas Freese
 */
public class RequestHandler implements IoHandler
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private final VirtualRepository virtualRepository;

	/**
	 * Erstellt ein neues {@link RequestHandler} Object.
	 * 
	 * @param virtualRepository {@link VirtualRepository}
	 */
	public RequestHandler(final VirtualRepository virtualRepository)
	{
		super();

		this.virtualRepository = virtualRepository;
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
		MavenRequest mavenRequest = (MavenRequest) message;

		// Absender anonymisieren.
		mavenRequest.getHttpHeader().setUserAgentValue("none");

		MavenResponse mavenResponse = null;

		if (mavenRequest.getHttpHeader().getMethod().equals("HEAD"))
		{
			mavenResponse = this.virtualRepository.exist(mavenRequest);
		}
		else
		{
			mavenResponse = this.virtualRepository.getResource(mavenRequest);
		}

		mavenResponse.getHttpHeader().setServerValue("Maven-Proxy");

		session.write(mavenResponse).addListener(IoFutureListener.CLOSE);
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#messageSent(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageSent(final IoSession session, final Object message) throws Exception
	{
		if (getLogger().isDebugEnabled())
		{
			getLogger().debug(message.toString());
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionClosed(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(final IoSession session) throws Exception
	{
		if (getLogger().isDebugEnabled())
		{
			getLogger().debug(session.toString());
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionCreated(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionCreated(final IoSession session) throws Exception
	{
		if (getLogger().isDebugEnabled())
		{
			getLogger().debug(session.toString());
		}
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
			getLogger().info("close session: {}", session.toString());
			session.close(true);
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandler#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session) throws Exception
	{
		if (getLogger().isDebugEnabled())
		{
			getLogger().debug(session.toString());
		}
	}
}
