// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.disruptor.DisruptorBackEnd;
import de.freese.jsensors.registry.LifeCycleManager;
import de.freese.jsensors.registry.SensorBackendRegistry;
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
    @AfterAll
    static void afterAll() throws Exception
    {
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testDisruptorBackEnd() throws Exception
    {
        Backend backendDisruptor = new DisruptorBackEnd();

        Sensor sensor = new ConstantSensor("123.456");
        sensor.setName("test/Sensor");
        sensor.setBackend(backendDisruptor);

        SensorBackendRegistry.getInstance().register(sensor, new ConsumerBackend(future::setResponse));

        LifeCycleManager.getInstance().start();
        sensor.scan();

        SensorValue sensorValue = future.get();
        LifeCycleManager.getInstance().stop();

        assertNotNull(sensorValue);
        assertEquals("123.456", sensorValue.getValue());
        assertEquals("TEST_SENSOR", sensorValue.getName());
    }
}
