/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.mina;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import de.freese.maven.proxy.AbstractMavenProxy;
import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.mina.codec.MinaMavenProtocolCodecFactory;
import de.freese.maven.proxy.mina.handler.MinaMavenRequestHandler;
import de.freese.maven.proxy.repository.Repository;

/**
 * {@link MavenProxy} mit dem mina-Framework.<br>
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
public class MinaMavenProxy extends AbstractMavenProxy
{
    /**
     *
     */
    private NioSocketAcceptor acceptor = null;

    /**
     * Erstellt ein neues {@link MinaMavenProxy} Object.
     *
     * @param repository {@link Repository}
     * @param executor {@link Executor}
     */
    public MinaMavenProxy(final Repository repository, final Executor executor)
    {
        super(repository, executor);
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#shutdown()
     */
    @Override
    public void shutdown()
    {
        getLogger().info(null);

        if (this.acceptor != null)
        {
            this.acceptor.dispose();
            this.acceptor.unbind();
        }
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#start()
     */
    @Override
    public void start()
    {
        try
        {
            NioProcessor processor = new NioProcessor(getExecutor());

            this.acceptor = new NioSocketAcceptor(getExecutor(), processor);

            if (getLogger().isDebugEnabled())
            {
                this.acceptor.getFilterChain().addLast("logger", new LoggingFilter());
            }

            this.acceptor.getFilterChain().addLast("executor", new ExecutorFilter(getExecutor()));
            this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaMavenProtocolCodecFactory(getCharset())));

            this.acceptor.getSessionConfig().setReadBufferSize(2048);
            // acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

            IoConnector connector = new NioSocketConnector();
            connector.setConnectTimeoutMillis(30 * 1000L);

            this.acceptor.setHandler(new MinaMavenRequestHandler(getRepository()));
            this.acceptor.bind(new InetSocketAddress(getPort()));

            getLogger().info("Listening on port {}", Integer.valueOf(getPort()));
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }
    }
}
