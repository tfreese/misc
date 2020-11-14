// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorBackendRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
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
    void testDisruptorBackend() throws Exception
    {
        Sensor sensor = new ConstantSensor("TEST_SENSOR_DISRUPTOR", "123.456");

        SensorBackendRegistry registry = new SensorBackendRegistry();
        registry.register(sensor, new ConsumerBackend(future::setResponse));

        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.setParallelism(2);
        backendDisruptor.setRingBufferSize(16);
        backendDisruptor.setSensorBackendRegistry(registry);
        backendDisruptor.start();

        sensor.setBackend(backendDisruptor);
        sensor.scan();

        SensorValue sensorValue = future.get();
        backendDisruptor.stop();

        assertNotNull(sensorValue);
        assertEquals("123.456", sensorValue.getValue());
        assertEquals("TEST_SENSOR_DISRUPTOR", sensorValue.getName());
    }
}
