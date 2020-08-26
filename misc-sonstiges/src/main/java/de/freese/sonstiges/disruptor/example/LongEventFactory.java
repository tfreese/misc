// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import com.lmax.disruptor.EventFactory;

/**
 * @author Thomas Freese
 */
public class LongEventFactory implements EventFactory<LongEvent>
{
    /**
     * @see com.lmax.disruptor.EventFactory#newInstance()
     */
    @Override
    public LongEvent newInstance()
    {
        return new LongEvent();
    }
}
