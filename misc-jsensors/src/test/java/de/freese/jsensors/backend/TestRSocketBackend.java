// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.disruptor.DisruptorBackEnd;
import de.freese.jsensors.backend.rsocket.RSocketBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.SyncFuture;
import io.netty.buffer.ByteBuf;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpServer;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
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
    private static CloseableChannel server;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        server.dispose();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        // @formatter:off
        TcpServer tcpServer = TcpServer.create()
                .host("localhost")
                .port(7000)
                ;
        // @formatter:on

        SocketAcceptor socketAcceptor = SocketAcceptor.forFireAndForget(payload -> {
            ByteBuf byteBuf = payload.data();

            int length = byteBuf.readInt();
            String name = byteBuf.readCharSequence(length, StandardCharsets.UTF_8).toString();

            length = byteBuf.readInt();
            String value = byteBuf.readCharSequence(length, StandardCharsets.UTF_8).toString();

            long timeStamp = byteBuf.readLong();

            SensorValue sensorValue = new SensorValue(name, value, timeStamp);
            future.setResponse(sensorValue);

            return Mono.empty();
        });

        // @formatter:off
        server = RSocketServer
            .create()
            .acceptor(socketAcceptor)
            .bindNow(TcpServerTransport.create(tcpServer))
            ;
        // @formatter:on
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testRSocketBackEnd() throws Exception
    {
        // Der Disruptor überträgt den SensorWert zum Speichern an einen anderen Thread.
        // In diesem Fall wird der SensorWert an das RSocketBackend durchgereicht.
        DisruptorBackEnd backendDisruptor = new DisruptorBackEnd();
        backendDisruptor.start();

        Sensor sensor = new ConstantSensor("test/Sensor", "123.456");
        sensor.setBackend(backendDisruptor);

        RSocketBackend backendRSocket = new RSocketBackend();
        backendRSocket.setUri(URI.create("rsocket://localhost:" + PORT));
        backendRSocket.start();

        backendDisruptor.register(sensor, backendRSocket);

        sensor.scan();

        SensorValue sensorValue = future.get();
        backendDisruptor.stop();
        backendRSocket.stop();

        assertNotNull(sensorValue);
        assertEquals("123.456", sensorValue.getValue());
        assertEquals("TEST_SENSOR", sensorValue.getName());
    }
}
