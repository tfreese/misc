// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorRegistry;
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
    private static DisruptorBackend backendDisruptor;

    /**
     *
     */
    private static SyncFuture<SensorValue> future = new SyncFuture<>();

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        backendDisruptor.stop();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        SensorRegistry registryServer = new SensorRegistry();
        registryServer.bind("test.sensor.disruptor", (Backend) future::setResponse);

        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        backendDisruptor = new DisruptorBackend();
        backendDisruptor.setParallelism(2);
        backendDisruptor.setRingBufferSize(16);
        backendDisruptor.setSensorRegistry(registryServer);
        backendDisruptor.start();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testDisruptorBackend() throws Exception
    {
        SensorRegistry registryClient = new SensorRegistry();

        Sensor sensor = new ConstantSensor("test.sensor.disruptor", "123.456");
        sensor.bindTo(registryClient);

        registryClient.bind("test.sensor.disruptor", backendDisruptor);
        registryClient.start();

        sensor.measure();

        SensorValue sensorValue = future.get();
        backendDisruptor.stop();

        assertNotNull(sensorValue);
        assertEquals("test.sensor.disruptor", sensorValue.getName());
        assertEquals("123.456", sensorValue.getValue());
    }
}
