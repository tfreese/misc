// Created: 31.10.2020
package de.freese.jsensors;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.hsqldb.jdbc.JDBCPool;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.ConsoleBackend;
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
public class SimpleClientDemo
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        SensorRegistry registry = new SensorRegistry();

        // Sensoren definieren
        DiskSensor sensorDisk = new DiskSensor(new File("/"), "root");
        sensorDisk.bindTo(registry);

        Sensor sensorCpu = new CpuSensor();
        sensorCpu.bindTo(registry);

        Sensor sensorMemory = new MemorySensor();
        sensorMemory.bindTo(registry);

        Sensor sensorSwap = new SwapSensor();
        sensorSwap.bindTo(registry);

        NetworkSensor sensorNetwork = new NetworkSensor();
        sensorNetwork.bindTo(registry);

        // Backends definieren
        CSVBackend csvBackendDiskRoot = new CSVBackend();
        csvBackendDiskRoot.setPath(Paths.get("logs", "diskRoot.csv"));
        csvBackendDiskRoot.setBatchSize(6);

        CSVBackend csvBackendOneFileForAll = new CSVBackend();
        csvBackendOneFileForAll.setPath(Paths.get("logs", "sensors.csv"));
        csvBackendOneFileForAll.setBatchSize(6);

        JDBCPool dataSource = new JDBCPool();
        dataSource.setUrl("jdbc:hsqldb:file:logs/hsqldb/sensordb;create=true;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        JDBCBackend jdbcBackendDiskRoot = new JDBCBackend();
        jdbcBackendDiskRoot.setDataSource(dataSource);
        jdbcBackendDiskRoot.setTableName("DISK_ROOT");
        jdbcBackendDiskRoot.setBatchSize(6);

        JDBCBackend jdbcBackendOneTableForAll = new JDBCBackend();
        jdbcBackendOneTableForAll.setDataSource(dataSource);
        jdbcBackendOneTableForAll.setTableName("SENSORS");
        jdbcBackendOneTableForAll.setBatchSize(6);

        Backend backendConsole = new ConsoleBackend();

        // Sensoren mit den Backends verknüpfen.
        registry.bind("disk.free.root", csvBackendDiskRoot, jdbcBackendDiskRoot, csvBackendOneFileForAll, jdbcBackendOneTableForAll);
        registry.bind("disk.usage.root", csvBackendDiskRoot, jdbcBackendDiskRoot, csvBackendOneFileForAll, jdbcBackendOneTableForAll);

        registry.bind("cpu.usage", csvBackendOneFileForAll, jdbcBackendOneTableForAll);

        registry.bind("memory.free", backendConsole);
        registry.bind("memory.usage", backendConsole);

        registry.bind("swap.free", backendConsole);
        registry.bind("swap.usage", backendConsole);

        registry.bind("network.in", backendConsole);
        registry.bind("network.out", backendConsole);

        registry.start();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

        scheduledExecutorService.schedule(() -> {
            registry.stop();

            try
            {
                dataSource.close(1);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            scheduledExecutorService.shutdownNow();
        }, 20, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensorDisk::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorCpu::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorMemory::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorSwap::measure, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorNetwork::measure, 1, 3, TimeUnit.SECONDS);
    }
}
