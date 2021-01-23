// Created: 28.10.2020
package de.freese.jsensors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.nio.file.Paths;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.file.RRDToolBackend;
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

        sensor.measure();
        sensor.measure();
        sensor.measure();

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
