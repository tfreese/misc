/**
 * Created: 21.09.2019
 */

package de.freese.maven.proxy.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Siehe Executors.DefaultThreadFactory
 *
 * @author Thomas Freese
 */
public class MavenProxyThreadFactory implements ThreadFactory
{
    // /**
    // *
    // */
    // private static final AtomicInteger poolNumber = new AtomicInteger(1);

    /**
     *
     */
    private final ThreadGroup group;

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
     */
    public MavenProxyThreadFactory()
    {
        super();

        SecurityManager securityManager = System.getSecurityManager();
        this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();

        // this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        this.namePrefix = "worker-";
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread thread = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);

        if (thread.isDaemon())
        {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != Thread.NORM_PRIORITY)
        {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        return thread;
    }
}
