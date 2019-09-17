// Created: 27.03.2018
package de.freese.maven.proxy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.HttpRepository;

/**
 * Basisklasse eines MavenProxies.<br>
 * Maven Konfiguration:
 *
 * <pre>
 * &lt;mirror&gt;
 *  &lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 *  &lt;id&gt;myProxy&lt;/id>&gt;
 *  &lt;name&gt;myProxy&lt;/name&gt;
 *  &lt;url&gt;http://localhost:8080&lt;/url&gt;
 * &lt;/mirror&gt;
 * </pre>
 *
 * @author Thomas Freese
 */
public abstract class AbstractMavenProxy implements MavenProxy
{
    /**
    *
    */
    private final Executor executor;

    /**
    *
    */
    private final FileRepository fileRepository;

    /**
    *
    */
    private final List<HttpRepository> httpRepositories;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private int port = 8080;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMavenProxy}.
     *
     * @param executor {@link Executor}
     * @param fileRepository {@link FileRepository}
     * @param httpRepositories {@link List}
     */
    public AbstractMavenProxy(final Executor executor, final FileRepository fileRepository, final List<HttpRepository> httpRepositories)
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.fileRepository = Objects.requireNonNull(fileRepository, "fileRepository required");
        this.httpRepositories = Objects.requireNonNull(httpRepositories, "httpRepositories required");
    }

    /**
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @return {@link FileRepository}
     */
    protected FileRepository getFileRepository()
    {
        return this.fileRepository;
    }

    /**
     * @return {@link List}<HttpRepository>
     */
    protected List<HttpRepository> getHttpRepositories()
    {
        return this.httpRepositories;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return int
     */
    protected int getPort()
    {
        return this.port;
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#setPort(int)
     */
    @Override
    public void setPort(final int port)
    {
        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0");
        }

        this.port = port;
    }
}
