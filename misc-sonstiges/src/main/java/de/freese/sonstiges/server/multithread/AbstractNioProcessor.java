// Created: 09.09.2020
package de.freese.sonstiges.server.multithread;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.ServerMain;

/**
 * @author Thomas Freese
 */
public abstract class AbstractNioProcessor implements Runnable
{
    /**
    *
    */
    private boolean isShutdown;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
    *
    */
    private final Selector selector;
    /**
    *
    */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link AbstractNioProcessor} Object.
     *
     * @param selector {@link Selector}
     */
    public AbstractNioProcessor(final Selector selector)
    {
        super();

        this.selector = Objects.requireNonNull(selector, "selector required");
    }

    /**
     * Methode nach einem {@link Selector#select()} Durchlauf.
     */
    protected void afterSelectorLoop()
    {
        // Empty
    }

    /**
     * Methode nach der while-Schleife.
     */
    protected void afterSelectorWhile()
    {
        cancelKeys();
        closeSelector();
    }

    /**
     * Methode vor der while-Schleife.
     *
     * @throws Exception Falls was schief geht.
     */
    protected void beforeSelectorWhile() throws Exception
    {
        // Empty
    }

    /**
    *
    */
    protected void cancelKeys()
    {
        Set<SelectionKey> selected = getSelector().selectedKeys();
        Iterator<SelectionKey> iterator = selected.iterator();

        while (iterator.hasNext())
        {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();

            if (selectionKey == null)
            {
                continue;
            }

            try
            {
                selectionKey.cancel();
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
    }

    /**
     *
     */
    protected void closeSelector()
    {
        if (getSelector().isOpen())
        {
            try
            {
                getSelector().close();
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
    }

    /**
     * @param readyChannels int
     * @return boolean
     */
    protected boolean exitCondition(final int readyChannels)
    {
        return isShutdown() || !getSelector().isOpen();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link Selector}
     */
    protected Selector getSelector()
    {
        return this.selector;
    }

    /**
     * @return {@link Semaphore}
     */
    protected Semaphore getStopLock()
    {
        return this.stopLock;
    }

    /**
     * @return boolean
     */
    protected boolean isShutdown()
    {
        return this.isShutdown;
    }

    /**
     * @param selectionKey {@link SelectionKey}
     */
    protected void onAcceptable(final SelectionKey selectionKey)
    {
        // Empty
    }

    /**
     * @param selectionKey {@link SelectionKey}
     */
    protected void onConnectable(final SelectionKey selectionKey)
    {
        // Empty
    }

    /**
     * @param selectionKey {@link SelectionKey}
     */
    protected void onInValid(final SelectionKey selectionKey)
    {
        // Empty
    }

    /**
     * @param selectionKey {@link SelectionKey}
     */
    protected void onReadable(final SelectionKey selectionKey)
    {
        // Empty
    }

    /**
     * @param selectionKey {@link SelectionKey}
     */
    protected void onWritable(final SelectionKey selectionKey)
    {
        // Empty
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        getStopLock().acquireUninterruptibly();

        try
        {
            beforeSelectorWhile();

            while (!Thread.interrupted())
            {
                int readyChannels = getSelector().select();

                if (exitCondition(readyChannels))
                {
                    break;
                }

                if (readyChannels > 0)
                {
                    Set<SelectionKey> selected = getSelector().selectedKeys();
                    Iterator<SelectionKey> iterator = selected.iterator();

                    while (iterator.hasNext())
                    {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();

                        if (!selectionKey.isValid())
                        {
                            getLogger().info("{}: SelectionKey not valid", ServerMain.getRemoteAddress(selectionKey));

                            onInValid(selectionKey);
                        }
                        else if (selectionKey.isAcceptable())
                        {
                            getLogger().info("New Client Accepted");

                            onAcceptable(selectionKey);
                        }
                        else if (selectionKey.isReadable())
                        {
                            getLogger().info("{}: Read Request", ServerMain.getRemoteAddress(selectionKey));

                            onReadable(selectionKey);
                        }
                        else if (selectionKey.isWritable())
                        {
                            getLogger().info("{}: Write Response", ServerMain.getRemoteAddress(selectionKey));

                            onWritable(selectionKey);
                        }
                        else if (selectionKey.isConnectable())
                        {
                            getLogger().info("{}: Client Connected", ServerMain.getRemoteAddress(selectionKey));

                            onConnectable(selectionKey);
                        }
                    }

                    selected.clear();
                }

                afterSelectorLoop();
            }

            afterSelectorWhile();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
        finally
        {
            getStopLock().release();
        }

        getLogger().info("{} stopped", getClass().getSimpleName().toLowerCase());
    }

    /**
     *
     */
    protected void setShutdow()
    {
        this.isShutdown = true;
    }

    /**
     * Stoppen des Processors.
     */
    void stop()
    {
        getLogger().info("stopping {}", getClass().getSimpleName().toLowerCase());

        setShutdow();
        getSelector().wakeup();

        getStopLock().acquireUninterruptibly();
        getStopLock().release();
    }
}
