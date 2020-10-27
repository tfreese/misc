// Created: 27.10.2020
package de.freese.jsensors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.ConsoleBackend;
import de.freese.jsensors.backend.disruptor.DisruptorBackEnd;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.cpu.CpuUsage;

/**
 * @author Thomas Freese
 */
public class SensorMain
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        Sensor sensorCpu = new CpuUsage();
        sensorCpu.setName("my-CPU");
        sensorCpu.start();

        Backend backendDisruptor = new DisruptorBackEnd();
        backendDisruptor.start();
        sensorCpu.setBackend(backendDisruptor);

        Backend backendConsole = new ConsoleBackend();
        backendConsole.start();

        SensorBackendRegistry.getInstance().register(sensorCpu, backendConsole);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

        scheduledExecutorService.scheduleWithFixedDelay(sensorCpu::scan, 3, 3, TimeUnit.SECONDS);
    }
}
