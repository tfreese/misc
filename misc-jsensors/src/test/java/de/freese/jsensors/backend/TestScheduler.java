// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorBackendRegistry;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestScheduler
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testScheduler() throws Exception
    {
        // Direktes Speichern des SensorWertes im gleichen Thread.
        Sensor sensor = new ConstantSensor("TEST_SENSOR_SCHEDULER", "123.456");
        sensor.setBackend(new ConsoleBackend());

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        SyncFuture<Object> future = new SyncFuture<>();

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();
            future.setResponse(new Object());
        }, 2, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensor::scan, 0, 1, TimeUnit.SECONDS);

        assertNotNull(future.get());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testSchedulerMitDisruptor() throws Exception
    {
        Sensor sensor = new ConstantSensor("TEST_SENSOR_SCHEDULER_MIT_DISRUPTOR", "123.456");

        SensorBackendRegistry registry = new SensorBackendRegistry();
        registry.register(sensor, new ConsoleBackend());

        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.setParallelism(2);
        backendDisruptor.setRingBufferSize(8);
        backendDisruptor.setSensorBackendRegistry(registry);

        sensor.setBackend(backendDisruptor);

        backendDisruptor.start();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        SyncFuture<Object> future = new SyncFuture<>();

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();
            backendDisruptor.stop();
            future.setResponse(new Object());
        }, 2, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensor::scan, 0, 1, TimeUnit.SECONDS);

        assertNotNull(future.get());
    }
}
