// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.nio.ByteBuffer;
import java.util.Map;
import com.lmax.disruptor.RingBuffer;

/**
 * @author Thomas Freese
 */
public class HttpEventProducer
{
    /**
     *
     */
    private final Map<String, Object> mapResponse;

    /**
     *
     */
    private final RingBuffer<HttpEvent> ringBuffer;

    /**
     * Erstellt ein neues {@link HttpEventProducer} Object.
     *
     * @param ringBuffer {@link RingBuffer}
     * @param mapResponse {@link Map}
     */
    public HttpEventProducer(final RingBuffer<HttpEvent> ringBuffer, final Map<String, Object> mapResponse)
    {
        super();

        this.ringBuffer = ringBuffer;
        this.mapResponse = mapResponse;
    }

    /**
     * @param requestId String
     * @param buffer {@link ByteBuffer}
     * @param numRead int
     */
    public void onData(final String requestId, final ByteBuffer buffer, final int numRead)
    {
        long sequence = this.ringBuffer.next();

        try
        {
            HttpEvent event = this.ringBuffer.get(sequence);
            event.setBuffer(buffer);
            event.setRequestId(requestId);
            event.setNumRead(numRead);
        }
        finally
        {
            this.mapResponse.put(requestId, "0");
            this.ringBuffer.publish(sequence);
        }
    }
}
