// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import com.lmax.disruptor.EventHandler;

/**
 * @author Thomas Freese
 */
public class CleaningEventHandler implements EventHandler<HttpEvent>
{
    /**
     * @see com.lmax.disruptor.EventHandler#onEvent(Object, long, boolean)
     */
    @Override
    public void onEvent(final HttpEvent event, final long sequence, final boolean endOfBatch)
    {
        System.out.printf("%s_CleaningEventHandler.onEvent: Sequence %d%n", Thread.currentThread().getName(), sequence);
        event.clear();
    }
}
