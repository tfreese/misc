// Created: 27.03.2018
package de.freese.maven.proxy.netty.handler;

import java.io.InputStream;
import java.util.Objects;
import javax.activation.MimetypesFileTypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.blobstore.Blob;
import de.freese.maven.proxy.blobstore.BlobId;
import de.freese.maven.proxy.blobstore.BlobStore;
import de.freese.maven.proxy.repository.RemoteRepository;
import de.freese.maven.proxy.repository.http.HttpRepository;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
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
import io.netty.handler.stream.ChunkedStream;
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
    private final BlobStore blobStore;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    /**
    *
    */
    private final RemoteRepository remoteRepository;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenRequestHandler}.
     *
     * @param blobStore {@link BlobStore}
     * @param remoteRepository {@link RemoteRepository}
     */
    public NettyMavenRequestHandler(final BlobStore blobStore, final RemoteRepository remoteRepository)
    {
        super();

        this.blobStore = Objects.requireNonNull(blobStore, "blobStore required");
        this.remoteRepository = Objects.requireNonNull(remoteRepository, "repository required");
    }

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        if ("/".equals(request.uri()))
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND, "File not found: /", request);

            return;
        }

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
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, request.method() + "; " + request.uri(), request);
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
        String resource = request.uri();

        BlobId id = new BlobId(resource);
        Blob blob = null;

        // Erst im FileRepository suchen.
        if (!this.blobStore.exists(id))
        {
            // Dann in den RemoteRepositories suchen.
            InputStream inputStream = this.remoteRepository.getInputStream(id.asUniqueString());

            if (inputStream != null)
            {
                getLogger().info("Download {}", id.asUniqueString());
                blob = this.blobStore.create(id, inputStream);
                // getLogger().info("Download complete " + id.asUniqueString());
                inputStream.close();
            }
        }
        else
        {
            blob = this.blobStore.get(id);
        }

        if (blob == null)
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND, "File not found: " + id.asUniqueString(), request);

            return;
        }

        long fileLength = blob.getLength();

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.SERVER, "Maven-Proxy");
        HttpUtil.setContentLength(response, fileLength);

        // setContentTypeHeader(response, file);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, this.mimeTypesMap.getContentType(blob.getSimpleName()));

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
            sendFileFuture = ctx.write(new HttpChunkedInput(new ChunkedStream(blob.getInputStream())), ctx.newProgressivePromise());

            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }
        else
        {
            sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedStream(blob.getInputStream())), ctx.newProgressivePromise());

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
                // getLogger().debug(future.channel() + " Transfer complete: " + request.uri());
                getLogger().debug("Transfer complete: " + request.uri());
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
                    getLogger().debug(future.channel() + " Transfer progress: " + progress + " : " + request.uri());
                }
                else
                {
                    getLogger().debug(future.channel() + " Transfer progress: " + progress + " / " + total + " : " + request.uri());
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

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param request {@link FullHttpRequest}
     * @throws Exception Falls was schief geht.
     */
    protected void handleHead(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        String resource = request.uri();
        BlobId id = new BlobId(resource);

        // Erst im FileRepository suchen.
        boolean exist = this.blobStore.exists(id);

        if (!exist)
        {
            // Dann in den RemoteRepositories suchen.
            try
            {
                exist = this.remoteRepository.exist(resource);
            }
            catch (Exception ex)
            {
                getLogger().warn(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }

        HttpResponseStatus responseStatus = exist ? HttpResponseStatus.OK : HttpResponseStatus.NOT_FOUND;

        // HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus);

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

        response.headers().set(HttpHeaderNames.SERVER, "Maven-Proxy");
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
        getLogger().error("HTTP-Failure: {}; Message: {}", status, message);

        StringBuilder sb = new StringBuilder();
        sb.append("HTTP-Failure: ").append(status).append(HttpRepository.CRLF);

        if ((message != null) && !message.isBlank())
        {
            sb.append("Message: ").append(message).append(HttpRepository.CRLF);
        }

        ByteBuf byteBuf = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        sendAndCleanupConnection(ctx, response, request);
    }
}
