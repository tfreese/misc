// Created: 27.03.2018
package de.freese.maven.proxy.old;

import java.util.Objects;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.old.repository.Repository;

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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private int port = 8080;

    /**
    *
    */
    private final Repository repository;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMavenProxy}.
     *
     * @param repository {@link Repository}
     * @param executor {@link Executor}
     */
    public AbstractMavenProxy(final Repository repository, final Executor executor)
    {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
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
     * @return {@link Repository}
     */
    protected Repository getRepository()
    {
        return this.repository;
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
