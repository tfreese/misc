// Created: 31.10.2020
package de.freese.jsensors;

import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.hsqldb.jdbc.JDBCPool;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.ConsoleBackend;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
import de.freese.jsensors.backend.file.CSVBackend;
import de.freese.jsensors.backend.jdbc.JDBCBackend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.cpu.CpuUsageSensor;
import de.freese.jsensors.sensor.disk.DiskFreeSpaceSensor;
import de.freese.jsensors.sensor.disk.DiskUsageSensor;
import de.freese.jsensors.sensor.memory.MemoryUsageSensor;
import de.freese.jsensors.sensor.network.NetworkUsageSensor;

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
        // Sensoren definieren
        DiskFreeSpaceSensor sensorDiskFreeSpace = new DiskFreeSpaceSensor("DISKFREE_ROOT");
        sensorDiskFreeSpace.setDisk("/");

        DiskUsageSensor sensorDiskUsage = new DiskUsageSensor("DISKUSAGE_ROOT");
        sensorDiskUsage.setDisk("/");

        Sensor sensorCpuUsage = new CpuUsageSensor("CPU_USAGE");
        Sensor sensorMemoryUsage = new MemoryUsageSensor("MEMORY_USAGE");
        Sensor sensorSwapUsage = new MemoryUsageSensor("SWAP_USAGE");
        NetworkUsageSensor sensorNetworkUsage = new NetworkUsageSensor("NETWORK");

        // Backends definieren
        CSVBackend csvBackendDiskFreeRoot = new CSVBackend();
        csvBackendDiskFreeRoot.setPath(Paths.get("logs", "diskFreeRoot.csv"));
        csvBackendDiskFreeRoot.setBatchSize(5);
        csvBackendDiskFreeRoot.setExclusive(true);

        CSVBackend csvBackendOneFileForAll = new CSVBackend();
        csvBackendOneFileForAll.setPath(Paths.get("logs", "sensors.csv"));
        csvBackendOneFileForAll.setBatchSize(5);

        JDBCPool dataSource = new JDBCPool();
        dataSource.setUrl("jdbc:hsqldb:file:logs/hsqldb/sensordb;create=true;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        JDBCBackend jdbcBackendDiskFreeRoot = new JDBCBackend();
        jdbcBackendDiskFreeRoot.setDataSource(dataSource);
        jdbcBackendDiskFreeRoot.setTableName("DISKFREE_ROOT");
        jdbcBackendDiskFreeRoot.setBatchSize(5);
        jdbcBackendDiskFreeRoot.setExclusive(true);

        JDBCBackend jdbcBackendOneTableForAll = new JDBCBackend();
        jdbcBackendOneTableForAll.setDataSource(dataSource);
        jdbcBackendOneTableForAll.setTableName("SENSORS");
        jdbcBackendOneTableForAll.setBatchSize(5);

        Backend backendConsole = new ConsoleBackend();

        // Sensoren mit den Backends verknüpfen.
        SensorBackendRegistry registry = new SensorBackendRegistry();
        registry.register(sensorDiskFreeSpace, csvBackendDiskFreeRoot, jdbcBackendDiskFreeRoot);
        registry.register(sensorDiskUsage, csvBackendOneFileForAll, jdbcBackendOneTableForAll);
        registry.register(sensorCpuUsage, csvBackendOneFileForAll, jdbcBackendOneTableForAll);
        registry.register(sensorMemoryUsage, backendConsole);
        registry.register(sensorSwapUsage, backendConsole);
        registry.register("NETWORK-IN", backendConsole);
        registry.register("NETWORK-OUT", backendConsole);

        // Registry in den Disruptor setzen.
        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.setParallelism(2);
        backendDisruptor.setRingBufferSize(16);
        backendDisruptor.setSensorBackendRegistry(registry);

        sensorDiskFreeSpace.setBackend(backendDisruptor);
        sensorDiskUsage.setBackend(backendDisruptor);
        sensorCpuUsage.setBackend(backendDisruptor);
        sensorMemoryUsage.setBackend(backendDisruptor);
        sensorSwapUsage.setBackend(backendDisruptor);
        sensorNetworkUsage.setBackend(backendDisruptor);

        // Backends starten
        csvBackendDiskFreeRoot.start();
        csvBackendOneFileForAll.start();
        jdbcBackendDiskFreeRoot.start();
        jdbcBackendOneTableForAll.start();
        backendDisruptor.start();

        // Sensoren starten.
        sensorDiskFreeSpace.start();
        sensorDiskUsage.start();
        sensorNetworkUsage.start();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();

            sensorDiskFreeSpace.stop();
            sensorDiskUsage.stop();
            sensorNetworkUsage.stop();

            backendDisruptor.stop();
            csvBackendDiskFreeRoot.stop();
            csvBackendOneFileForAll.stop();
            jdbcBackendDiskFreeRoot.stop();
            jdbcBackendOneTableForAll.stop();

            try
            {
                dataSource.close(1);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }, 20, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(sensorDiskFreeSpace::scan, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorDiskUsage::scan, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorCpuUsage::scan, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorMemoryUsage::scan, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorSwapUsage::scan, 1, 3, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(sensorNetworkUsage::scan, 1, 3, TimeUnit.SECONDS);
    }
}
