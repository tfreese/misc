// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestDisruptorBackend
{
    /**
     *
     */
    private static SyncFuture<SensorValue> future = new SyncFuture<>();

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testDisruptorBackEnd() throws Exception
    {
        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.start();

        Sensor sensor = new ConstantSensor("test/Sensor", "123.456");
        sensor.setBackend(backendDisruptor);

        backendDisruptor.register(sensor, new ConsumerBackend(future::setResponse));

        sensor.scan();

        SensorValue sensorValue = future.get();
        backendDisruptor.stop();

        assertNotNull(sensorValue);
        assertEquals("123.456", sensorValue.getValue());
        assertEquals("TEST_SENSOR", sensorValue.getName());
    }
}
