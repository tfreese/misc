// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import com.lmax.disruptor.EventHandler;

/**
 * @author Thomas Freese
 */
public class LongEventHandler implements EventHandler<LongEvent>
{
    /**
     *
     */
    private final int id;

    /**
     *
     */
    public LongEventHandler()
    {
        this(-1);
    }

    /**
     * @param id int
     */
    public LongEventHandler(final int id)
    {
        super();

        this.id = id;
    }

    /**
     * @see EventHandler#onEvent(Object, long, boolean)
     */
    @Override
    public void onEvent(final LongEvent event, final long sequence, final boolean endOfBatch)
    {
        // Load-Balancing auf die Handler über die Sequence.
        if ((this.id == -1) || (this.id == (sequence % Runtime.getRuntime().availableProcessors())))
        {
            handleEvent(event);
        }
    }

    /**
     * @param event {@link LongEvent}
     */
    private void handleEvent(final LongEvent event)
    {
        System.out.printf("%s_LongEventHandler.onEvent: Event = %s%n", Thread.currentThread().getName(), event);
    }
}
