// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import com.lmax.disruptor.EventHandler;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.registry.SensorBackendRegistry;

/**
 * @author Thomas Freese
 */
public class DisruptorSensorEventHandler implements EventHandler<SensorValue>
{
    /**
    *
    */
    private final int id;

    /**
     * @param id int
     */
    public DisruptorSensorEventHandler(final int id)
    {
        super();

        this.id = id;
    }

    /**
     * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
     */
    @Override
    public void onEvent(final SensorValue event, final long sequence, final boolean endOfBatch) throws Exception
    {
        // Load-Balancing auf die Handler über die Sequence.
        if ((this.id == -1) || (this.id == (sequence % DisruptorBackEnd.THREAD_COUNT)))
        {
            SensorValue sensorValue = new SensorValue();
            sensorValue.copyFrom(event);

            SensorBackendRegistry.getInstance().save(sensorValue);

            event.clear();
        }
    }
}
