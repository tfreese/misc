/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.blobstore.BlobStore;
import de.freese.maven.proxy.blobstore.file.FileBlobStore;
import de.freese.maven.proxy.netty.initializer.NettyMavenInitializer;
import de.freese.maven.proxy.repository.RemoteRepositories;
import de.freese.maven.proxy.repository.RemoteRepository;
import de.freese.maven.proxy.repository.http.JreHttpClientRepository;
import de.freese.maven.proxy.util.ProxyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Startet den Maven Proxy.<br>
 * Maven Konfiguration:
 *
 * <pre>
 * &lt;mirror&gt;
 *   &lt;id&gt;myProxy&lt;/id>&gt;
 *   &lt;name&gt;myProxy&lt;/name&gt;
 *   &lt;url&gt;http://localhost:8085&lt;/url&gt;
 *   &lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 * &lt;/mirror&gt;
 * </pre>
 *
 * @author Thomas Freese
 */
public class MavenProxyApplication
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyApplication.class);

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // enableProxy();
        // Charset charset = Charset.forName("ISO-8859-1");
        // Charset charset = StandardCharsets.ISO_8859_1;

        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(9, Runtime.getRuntime().availableProcessors() * 2));

        // @formatter:off
        HttpClient.Builder builder = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NEVER)
                .proxy(ProxySelector.getDefault())
                .connectTimeout(Duration.ofSeconds(5))
                .executor(executorService)
                ;
        // @formatter:on
        // .authenticator(Authenticator.getDefault())
        // .cookieHandler(CookieHandler.getDefault())
        // .sslContext(SSLContext.getDefault())
        // .sslParameters(new SSLParameters())

        HttpClient httpClient = builder.build();

        RemoteRepositories remoteRepositories = new RemoteRepositories();
        remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("https://repo1.maven.org/maven2"), httpClient));
        remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("https://repo.spring.io/snapshot"), httpClient));
        remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("https://repo.spring.io/milestone"), httpClient));
        remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("https://repo.spring.io/libs-milestone"), httpClient));
        remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("https://repository.primefaces.org"), httpClient));
        remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("https://oss.sonatype.org/content/repositories/releases"), httpClient));

        // remoteRepositories.addRepository(new JreHttpClientRepository(URI.create("http://repository.jboss.org/nexus/content/groups/public-jboss"),
        // httpClient));
        // remoteRepositories
        // .addRepository(new JreHttpClientRepository(URI.create("https://repository.jboss.org/nexus/content/repositories/releases"), httpClient));

        final MavenProxyApplication proxy =
                new MavenProxyApplication(executorService, new FileBlobStore(Paths.get("/mnt/sonstiges/maven-proxy")), remoteRepositories);
        proxy.setPort(8085);

        new Thread(proxy::start, "Maven-Proxy").start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            proxy.shutdown();

            ProxyUtils.shutdown(executorService, LOGGER);
        }, "Shutdown"));

        // Nur in Eclipse nutzen, Enter = Shutdown.
        try
        {
            System.in.read();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    /**
    *
    */
    private EventLoopGroup acceptorGroup = null;

    /**
    *
    */
    private final BlobStore blobStore;

    /**
    *
    */
    private final Executor executor;

    /**
    *
    */
    private int port = 8080;

    /**
    *
    */
    private final RemoteRepository remoteRepository;

    /**
    *
    */
    private EventLoopGroup workerGroup = null;

    /**
     * Erstellt ein neues {@link MavenProxyApplication} Object.
     *
     * @param executor {@link Executor}
     * @param blobStore {@link BlobStore}
     * @param remoteRepository {@link RemoteRepository}
     */
    public MavenProxyApplication(final Executor executor, final BlobStore blobStore, final RemoteRepository remoteRepository)
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.blobStore = Objects.requireNonNull(blobStore, "blobStore required");
        this.remoteRepository = Objects.requireNonNull(remoteRepository, "repository required");
    }

    /**
     * @param port int
     */
    public void setPort(final int port)
    {
        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0");
        }

        this.port = port;
    }

    /**
     *
     */
    public void shutdown()
    {
        LOGGER.info("shutdown");

        if (this.acceptorGroup != null)
        {
            this.acceptorGroup.shutdownGracefully();
        }

        if (this.workerGroup != null)
        {
            this.workerGroup.shutdownGracefully();
        }
    }

    /**
     *
     */
    public void start()
    {
        LOGGER.info("start");

        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();

            this.acceptorGroup = new NioEventLoopGroup(2, this.executor);
            this.workerGroup = new NioEventLoopGroup(6, this.executor);

            // @formatter:off
            bootstrap.group(this.acceptorGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new NettyMavenInitializer(this.blobStore, this.remoteRepository));
            // @formatter:on

            ChannelFuture ch = bootstrap.bind(this.port);

            ch.channel().closeFuture().sync();
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }
        // finally
        // {
        // shutdown()
        // }
    }
}
