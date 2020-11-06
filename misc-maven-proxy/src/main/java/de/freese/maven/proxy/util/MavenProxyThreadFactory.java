/**
 * Created: 21.09.2019
 */

package de.freese.maven.proxy.util;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Siehe Executors.DefaultThreadFactory
 *
 * @author Thomas Freese
 */
public class MavenProxyThreadFactory implements ThreadFactory
{
    /**
    *
    */
    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

    /**
     *
     */
    private final String namePrefix;

    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Erstellt ein neues {@link MavenProxyThreadFactory} Object.
     *
     * @param namePrefix String
     */
    public MavenProxyThreadFactory(final String namePrefix)
    {
        super();

        this.namePrefix = Objects.requireNonNull(namePrefix, "namePrefix required");
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread thread = this.defaultThreadFactory.newThread(r);

        thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
        thread.setDaemon(false);

        return thread;
    }
}
