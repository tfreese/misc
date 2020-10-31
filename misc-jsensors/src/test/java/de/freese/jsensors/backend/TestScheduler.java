// Created: 28.10.2020
package de.freese.jsensors.backend;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestScheduler
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testScheduler() throws Exception
    {
        // Direktes Speichern des SensorWertes im gleichen Thread.
        Sensor sensor = new ConstantSensor("test/Sensor", "123.456");
        sensor.setBackend(new ConsoleBackend());

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        SyncFuture<Object> future = new SyncFuture<>();

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();
            future.setResponse(new Object());
        }, 5, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensor::scan, 1, 1, TimeUnit.SECONDS);

        future.get();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testSchedulerMitDisruptor() throws Exception
    {
        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.start();

        Sensor sensor = new ConstantSensor("test/Sensor", "123.456");
        sensor.setBackend(backendDisruptor);

        backendDisruptor.register(sensor, new ConsoleBackend());

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        SyncFuture<Object> future = new SyncFuture<>();

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();
            future.setResponse(new Object());
        }, 5, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensor::scan, 1, 1, TimeUnit.SECONDS);

        future.get();
    }
}
