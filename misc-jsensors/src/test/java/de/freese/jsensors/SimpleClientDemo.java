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
        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.start();

        // Sensoren
        DiskFreeSpaceSensor sensorDiskFreeSpace = new DiskFreeSpaceSensor("DISKFREE_ROOT");
        sensorDiskFreeSpace.setDisk("/");
        sensorDiskFreeSpace.start();
        sensorDiskFreeSpace.setBackend(backendDisruptor);

        DiskUsageSensor sensorDiskUsage = new DiskUsageSensor("DISKUSAGE_ROOT");
        sensorDiskUsage.setDisk("/");
        sensorDiskUsage.start();
        sensorDiskUsage.setBackend(backendDisruptor);

        Sensor sensorCpuUsage = new CpuUsageSensor("CPU_USAGE");
        sensorCpuUsage.setBackend(backendDisruptor);

        Sensor sensorMemoryUsage = new MemoryUsageSensor("MEMORY_USAGE");
        sensorMemoryUsage.setBackend(backendDisruptor);

        Sensor sensorSwapUsage = new MemoryUsageSensor("SWAP_USAGE");
        sensorSwapUsage.setBackend(backendDisruptor);

        NetworkUsageSensor sensorNetworkUsage = new NetworkUsageSensor("NETWORK");
        sensorNetworkUsage.start();
        sensorNetworkUsage.setBackend(backendDisruptor);

        // Backends
        CSVBackend csvBackendDiskFreeRoot = new CSVBackend();
        csvBackendDiskFreeRoot.setPath(Paths.get("logs", "diskFreeRoot.csv"));
        csvBackendDiskFreeRoot.setBatchSize(5);
        csvBackendDiskFreeRoot.setExclusive(true);
        csvBackendDiskFreeRoot.start();

        CSVBackend csvBackendOneFileForAll = new CSVBackend();
        csvBackendOneFileForAll.setPath(Paths.get("logs", "sensors.csv"));
        csvBackendOneFileForAll.setBatchSize(5);
        csvBackendOneFileForAll.start();

        JDBCPool dataSource = new JDBCPool();
        dataSource.setUrl("jdbc:hsqldb:file:logs/hsqldb/sensordb;create=true;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        JDBCBackend jdbcBackendDiskFreeRoot = new JDBCBackend();
        jdbcBackendDiskFreeRoot.setDataSource(dataSource);
        jdbcBackendDiskFreeRoot.setTableName("DISKFREE_ROOT");
        jdbcBackendDiskFreeRoot.setBatchSize(5);
        jdbcBackendDiskFreeRoot.setExclusive(true);
        jdbcBackendDiskFreeRoot.start();

        JDBCBackend jdbcBackendOneTableForAll = new JDBCBackend();
        jdbcBackendOneTableForAll.setDataSource(dataSource);
        jdbcBackendOneTableForAll.setTableName("SENSORS");
        jdbcBackendOneTableForAll.setBatchSize(5);
        jdbcBackendOneTableForAll.start();

        Backend backendConsole = new ConsoleBackend();

        // Backends den Sensoren zuordnen.
        backendDisruptor.register(sensorDiskFreeSpace, csvBackendDiskFreeRoot, jdbcBackendDiskFreeRoot);
        backendDisruptor.register(sensorDiskUsage, csvBackendOneFileForAll, jdbcBackendOneTableForAll);
        backendDisruptor.register(sensorCpuUsage, csvBackendOneFileForAll, jdbcBackendOneTableForAll);
        backendDisruptor.register(sensorMemoryUsage, backendConsole);
        backendDisruptor.register(sensorSwapUsage, backendConsole);
        backendDisruptor.register("NETWORK-IN", backendConsole);
        backendDisruptor.register("NETWORK-OUT", backendConsole);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

        scheduledExecutorService.schedule(() -> {
            scheduledExecutorService.shutdownNow();

            backendDisruptor.stop();
            sensorDiskFreeSpace.stop();
            sensorDiskUsage.stop();
            sensorNetworkUsage.stop();

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
