// Created: 28.10.2020
package de.freese.jsensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.hsqldb.jdbc.JDBCPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.file.RRDToolBackend;
import de.freese.jsensors.backend.jdbc.JDBCBackend;
import de.freese.jsensors.backend.rsocket.RSocketBackend;
import de.freese.jsensors.backend.rsocket.SensorRSocketServer;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestBackends
{
    /**
    *
    */
    private static JDBCPool dataSource;

    /**
     * @throws SQLException Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws SQLException
    {
        dataSource.close(1);
    }

    /**
    *
    */
    @BeforeAll
    static void beforeAll()
    {
        dataSource = new JDBCPool();
        dataSource.setUrl("jdbc:hsqldb:mem:sensordb;create=true;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testJdbcBackend() throws Exception
    {
        JDBCBackend jdbcBackend = new JDBCBackend(dataSource, "SENSORS");
        jdbcBackend.setBatchSize(3);
        jdbcBackend.start();

        Sensor sensor = new ConstantSensor("test.sensor.jdbc", "123.456");
        sensor.setBackend(jdbcBackend);

        for (int i = 0; i < 3; i++)
        {
            sensor.measure();
            TimeUnit.MILLISECONDS.sleep(10);
        }

        jdbcBackend.stop();

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testJdbcBackendExlusive() throws Exception
    {
        JDBCBackend jdbcBackend = new JDBCBackend(dataSource, "SENSOR_EXCLUSIVE", true);
        jdbcBackend.setBatchSize(3);
        jdbcBackend.start();

        Sensor sensor = new ConstantSensor("test.sensor.jdbcExclusive", "123.456");
        sensor.setBackend(jdbcBackend);

        for (int i = 0; i < 3; i++)
        {
            sensor.measure();
            TimeUnit.MILLISECONDS.sleep(10);
        }

        jdbcBackend.stop();

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testRRDToolBackend() throws Exception
    {
        RRDToolBackend rrdToolBackend = new RRDToolBackend(Paths.get("logs", "sensors.rrd"));
        rrdToolBackend.setBatchSize(2);
        rrdToolBackend.start();

        Sensor sensor = new ConstantSensor("test.sensor.rrdtool", "123.456");
        sensor.setBackend(rrdToolBackend);

        for (int i = 0; i < 3; i++)
        {
            sensor.measure();
            TimeUnit.MILLISECONDS.sleep(10);
        }

        rrdToolBackend.stop();

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testRSocketBackend() throws Exception
    {
        // Datenfluß: Sensor -> RSocketBackend -> RSocketServer -> SensorRegistry -> Backend

        // RSocket-Server starten.
        SensorRegistry registry = new SensorRegistry();
        SensorRSocketServer rSocketServer = new SensorRSocketServer(registry, 7000);
        rSocketServer.start();

        // RSocket-Backend für den Sensor starten.
        RSocketBackend backendRSocket = new RSocketBackend(URI.create("rsocket://localhost:" + 7000));
        backendRSocket.start();

        Sensor sensor = new ConstantSensor("test.sensor.rsocket", "123.456");
        sensor.setBackend(backendRSocket);

        SyncFuture<SensorValue> future = new SyncFuture<>();
        Backend backend = future::setResponse;

        sensor.getNames().forEach(name -> registry.bind(name, backend));
        sensor.measure();

        SensorValue sensorValue = future.get();
        backendRSocket.stop();
        rSocketServer.stop();

        assertNotNull(sensorValue);
        assertEquals(sensor.getNames().get(0), sensorValue.getName());
        assertEquals("123.456", sensorValue.getValue());
    }
}
