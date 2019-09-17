// Created: 27.03.2018
package de.freese.maven.proxy.netty.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.activation.MimetypesFileTypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.HttpRepository;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

/**
 * Handler für Requests an den Maven Proxy.
 *
 * @author Thomas Freese
 */
public class NettyMavenRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{
    /**
    *
    */
    private final FileRepository fileRepository;

    /**
        *
        */
    private final List<HttpRepository> httpRepositories;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenRequestHandler}.
     *
     * @param fileRepository {@link FileRepository}
     * @param httpRepositories {@link List}
     */
    public NettyMavenRequestHandler(final FileRepository fileRepository, final List<HttpRepository> httpRepositories)
    {
        super();

        this.fileRepository = Objects.requireNonNull(fileRepository, "fileRepository required");
        this.httpRepositories = Objects.requireNonNull(httpRepositories, "httpRepositories required");
    }

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        // Absender anonymisieren.
        // mavenRequest.setUserAgentValue("none");
        // final boolean keepAlive = HttpUtil.isKeepAlive(request);

        if (HttpMethod.HEAD.equals(request.method()))
        {
            handleHead(ctx, request);
        }
        else if (HttpMethod.GET.equals(request.method()))
        {
            handleGet(ctx, request);
        }
        else if (HttpMethod.PUT.equals(request.method()))
        {
            // deploy
            handlePut(ctx, request);
        }
        else
        {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, request.uri(), request);
        }
    }

    /**
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception
    {
        if (ctx.channel().isActive())
        {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, cause.getMessage(), null);
        }
        else
        {
            getLogger().error(null, cause);
        }
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param request {@link FullHttpRequest}
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected void handleGet(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        final String resource = request.uri();

        Path path = this.fileRepository.createResourcePath(resource);

        // Erst im FileRepository suchen.
        if (!this.fileRepository.exist(resource))
        {
            // Dann in den HttpRepositories suchen.
            for (HttpRepository httpRepository : this.httpRepositories)
            {
                InputStream inputStream = null;

                try
                {
                    inputStream = httpRepository.getInputStream(resource);
                }
                catch (Exception ex)
                {
                    getLogger().warn(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }

                if (inputStream == null)
                {
                    continue;
                }

                getLogger().info("Download {}{}", httpRepository.getUri(), resource);
                Files.createDirectories(path.getParent());
                Files.copy(inputStream, path);
                inputStream.close();

                break;
            }
        }

        File file = path.toFile();
        RandomAccessFile raf = null;

        try
        {
            raf = new RandomAccessFile(file, "r");
        }
        catch (FileNotFoundException ex)
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND, ex.getMessage(), request);

            return;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.SERVER, "Maven-Proxy");
        HttpUtil.setContentLength(response, fileLength);

        // setContentTypeHeader(response, file);
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));

        // setDateAndCacheHeaders(response, file);

        if (!keepAlive)
        {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        else if (request.protocolVersion().equals(HttpVersion.HTTP_1_0))
        {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        ctx.write(response);

        // Write the content.
        ChannelFuture sendFileFuture = null;
        ChannelFuture lastContentFuture = null;

        if (ctx.pipeline().get(SslHandler.class) == null)
        {
            sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());

            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        else
        {
            sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise());

            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
            lastContentFuture = sendFileFuture;
        }

        sendFileFuture.addListener(new ChannelProgressiveFutureListener()
        {
            /**
             * @see io.netty.util.concurrent.GenericFutureListener#operationComplete(io.netty.util.concurrent.Future)
             */
            @Override
            public void operationComplete(final ChannelProgressiveFuture future)
            {
                getLogger().debug(future.channel() + " Transfer complete for " + request.uri());
            }

            /**
             * @see io.netty.util.concurrent.GenericProgressiveFutureListener#operationProgressed(io.netty.util.concurrent.ProgressiveFuture, long, long)
             */
            @Override
            public void operationProgressed(final ChannelProgressiveFuture future, final long progress, final long total)
            {
                if (total < 0)
                {
                    // total unknown
                    getLogger().debug(future.channel() + " Transfer progress: " + progress + " for " + request.uri());
                }
                else
                {
                    getLogger().debug(future.channel() + " Transfer progress: " + progress + " / " + total + " for " + request.uri());
                }
            }
        });

        // Decide whether to close the connection or not.
        if (!keepAlive)
        {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    // /**
    // * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
    // */
    // @Override
    // public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception
    // {
    // ctx.flush();
    //
    // super.channelReadComplete(ctx);
    // }

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param request {@link FullHttpRequest}
     * @throws Exception Falls was schief geht.
     */
    protected void handleHead(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        String resource = request.uri();

        // Erst im FileRepository suchen.
        boolean exist = this.fileRepository.exist(resource);

        if (!exist)
        {
            // Dann in den HttpRepositories suchen.
            for (HttpRepository httpRepository : this.httpRepositories)
            {
                try
                {
                    exist = httpRepository.exist(resource);
                }
                catch (Exception ex)
                {
                    getLogger().warn(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }

                if (exist)
                {
                    break;
                }
            }
        }

        HttpResponseStatus responseStatus = exist ? HttpResponseStatus.OK : HttpResponseStatus.NOT_FOUND;

        // HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
        response.headers().set(HttpHeaderNames.SERVER, "Maven-Proxy");

        // ctx.writeAndFlush(response);
        sendAndCleanupConnection(ctx, response, request);
    }

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param request {@link FullHttpRequest}
     * @throws Exception Falls was schief geht.
     */
    protected void handlePut(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, request.uri(), request);
    }

    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response and closes the connection after the response being sent.
     *
     * @param ctx {@link ChannelHandlerContext}
     * @param response {@link FullHttpResponse}
     * @param request {@link FullHttpRequest}
     */
    protected void sendAndCleanupConnection(final ChannelHandlerContext ctx, final FullHttpResponse response, final FullHttpRequest request)
    {
        final boolean keepAlive = request != null ? HttpUtil.isKeepAlive(request) : true;
        HttpUtil.setContentLength(response, response.content().readableBytes());

        if (!keepAlive)
        {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        else if ((request != null) && request.protocolVersion().equals(HttpVersion.HTTP_1_0))
        {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive)
        {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param status {@link HttpResponseStatus}
     * @param request {@link FullHttpRequest}
     * @param message String, kann null ein
     */
    protected void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status, final String message, final FullHttpRequest request)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP-Failure: ").append(status).append(HttpRepository.CRLF);

        if ((message != null) && !message.isBlank())
        {
            sb.append("Message: ").append(message).append(HttpRepository.CRLF);
        }

        getLogger().error(sb.toString());

        ByteBuf byteBuf = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        // ctx.writeAndFlush(response);
        sendAndCleanupConnection(ctx, response, request);
    }
}
