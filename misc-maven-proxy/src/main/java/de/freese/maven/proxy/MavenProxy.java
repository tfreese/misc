/**
 * Created: 21.09.2019
 */

package de.freese.maven.proxy;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.blobstore.BlobStore;
import de.freese.maven.proxy.blobstore.file.FileBlobStore;
import de.freese.maven.proxy.netty.initializer.NettyMavenInitializer;
import de.freese.maven.proxy.repository.RemoteRepositories;
import de.freese.maven.proxy.repository.http.JreHttpClientRepository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Thomas Freese
 */
public class MavenProxy
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxy.class);

    /**
    *
    */
    private EventLoopGroup acceptorGroup = null;

    /**
    *
    */
    private ExecutorService executorService = null;

    /**
     *
     */
    private final List<URI> httpRepositories = new ArrayList<>();

    /**
    *
    */
    private int port = -1;

    /**
    *
    */
    private EventLoopGroup workerGroup = null;

    /**
     *
     */
    private Path workingDirectory = null;

    /**
     * Erstellt ein neues {@link MavenProxy} Object.
     */
    public MavenProxy()
    {
        super();
    }

    /**
     * @param uri {@link URI}
     */
    public void addHttpRepository(final URI uri)
    {
        Objects.requireNonNull(uri, "uri required");

        this.httpRepositories.add(uri);
    }

    /**
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        if (this.executorService == null)
        {
            this.executorService = Executors.newCachedThreadPool();
        }

        return this.executorService;
    }

    /**
     * @return int
     */
    protected int getPort()
    {
        if (this.port <= 0)
        {
            this.port = 7999;
        }

        return this.port;
    }

    /**
     * @return {@link Path}
     */
    protected Path getWorkingDirectory()
    {
        return this.workingDirectory;
    }

    /**
     * @param executorService {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = executorService;
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
     * @param workingDirectory {@link Path}
     */
    public void setWorkingDirectory(final Path workingDirectory)
    {
        Objects.requireNonNull(workingDirectory, "workingDirectory required");

        this.workingDirectory = workingDirectory;
    }

    /**
     *
     */
    public void start()
    {
        LOGGER.info("starting MavenProxy at Port {}", getPort());

        try
        {
            // @formatter:off
            HttpClient.Builder builder = HttpClient.newBuilder()
                    .version(Version.HTTP_2)
                    .followRedirects(Redirect.NEVER)
                    .proxy(ProxySelector.getDefault())
                    .connectTimeout(Duration.ofSeconds(5))
                    .executor(getExecutorService())
                    ;
            // @formatter:on
            // .authenticator(Authenticator.getDefault())
            // .cookieHandler(CookieHandler.getDefault())
            // .sslContext(SSLContext.getDefault())
            // .sslParameters(new SSLParameters())

            HttpClient httpClient = builder.build();

            RemoteRepositories remoteRepositories = new RemoteRepositories();

            for (URI uri : this.httpRepositories)
            {
                remoteRepositories.addRepository(new JreHttpClientRepository(uri, httpClient));
            }

            final Path pathFileRepository = getWorkingDirectory().resolve("repository");

            if (!Files.exists(pathFileRepository))
            {
                Files.createDirectories(pathFileRepository);
            }

            BlobStore blobStore = new FileBlobStore(pathFileRepository);

            ServerBootstrap bootstrap = new ServerBootstrap();

            this.acceptorGroup = new NioEventLoopGroup(2, getExecutorService());
            this.workerGroup = new NioEventLoopGroup(6, getExecutorService());

            // @formatter:off
            bootstrap.group(this.acceptorGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NettyMavenInitializer(blobStore, remoteRepositories));
            // @formatter:on

            ChannelFuture ch = bootstrap.bind(getPort());

            ch.channel().closeFuture().sync();
        }
        catch (Exception ex)
        {
            LOGGER.error(null, ex);

            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }
        // finally
        // {
        // stop()
        // }
    }

    /**
     *
     */
    public void stop()
    {
        LOGGER.info("stopping MavenProxy");

        if (this.acceptorGroup != null)
        {
            this.acceptorGroup.shutdownGracefully();
        }

        if (this.workerGroup != null)
        {
            this.workerGroup.shutdownGracefully();
        }
    }
}
