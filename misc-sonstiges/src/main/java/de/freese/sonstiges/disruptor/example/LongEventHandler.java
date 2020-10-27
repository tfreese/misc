// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lmax.disruptor.EventHandler;

/**
 * @author Thomas Freese
 */
public class LongEventHandler implements EventHandler<LongEvent>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(LongEventHandler.class);

    /**
     *
     */
    private final int id;

    /**
     * @param id int
     */
    public LongEventHandler(final int id)
    {
        super();

        this.id = id;
    }

    /**
     * @param event {@link LongEvent}
     */
    private void handleEvent(final LongEvent event)
    {
        LOGGER.info("{}: LongEventHandler.onEvent: Event = {}", Thread.currentThread().getName(), event);

        // Kann auch vom CleaningEventHandler erledigt werden, wenn es mehrere EventHandler sind.
        // event.clear();
    }

    /**
     * @see EventHandler#onEvent(Object, long, boolean)
     */
    @Override
    public void onEvent(final LongEvent event, final long sequence, final boolean endOfBatch)
    {
        // Load-Balancing auf die Handler über die Sequence.
        if ((this.id == -1) || (this.id == (sequence % LongEventMain.THREAD_COUNT)))
        {
            handleEvent(event);
        }
    }
}
