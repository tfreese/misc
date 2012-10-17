/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

/**
 * @author Thomas Freese
 */
public class HttpSnoopClientHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * 
	 */
	private boolean readingChunks = false;

	/**
	 * Erstellt ein neues {@link HttpSnoopClientHandler} Object.
	 */
	public HttpSnoopClientHandler()
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
			HttpResponse response = (HttpResponse) e.getMessage();

			System.out.println("STATUS: " + response.getStatus());
			System.out.println("VERSION: " + response.getProtocolVersion());
			System.out.println();

			if (!response.getHeaderNames().isEmpty())
			{
				for (String name : response.getHeaderNames())
				{
					for (String value : response.getHeaders(name))
					{
						System.out.println("HEADER: " + name + " = " + value);
					}
				}

				System.out.println();
			}

			if (response.isChunked())
			{
				this.readingChunks = true;
				System.out.println("CHUNKED CONTENT {");
			}
			else
			{
				ChannelBuffer content = response.getContent();
				if (content.readable())
				{
					System.out.println("CONTENT {");
					System.out.println(content.toString(CharsetUtil.UTF_8));
					System.out.println("} END OF CONTENT");
				}
			}
		}
		else
		{
			HttpChunk chunk = (HttpChunk) e.getMessage();

			if (chunk.isLast())
			{
				this.readingChunks = false;
				System.out.println("} END OF CHUNKED CONTENT");
			}
			else
			{
				System.out.print(chunk.getContent().toString(CharsetUtil.UTF_8));
				System.out.flush();
			}
		}
	}
}
