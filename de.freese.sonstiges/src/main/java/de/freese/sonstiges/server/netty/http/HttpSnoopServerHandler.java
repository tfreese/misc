/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty.http;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

/**
 * @author Thomas Freese
 */
public class HttpSnoopServerHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * Buffer that stores the response content
	 */
	private final StringBuilder buf = new StringBuilder();

	/**
	   * 
	   */
	private boolean readingChunks = false;

	/**
   * 
   */
	private HttpRequest request = null;

	/**
	 * Erstellt ein neues {@link HttpSnoopServerHandler} Object.
	 */
	public HttpSnoopServerHandler()
	{
		super();
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e)
		throws Exception
	{
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
		throws Exception
	{
		if (!this.readingChunks)
		{
			HttpRequest request = this.request = (HttpRequest) e.getMessage();

			if (HttpHeaders.is100ContinueExpected(request))
			{
				send100Continue(e);
			}

			this.buf.setLength(0);
			this.buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
			this.buf.append("===================================\r\n");

			this.buf.append("DATE: " + new Date().toString() + "\r\n");
			this.buf.append("VERSION: " + request.getProtocolVersion() + "\r\n");
			this.buf.append("HOSTNAME: " + HttpHeaders.getHost(request, "unknown") + "\r\n");
			this.buf.append("REQUEST_URI: " + request.getUri() + "\r\n\r\n");

			for (Map.Entry<String, String> h : request.getHeaders())
			{
				this.buf.append("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
			}

			this.buf.append("\r\n");

			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
			Map<String, List<String>> params = queryStringDecoder.getParameters();

			if (!params.isEmpty())
			{
				for (Entry<String, List<String>> p : params.entrySet())
				{
					String key = p.getKey();
					List<String> vals = p.getValue();

					for (String val : vals)
					{
						this.buf.append("PARAM: " + key + " = " + val + "\r\n");
					}
				}

				this.buf.append("\r\n");
			}

			if (request.isChunked())
			{
				this.readingChunks = true;
			}
			else
			{
				ChannelBuffer content = request.getContent();

				if (content.readable())
				{
					this.buf.append("CONTENT: " + content.toString(CharsetUtil.UTF_8) + "\r\n");
				}

				writeResponse(e);
			}
		}
		else
		{
			HttpChunk chunk = (HttpChunk) e.getMessage();

			if (chunk.isLast())
			{
				this.readingChunks = false;
				this.buf.append("END OF CONTENT\r\n");

				HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;

				if (!trailer.getHeaderNames().isEmpty())
				{
					this.buf.append("\r\n");

					for (String name : trailer.getHeaderNames())
					{
						for (String value : trailer.getHeaders(name))
						{
							this.buf.append("TRAILING HEADER: " + name + " = " + value + "\r\n");
						}
					}

					this.buf.append("\r\n");
				}

				writeResponse(e);
			}
			else
			{
				this.buf.append("CHUNK: " + chunk.getContent().toString(CharsetUtil.UTF_8) + "\r\n");
			}
		}
	}

	/**
	 * @param e {@link MessageEvent}
	 */
	private void send100Continue(final MessageEvent e)
	{
		HttpResponse response =
				new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
		e.getChannel().write(response);
	}

	/**
	 * @param e {@link MessageEvent}
	 */
	private void writeResponse(final MessageEvent e)
	{
		// Decide whether to close the connection or not.
		boolean keepAlive = HttpHeaders.isKeepAlive(this.request);

		// Build the response object.
		HttpResponse response =
				new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.setContent(ChannelBuffers.copiedBuffer(this.buf.toString(), CharsetUtil.UTF_8));
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

		if (keepAlive)
		{
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
					Integer.valueOf(response.getContent().readableBytes()));
			// Add keep alive header as per:
			// - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}

		// Encode the cookie.
		String cookieString = this.request.getHeader(HttpHeaders.Names.COOKIE);

		if (cookieString != null)
		{
			CookieDecoder cookieDecoder = new CookieDecoder();
			Set<Cookie> cookies = cookieDecoder.decode(cookieString);

			if (!cookies.isEmpty())
			{
				// Reset the cookies if necessary.
				CookieEncoder cookieEncoder = new CookieEncoder(true);
				for (Cookie cookie : cookies)
				{
					cookieEncoder.addCookie(cookie);
					response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
				}
			}
		}
		else
		{
			// Browser sent no cookie. Add some.
			CookieEncoder cookieEncoder = new CookieEncoder(true);
			cookieEncoder.addCookie("key1", "value1");
			response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
			cookieEncoder.addCookie("key2", "value2");
			response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder.encode());
		}

		// Write the response.
		ChannelFuture future = e.getChannel().write(response);

		// Close the non-keep-alive connection after the write operation is done.
		if (!keepAlive)
		{
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
