// Created: 28.10.2020
package de.freese.jsensors.backend.rsocket;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.utils.LifeCycle;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.ByteBufPayload;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

/**
 * @author Thomas Freese
 */
public class RSocketBackend extends AbstractBackend implements LifeCycle
{
    /**
     *
     */
    private RSocket client;

    /**
     *
     */
    private URI uri;

    /**
     *
     */
    private int workerCount = 3;

    /**
     * Erstellt ein neues {@link RSocketBackend} Object.
     */
    public RSocketBackend()
    {
        super();
    }

    /**
     * @param sensorValue {@link SensorValue}
     * @return {@link ByteBuf}
     */
    protected ByteBuf encode(final SensorValue sensorValue)
    {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

        // byteBuf.writeCharSequence(sensorValue.getName(), StandardCharsets.UTF_8);
        byte[] bytes = sensorValue.getName().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        // byteBuf.writeCharSequence(sensorValue.getValue(), StandardCharsets.UTF_8);
        bytes = sensorValue.getValue().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        byteBuf.writeLong(sensorValue.getTimestamp());

        return byteBuf;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
    {
        ByteBuf byteBuf = encode(sensorValue);

        // @formatter:off
        this.client
            .fireAndForget(ByteBufPayload.create(byteBuf))
            .block()
            ;
        // @formatter:on

        // byteBuf.release();
    }

    /**
     * @param uri {@link URI}
     */
    public void setUri(final URI uri)
    {
        this.uri = Objects.requireNonNull(uri, "uri required");
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
        if (this.workerCount < 1)
        {
            throw new IllegalArgumentException("workerCount must be >= 1");
        }

        Objects.requireNonNull(this.uri, "uri required");

        // @formatter:off
        TcpClient tcpClient = TcpClient.create()
                .host(this.uri.getHost())
                .port(this.uri.getPort())
                .runOn(LoopResources.create("sensor-client-" + this.uri.getPort(), this.workerCount, true))
                ;
        // @formatter:on

        // @formatter:off
        this.client = RSocketConnector.create()
            .reconnect(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .connect(TcpClientTransport.create(tcpClient))
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
        this.client.dispose();
    }
}
