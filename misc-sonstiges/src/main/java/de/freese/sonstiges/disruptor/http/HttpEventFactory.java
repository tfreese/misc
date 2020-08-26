// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import com.lmax.disruptor.EventFactory;

/**
 * @author Thomas Freese
 */
public class HttpEventFactory implements EventFactory<HttpEvent>
{
    /**
     * @see com.lmax.disruptor.EventFactory#newInstance()
     */
    @Override
    public HttpEvent newInstance()
    {
        return new HttpEvent();
    }
}
