/*
 * Copyright 2012 The Netty Project The Netty Project licenses this file to you under the Apache License, version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package de.freese.maven.proxy.netty_fileserver;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.activation.MimetypesFileTypeMap;
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
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * A simple handler that serves incoming HTTP requests to send their respective HTTP responses. It also implements {@code 'If-Modified-Since'} header to take
 * advantage of browser cache, as described in <a href="http://tools.ietf.org/html/rfc2616#section-14.25">RFC 2616</a>.
 * <h3>How Browser Caching Works</h3> Web browser caching works with HTTP headers as illustrated by the following sample:
 * <ol>
 * <li>Request #1 returns the content of {@code /file1.txt}.</li>
 * <li>Contents of {@code /file1.txt} is cached by the browser.</li>
 * <li>Request #2 for {@code /file1.txt} does not return the contents of the file again. Rather, a 304 Not Modified is returned. This tells the browser to use
 * the contents stored in its cache.</li>
 * <li>The server knows the file has not been modified because the {@code If-Modified-Since} date is the same as the file's last modified date.</li>
 * </ol>
 *
 * <pre>
 * Request #1 Headers
 * ===================
 * GET /file1.txt HTTP/1.1
 *
 * Response #1 Headers
 * ===================
 * HTTP/1.1 200 OK
 * Date:               Tue, 01 Mar 2011 22:44:26 GMT
 * Last-Modified:      Wed, 30 Jun 2010 21:36:48 GMT
 * Expires:            Tue, 01 Mar 2012 22:44:26 GMT
 * Cache-Control:      private, max-age=31536000
 *
 * Request #2 Headers
 * ===================
 * GET /file1.txt HTTP/1.1
 * If-Modified-Since:  Wed, 30 Jun 2010 21:36:48 GMT
 *
 * Response #2 Headers
 * ===================
 * HTTP/1.1 304 Not Modified
 * Date:               Tue, 01 Mar 2011 22:44:28 GMT
 * </pre>
 */
public class HttpStaticFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{
    /**
     *
     */
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");

    /**
     *
     */
    public static final int HTTP_CACHE_SECONDS = 60;

    /**
     *
     */
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     *
     */
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    /**
     *
     */
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    /**
     * @param uri String
     * @return String
     */
    private static String sanitizeUri(String uri)
    {
        // Decode the path.
        try
        {
            uri = URLDecoder.decode(uri, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new Error(e);
        }

        if (uri.isEmpty() || (uri.charAt(0) != '/'))
        {
            return null;
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || (uri.charAt(0) == '.') || (uri.charAt(uri.length() - 1) == '.')
                || INSECURE_URI.matcher(uri).matches())
        {
            return null;
        }

        // Convert to absolute path.
        return SystemPropertyUtil.get("user.dir") + File.separator + uri;
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response {@link HttpResponse}
     * @param file {@link File}
     */
    private static void setContentTypeHeader(final HttpResponse response, final File file)
    {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response {@link HttpResponse}
     * @param fileToCache {@link File}
     */
    private static void setDateAndCacheHeaders(final HttpResponse response, final File fileToCache)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response {@link FullHttpResponse}
     */
    private static void setDateHeader(final FullHttpResponse response)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }

    /**
     *
     */
    private FullHttpRequest request = null;

    /**
     * Erstellt ein neues {@link HttpStaticFileServerHandler} Object.
     */
    public HttpStaticFileServerHandler()
    {
        super();
    }

    /**
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @SuppressWarnings("resource")
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception
    {
        this.request = request;

        if (!request.decoderResult().isSuccess())
        {
            sendError(ctx, BAD_REQUEST);

            return;
        }

        if (!GET.equals(request.method()))
        {
            sendError(ctx, METHOD_NOT_ALLOWED);

            return;
        }

        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        final String uri = request.uri();
        final String path = sanitizeUri(uri);

        if (path == null)
        {
            sendError(ctx, FORBIDDEN);

            return;
        }

        File file = new File(path);

        if (file.isHidden() || !file.exists())
        {
            sendError(ctx, NOT_FOUND);

            return;
        }

        if (file.isDirectory())
        {
            if (uri.endsWith("/"))
            {
                sendListing(ctx, file, uri);
            }
            else
            {
                sendRedirect(ctx, uri + '/');
            }

            return;
        }

        if (!file.isFile())
        {
            sendError(ctx, FORBIDDEN);

            return;
        }

        // Cache Validation
        String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);

        if ((ifModifiedSince != null) && !ifModifiedSince.isEmpty())
        {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;

            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds)
            {
                sendNotModified(ctx);
                return;
            }
        }

        RandomAccessFile raf = null;

        try
        {
            raf = new RandomAccessFile(file, "r");
        }
        catch (FileNotFoundException ignore)
        {
            sendError(ctx, NOT_FOUND);

            return;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        setDateAndCacheHeaders(response, file);

        if (!keepAlive)
        {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        else if (request.protocolVersion().equals(HTTP_1_0))
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
                System.err.println(future.channel() + " Transfer complete.");
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
                    System.err.println(future.channel() + " Transfer progress: " + progress);
                }
                else
                {
                    System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
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
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
    {
        cause.printStackTrace();

        if (ctx.channel().isActive())
        {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response and closes the connection after the response being sent.
     *
     * @param ctx {@link ChannelHandlerContext}
     * @param response {@link FullHttpResponse}
     */
    private void sendAndCleanupConnection(final ChannelHandlerContext ctx, final FullHttpResponse response)
    {
        final FullHttpRequest request = this.request;
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setContentLength(response, response.content().readableBytes());

        if (!keepAlive)
        {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        else if (request.protocolVersion().equals(HTTP_1_0))
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
     */
    private void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        sendAndCleanupConnection(ctx, response);
    }

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param dir {@link File}
     * @param dirPath String
     */
    private void sendListing(final ChannelHandlerContext ctx, final File dir, final String dirPath)
    {
        // @formatter:off
        StringBuilder buf = new StringBuilder()
                .append("<!DOCTYPE html>\r\n")
                .append("<html><head><meta charset='utf-8' /><title>")
                .append("Listing of: ")
                .append(dirPath)
                .append("</title></head><body>\r\n")
                .append("<h3>Listing of: ")
                .append(dirPath)
                .append("</h3>\r\n")
                .append("<ul>")
                .append("<li><a href=\"../\">..</a></li>\r\n");
        // @formatter:on

        for (File f : dir.listFiles())
        {
            if (f.isHidden() || !f.canRead())
            {
                continue;
            }

            String name = f.getName();

            if (!ALLOWED_FILE_NAME.matcher(name).matches())
            {
                continue;
            }

            buf.append("<li><a href=\"").append(name).append("\">").append(name).append("</a></li>\r\n");
        }

        buf.append("</ul></body></html>\r\n");

        ByteBuf buffer = ctx.alloc().buffer(buf.length());
        buffer.writeCharSequence(buf.toString(), CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        sendAndCleanupConnection(ctx, response);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx {@link ChannelHandlerContext}
     */
    private void sendNotModified(final ChannelHandlerContext ctx)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
        setDateHeader(response);

        sendAndCleanupConnection(ctx, response);
    }

    /**
     * @param ctx {@link ChannelHandlerContext}
     * @param newUri String
     */
    private void sendRedirect(final ChannelHandlerContext ctx, final String newUri)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND, Unpooled.EMPTY_BUFFER);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);

        sendAndCleanupConnection(ctx, response);
    }
}