/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.codec.MavenProtocolCodecFactory;
import de.freese.maven.proxy.handler.RequestHandler;
import de.freese.maven.proxy.repository.VirtualRepository;

/**
 * Zentrale Klasse für den Maven Proxy.<br>
 * Maven Konfiguration:
 * 
 * <pre>
 * &lt;mirror&gt;
 * 	&lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 * 	&lt;id&gt;myProxy&lt;/id>&gt;
 * 	&lt;name&gt;myProxy&lt;/name&gt;
 * 	&lt;url&gt;http://localhost:8080&lt;/url&gt;
 * &lt;/mirror&gt;
 * </pre>
 * 
 * @author Thomas Freese
 */
public class MavenProxy
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private int port = 8088;

	/**
	 * 
	 */
	private NioSocketAcceptor acceptor = null;

	/**
	 * 
	 */
	private Charset charset = null;

	/**
	 * 
	 */
	private VirtualRepository virtualRepository = null;

	/**
	 * 
	 */
	private Executor executor = null;

	/**
	 * 
	 */
	private boolean createdExecutor = false;

	/**
	 * Erstellt ein neues {@link MavenProxy} Object.
	 */
	public MavenProxy()
	{
		super();
	}

	/**
	 * @return {@link Executor}
	 */
	private Executor getExecutor()
	{
		return this.executor;
	}

	/**
	 * @return {@link Logger}
	 */
	private Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * Zeichensatz für die Codierung.
	 * 
	 * @param charset {@link Charset}
	 */
	public void setCharset(final Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * Setzt den {@link Executor}.<br>
	 * Optional
	 * 
	 * @param executor {@link Executor}
	 */
	public void setExecutor(final Executor executor)
	{
		this.executor = executor;
	}

	/**
	 * Setzt den Port, Default ist 8088.
	 * 
	 * @param port int
	 */
	public void setPort(final int port)
	{
		this.port = port;
	}

	/**
	 * @param virtualRepository {@link VirtualRepository}
	 */
	public void setVirtualRepository(final VirtualRepository virtualRepository)
	{
		this.virtualRepository = virtualRepository;
	}

	/**
	 * Beenden des Proxies.
	 */
	public void shutdown()
	{
		getLogger().info(null);

		if (this.acceptor != null)
		{
			this.acceptor.dispose();
			this.acceptor.unbind();
		}

		if (this.createdExecutor && (this.executor instanceof ExecutorService))
		{
			ExecutorService executorService = (ExecutorService) this.executor;

			while (executorService.isTerminated())
			{
				try
				{
					executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
				}
				catch (InterruptedException ex)
				{
					// Ignore
				}
			}
		}

		System.exit(0);
	}

	/**
	 * Startet den Proxy NACH dem setzen aller notwendigen Parameter.
	 */
	public void start()
	{
		getLogger().info(null);

		this.virtualRepository.init();

		try
		{
			if (getExecutor() == null)
			{
				setExecutor(Executors.newCachedThreadPool());
				this.createdExecutor = true;
			}

			NioProcessor processor = new NioProcessor(getExecutor());

			this.acceptor = new NioSocketAcceptor(getExecutor(), processor);

			if (getLogger().isDebugEnabled())
			{
				this.acceptor.getFilterChain().addLast("logger", new LoggingFilter());
			}

			this.acceptor.getFilterChain().addLast("executor", new ExecutorFilter(getExecutor()));
			this.acceptor.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(new MavenProtocolCodecFactory(this.charset)));

			this.acceptor.getSessionConfig().setReadBufferSize(2048);
			// acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

			IoConnector connector = new NioSocketConnector();
			connector.setConnectTimeoutMillis(30 * 1000L);

			this.acceptor.setHandler(new RequestHandler(this.virtualRepository));
			this.acceptor.bind(new InetSocketAddress(this.port));

			getLogger().info("Listening on port {}", Integer.valueOf(this.port));
		}
		catch (Exception ex)
		{
			getLogger().error(null, ex);
			System.exit(-1);
		}
	}
}
