// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.rsocket.RSocketBackend;
import de.freese.jsensors.backend.rsocket.SensorRSocketServer;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestRSocketBackend
{
    /**
     *
     */
    private static SyncFuture<SensorValue> future = new SyncFuture<>();

    /**
     *
     */
    private static final int PORT = 7000;

    /**
     *
     */
    private static SensorRSocketServer rSocketServer;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        rSocketServer.stop();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        SensorRegistry registryServer = new SensorRegistry();
        registryServer.bind("test.sensor.rsocket", (Backend) future::setResponse);

        rSocketServer = new SensorRSocketServer();
        rSocketServer.setPort(PORT);
        rSocketServer.setSensorRegistry(registryServer);
        rSocketServer.start();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testRSocketBackend() throws Exception
    {
        SensorRegistry registryClient = new SensorRegistry();

        Sensor sensor = new ConstantSensor("test.sensor.rsocket", "123.456");
        sensor.bindTo(registryClient);

        RSocketBackend backendRSocket = new RSocketBackend();
        backendRSocket.setUri(URI.create("rsocket://localhost:" + PORT));

        // SensorWert mit dem RSocketBackend verknüpfen.
        registryClient.bind("test.sensor.rsocket", backendRSocket);
        registryClient.start();

        sensor.measure();

        SensorValue sensorValue = future.get();
        backendRSocket.stop();

        assertNotNull(sensorValue);
        assertEquals("test.sensor.rsocket", sensorValue.getName());
        assertEquals("123.456", sensorValue.getValue());
    }
}
