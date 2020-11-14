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
import de.freese.jsensors.SensorBackendRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;
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
        rSocketServer = new SensorRSocketServer();
        rSocketServer.setPort(PORT);

        SensorBackendRegistry registryServer = new SensorBackendRegistry();
        registryServer.register("TEST_SENSOR_RSOCKET", (Backend) future::setResponse);

        rSocketServer.setSensorBackendRegistry(registryServer);

        rSocketServer.start();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testRSocketBackend() throws Exception
    {
        Sensor sensor = new ConstantSensor("TEST_SENSOR_RSOCKET", "123.456");

        RSocketBackend backendRSocket = new RSocketBackend();
        backendRSocket.setUri(URI.create("rsocket://localhost:" + PORT));

        SensorBackendRegistry registryClient = new SensorBackendRegistry();

        // SensorWert mit dem RSocketBackend verknüpfen.
        registryClient.register(sensor, backendRSocket);

        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        DisruptorBackend backendDisruptor = new DisruptorBackend();
        backendDisruptor.setParallelism(2);
        backendDisruptor.setRingBufferSize(8);
        backendDisruptor.setSensorBackendRegistry(registryClient);

        sensor.setBackend(backendDisruptor);

        backendRSocket.start();
        backendDisruptor.start();

        sensor.scan();

        SensorValue sensorValue = future.get();
        backendDisruptor.stop();
        backendRSocket.stop();

        assertNotNull(sensorValue);
        assertEquals("123.456", sensorValue.getValue());
        assertEquals("TEST_SENSOR_RSOCKET", sensorValue.getName());
    }
}
