// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.concurrent.TimeUnit;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.utils.JSensorThreadFactory;

/**
 * @author Thomas Freese
 */
public class DisruptorBackEnd extends AbstractBackend
{
    /**
     *
     */
    public static final int THREAD_COUNT = 3;

    /**
     *
     */
    private Disruptor<SensorValue> disruptor;

    /**
     * int ringBufferSize = Integer.highestOneBit(31) << 1;
     */
    private int ringBufferSize = 128;

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#onStart()
     */
    @Override
    protected void onStart() throws Exception
    {
        this.disruptor = new Disruptor<>(SensorValue::new, this.ringBufferSize, new JSensorThreadFactory("jsensor-disruptor-"));

        DisruptorSensorEventHandler[] handlers = new DisruptorSensorEventHandler[THREAD_COUNT];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new DisruptorSensorEventHandler(i);
        }

        // disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());
        this.disruptor.handleEventsWith(handlers);

        this.disruptor.start();
    }

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#onStop()
     */
    @Override
    protected void onStop() throws Exception
    {
        this.disruptor.halt();
        this.disruptor.shutdown(3, TimeUnit.SECONDS);
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue)
    {
        RingBuffer<SensorValue> ringBuffer = this.disruptor.getRingBuffer();

        long sequence = ringBuffer.next();

        try
        {
            SensorValue value = ringBuffer.get(sequence);

            value.copyFrom(sensorValue);
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }

    /**
     * Default: 128
     *
     * @param ringBufferSize int
     */
    public void setRingBufferSize(final int ringBufferSize)
    {
        this.ringBufferSize = ringBufferSize;
    }
}
