// Created: 31.10.2020
package de.freese.jsensors;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.hsqldb.jdbc.JDBCPool;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.ConsoleBackend;
import de.freese.jsensors.backend.async.AsyncBackend;
import de.freese.jsensors.backend.async.ExecutorBackend;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.backend.file.CSVBackend;
import de.freese.jsensors.backend.jdbc.JDBCBackend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.cpu.CpuSensor;
import de.freese.jsensors.sensor.disk.DiskSensor;
import de.freese.jsensors.sensor.memory.MemorySensor;
import de.freese.jsensors.sensor.network.NetworkSensor;
import de.freese.jsensors.sensor.swap.SwapSensor;

/**
 * @author Thomas Freese
 */
public class SensorDemo
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Datenfluß: Sensor -> DisruptorBackend -> SensorRegistry -> Backend

        // Konkrete Backends definieren
        CSVBackend csvBackendCpuUsage = new CSVBackend(Paths.get("logs", "cpuUsage.csv"), true);
        csvBackendCpuUsage.setBatchSize(3);
        csvBackendCpuUsage.start();

        CSVBackend csvBackendOneFileForAll = new CSVBackend(Paths.get("logs", "sensors.csv"));
        csvBackendOneFileForAll.setBatchSize(6);
        csvBackendOneFileForAll.start();

        JDBCPool dataSource = new JDBCPool();
        dataSource.setUrl("jdbc:hsqldb:file:logs/hsqldb/sensordb;create=true;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        JDBCBackend jdbcBackendCpuUsage = new JDBCBackend(dataSource, "CPU_USAGE", true);
        jdbcBackendCpuUsage.setBatchSize(3);
        jdbcBackendCpuUsage.start();

        JDBCBackend jdbcBackendOneTableForAll = new JDBCBackend(dataSource, "SENSORS");
        jdbcBackendOneTableForAll.setBatchSize(6);
        jdbcBackendOneTableForAll.start();

        Backend backendConsole = new ConsoleBackend();

        // Backend für die Sensoren definieren.
        SensorRegistry registry = new SensorRegistry();
        DisruptorBackend disruptorBackend = new DisruptorBackend(registry, 4);
        disruptorBackend.start();

        // Sensoren definieren
        Sensor sensorDisk = new DiskSensor(new File("/"), "root");
        sensorDisk.setBackend(disruptorBackend);

        Sensor sensorCpu = new CpuSensor();
        sensorCpu.setBackend(disruptorBackend);

        Sensor sensorMemory = new MemorySensor();
        sensorMemory.setBackend(disruptorBackend);

        Sensor sensorSwap = new SwapSensor();
        sensorSwap.setBackend(disruptorBackend);

        NetworkSensor sensorNetwork = new NetworkSensor();
        sensorNetwork.setBackend(disruptorBackend);
        sensorNetwork.start();

        // Sensoren mit den konkreten Backends verknüpfen.
        sensorDisk.getNames().forEach(name -> registry.bind(name, csvBackendOneFileForAll, jdbcBackendOneTableForAll));

        // sensorCpu.getNames().forEach(name -> registry.bind(name, csvBackendOneFileForAll, jdbcBackendOneTableForAll));
        registry.bind("cpu.usage", csvBackendCpuUsage, jdbcBackendCpuUsage);

        sensorMemory.getNames().forEach(name -> registry.bind(name, csvBackendOneFileForAll, jdbcBackendOneTableForAll));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        sensorSwap.getNames()
                .forEach(name -> registry.bind(name, new ExecutorBackend(backendConsole, executorService), csvBackendOneFileForAll, jdbcBackendOneTableForAll));

        AsyncBackend asyncBackendConsole = new AsyncBackend(backendConsole, 2);
        asyncBackendConsole.start();
        sensorNetwork.getNames().forEach(name -> registry.bind(name, asyncBackendConsole, csvBackendOneFileForAll, jdbcBackendOneTableForAll));

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();
            executorService.shutdownNow();

            // Erst die Sensoren stoppen.
            sensorNetwork.stop();

            // Dann die Backends stoppen.
            disruptorBackend.stop();
            asyncBackendConsole.stop();

            csvBackendCpuUsage.stop();
            csvBackendOneFileForAll.stop();
            jdbcBackendCpuUsage.stop();
            jdbcBackendOneTableForAll.start();

            try
            {
                dataSource.close(1);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            // System.exit(0);
        }, 13, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensorDisk::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorCpu::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorMemory::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorSwap::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorNetwork::measure, 1, 3, TimeUnit.SECONDS);
    }
}
