// Created: 19.10.2020
package de.freese.jsensors.backend.rsocket;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.SensorBackendRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.utils.LifeCycle;
import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.core.Resume;
import io.rsocket.frame.decoder.PayloadDecoder;
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
    private int port;

    /**
     *
     */
    private int selectCount = 1;

    /**
     *
     */
    private SensorBackendRegistry sensorBackendRegistry;

    /**
     *
     */
    private Disposable server;

    /**
     *
     */
    private int workerCount = 3;

    /**
     * Erstellt ein neues {@link SensorRSocketServer} Object.
     */
    public SensorRSocketServer()
    {
        super();
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

        List<Backend> backends = this.sensorBackendRegistry.getBackends(sensorValue.getName());

        if ((backends == null) || backends.isEmpty())
        {
            getLogger().error("no backends configured for sensor '{}'", sensorValue.getName());
            return Mono.empty();
        }

        for (Backend backend : backends)
        {
            try
            {
                backend.save(sensorValue);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }

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
     * @param port int
     */
    public void setPort(final int port)
    {
        this.port = port;
    }

    /**
     * Default: 1
     *
     * @param selectCount int
     */
    public void setSelectCount(final int selectCount)
    {
        this.selectCount = selectCount;
    }

    /**
     * @param sensorBackendRegistry {@link SensorBackendRegistry}
     */
    public void setSensorBackendRegistry(final SensorBackendRegistry sensorBackendRegistry)
    {
        this.sensorBackendRegistry = Objects.requireNonNull(sensorBackendRegistry, "sensorBackendRegistry required");
    }

    /**
     * Default: 3
     *
     * @param workerCount int
     */
    public void setWorkerCount(final int workerCount)
    {
        this.workerCount = workerCount;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (this.port < 1)
        {
            throw new IllegalArgumentException("port must be >= 1");
        }

        if (this.selectCount < 1)
        {
            throw new IllegalArgumentException("selectCount must be >= 1");
        }

        if (this.workerCount < 1)
        {
            throw new IllegalArgumentException("workerCount must be >= 1");
        }

        Objects.requireNonNull(this.sensorBackendRegistry, "sensorBackendRegistry required");

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
        // TcpResources.set(LoopResources.create("jsync-server", 2, 4, true));
        // TcpResources.set(ConnectionProvider.create("demo-connectionPool", 16));

        // @formatter:off
        TcpServer tcpServer = TcpServer.create()
                .host("localhost")
                .port(this.port)
                .runOn(LoopResources.create("sensor-server-", this.selectCount, this.workerCount, false))
                ;
        // @formatter:on

        // @formatter:off
         this.server = RSocketServer
                .create()
                .acceptor(SocketAcceptor.forFireAndForget(this::forFireAndForget))
                .resume(resume)
                .payloadDecoder(PayloadDecoder.DEFAULT)
                .bind(TcpServerTransport.create(tcpServer))
                .block()
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
