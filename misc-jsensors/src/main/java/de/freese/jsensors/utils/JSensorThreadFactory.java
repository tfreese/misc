// Created: 10.09.2020
package de.freese.jsensors.utils;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class JSensorThreadFactory implements ThreadFactory
{
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
     * Erstellt ein neues {@link JSensorThreadFactory} Object.
     *
     * @param namePrefix String
     */
    public JSensorThreadFactory(final String namePrefix)
    {
        super();

        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        this.namePrefix = Objects.requireNonNull(namePrefix, "namePrefix required");
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);

        t.setDaemon(true);
        t.setPriority(Thread.NORM_PRIORITY);

        return t;
    }
}
