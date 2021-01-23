// Created: 19.10.2020
package de.freese.jsensors.backend.rsocket;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.SensorRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.utils.LifeCycle;
import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.core.Resume;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.Disposable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;
import reactor.util.retry.Retry;

/**
 * @author Thomas Freese
 */
public class SensorRSocketServer implements LifeCycle
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorRSocketServer.class);

    /**
     *
     */
    private final int port;

    /**
     *
     */
    private final int selectCount;

    /**
     *
     */
    private final SensorRegistry sensorRegistry;

    /**
     *
     */
    private Disposable server;

    /**
     *
     */
    private final int workerCount;

    /**
     * Erstellt ein neues {@link SensorRSocketServer} Object.
     *
     * @param port int
     * @param sensorRegistry {@link SensorRegistry}
     */
    public SensorRSocketServer(final SensorRegistry sensorRegistry, final int port)
    {
        this(sensorRegistry, port, 1, 3);
    }

    /**
     * Erstellt ein neues {@link SensorRSocketServer} Object.
     *
     * @param sensorRegistry {@link SensorRegistry}
     * @param port int
     * @param selectCount int
     * @param workerCount int
     */
    public SensorRSocketServer(final SensorRegistry sensorRegistry, final int port, final int selectCount, final int workerCount)
    {
        super();

        this.sensorRegistry = Objects.requireNonNull(sensorRegistry, "sensorRegistry required");

        if (port < 1)
        {
            throw new IllegalArgumentException("port must be >= 1");
        }

        this.port = port;

        if (selectCount < 1)
        {
            throw new IllegalArgumentException("selectCount must be >= 1");
        }

        this.selectCount = selectCount;

        if (workerCount < 1)
        {
            throw new IllegalArgumentException("workerCount must be >= 1");
        }

        this.workerCount = workerCount;
    }

    /**
     * @param payload {@link Payload}
     * @return {@link SensorValue}
     */
    protected SensorValue decode(final Payload payload)
    {
        ByteBuf byteBuf = payload.data();

        int length = byteBuf.readInt();
        String name = byteBuf.readCharSequence(length, StandardCharsets.UTF_8).toString();

        length = byteBuf.readInt();
        String value = byteBuf.readCharSequence(length, StandardCharsets.UTF_8).toString();

        long timeStamp = byteBuf.readLong();

        SensorValue sensorValue = new SensorValue(name, value, timeStamp);

        return sensorValue;
    }

    /**
     * @param payload {@link Payload}
     * @return {@link Mono}
     */
    protected Mono<Void> forFireAndForget(final Payload payload)
    {
        SensorValue sensorValue = decode(payload);

        this.sensorRegistry.store(sensorValue);

        return Mono.empty();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        getLogger().info("starting jsensor-rsocket server on port: {}", this.port);

        // Fehlermeldung, wenn Client die Verbindung schliesst.
        Hooks.onErrorDropped(th -> LOGGER.error(th.getMessage()));

        // @formatter:off
        Resume resume = new Resume()
                .sessionDuration(Duration.ofMinutes(5))
                .retry(
                        Retry
                            .fixedDelay(10, Duration.ofSeconds(1))
                            .doBeforeRetry(s -> LOGGER.debug("Disconnected. Trying to resume..."))
                )
                ;
        // @formatter:on

        // Globale Default-Resourcen.
        // TcpResources.set(LoopResources.create("sensor-server", 1, 3, true));
        // TcpResources.set(ConnectionProvider.create("connectionPool", 16));

        // @formatter:off
        TcpServer tcpServer = TcpServer.create()
                .host("localhost")
                .port(this.port)
                .runOn(LoopResources.create("sensor-server-", this.selectCount, this.workerCount, false))
                ;
        // @formatter:on

        ServerTransport<CloseableChannel> serverTransport = TcpServerTransport.create(tcpServer);
        // ServerTransport<Closeable> serverTransport = LocalServerTransport.create("test-local-" + port);

        SocketAcceptor socketAcceptor = SocketAcceptor.forFireAndForget(this::forFireAndForget);

        // @formatter:off
        this.server = RSocketServer.create()
                .acceptor(socketAcceptor)
                .resume(resume)
                .payloadDecoder(PayloadDecoder.DEFAULT)
                .bindNow(serverTransport)
                ;
        // @formatter:on
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        getLogger().info("stopping jsensor-rsocket server");

        this.server.dispose();
    }
}
