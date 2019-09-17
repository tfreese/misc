// Created: 27.03.2018
package de.freese.maven.proxy.old.netty.handler;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.old.model.MavenRequest;
import de.freese.maven.proxy.old.model.MavenResponse;
import de.freese.maven.proxy.old.repository.Repository;
import de.freese.maven.proxy.old.repository.file.FileRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler für Requests an den Maven Proxy.
 *
 * @author Thomas Freese
 */
public class NettyMavenRequestHandler extends ChannelInboundHandlerAdapter
{
    /**
     *
     */
    private final FileRepository fileRepository;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private final Repository repository;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenRequestHandler}.
     *
     * @param repository {@link Repository}
     * @param fileRepository {@link FileRepository}
     */
    public NettyMavenRequestHandler(final Repository repository, final FileRepository fileRepository)
    {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
        this.fileRepository = Objects.requireNonNull(fileRepository, "fileRepository required");
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
    {
        MavenRequest mavenRequest = (MavenRequest) msg;

        // Absender anonymisieren.
        mavenRequest.setUserAgentValue("none");

        MavenResponse mavenResponse = null;

        if (mavenRequest.getHttpMethod().equals("HEAD"))
        {
            mavenResponse = this.fileRepository.exist(mavenRequest);

            if (mavenResponse == null)
            {
                mavenResponse = this.repository.exist(mavenRequest);
            }
        }
        else if (mavenRequest.getHttpMethod().equals("GET"))
        {
            mavenResponse = this.fileRepository.getResource(mavenRequest);

            if (mavenResponse == null)
            {
                mavenResponse = this.repository.getResource(mavenRequest);
                this.fileRepository.writeFile(mavenResponse);
            }
        }
        else if (mavenRequest.getHttpMethod().equals("PUT"))
        {
            throw new UnsupportedOperationException(mavenRequest.getHttpMethod());
        }
        else
        {
            throw new UnsupportedOperationException(mavenRequest.getHttpMethod());
        }

        if (mavenResponse != null)
        {
            mavenResponse.setServerValue("Maven-Proxy");
        }

        ctx.writeAndFlush(mavenResponse);
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();

        super.channelReadComplete(ctx);
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
    {
        super.exceptionCaught(ctx, cause);

        getLogger().error(null, cause);

        ctx.close();
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return this.logger;
    }
}
