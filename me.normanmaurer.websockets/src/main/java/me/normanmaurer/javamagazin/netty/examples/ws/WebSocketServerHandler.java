package me.normanmaurer.javamagazin.netty.examples.ws;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;

/**
 * {@link SimpleChannelUpstreamHandler} implementation der den WebSocket Handshake durchfuert sowie
 * das Abhandeln von {@link HttpRequest}'s.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class WebSocketServerHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * 
	 */
	private static final String WEBSOCKET_PATH = "/ws";

	/**
	 * @param req {@link HttpRequest}
	 * @return String
	 */
	private static String getWebSocketLocation(final HttpRequest req)
	{
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}

	/**
	 * @param ctx {@link ChannelHandlerContext}
	 * @param req {@link HttpRequest}
	 * @param res {@link HttpResponse}
	 */
	private static void sendHttpResponse(final ChannelHandlerContext ctx, final HttpRequest req,
											final HttpResponse res)
	{
		// Erzeugen einer “Error-Page” wenn Status-Code nicht OK (200) ist.
		if (res.getStatus().getCode() != HttpResponseStatus.OK.getCode())
		{
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(),
					CharsetUtil.UTF_8));
			HttpHeaders.setContentLength(res, res.getContent().readableBytes());
		}

		// Senden der HttpResponse
		ChannelFuture f = ctx.getChannel().write(res);

		if (!HttpHeaders.isKeepAlive(req) || (res.getStatus().getCode() != 200))
		{
			// Falls der HttpRequest nicht den Keep-Alive Header enthielt
			// oder der Status-Code nicht 200 war, wird der Channel nach dem
			// Senden der Nachricht geschlossen.
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * 
	 */
	private final ChannelGroup wsGroup;

	/**
	 * Erstellt ein neues {@link WebSocketServerHandler} Object.
	 * 
	 * @param wsGroup {@link ChannelGroup}
	 */
	public WebSocketServerHandler(final ChannelGroup wsGroup)
	{
		super();

		this.wsGroup = wsGroup;
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e)
		throws Exception
	{
		// Stacktrace nach STDOUT ausgeben und Channel schliessen.
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	/**
	 * @param ctx {@link ChannelHandlerContext}
	 * @param req {@link HttpRequest}
	 * @throws Exception Falls was schief geht.
	 */
	private void handleHttpRequest(final ChannelHandlerContext ctx, final HttpRequest req)
		throws Exception
	{
		// Ueberpruefen ob der Request ein GET ist oder nicht, wenn nicht
		// kann dieser nicht bearbeitet werden. Somit senden eines 403 Status-Code‘s.
		if (req.getMethod() != HttpMethod.GET)
		{
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1,
					HttpResponseStatus.FORBIDDEN));
			return;
		}

		// Senden der Index-Seite
		if (req.getUri().equals("/"))
		{
			HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

			ChannelBuffer content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));

			res.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
			HttpHeaders.setContentLength(res, content.readableBytes());

			res.setContent(content);
			sendHttpResponse(ctx, req, res);
		}
		else if (req.getUri().startsWith(WEBSOCKET_PATH))
		{
			// Handshake
			WebSocketServerHandshakerFactory wsFactory =
					new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
			WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);

			// Ueberpruefen ob ein geeigneter WebSocketServerHandshaker fuer den Request
			// gefunden worden konnte. Wenn nicht wird der Client darueber informiert.
			if (handshaker == null)
			{
				wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
			}
			else
			{
				// Fuehre den Handshake
				handshaker.handshake(ctx.getChannel(), req).addListener(new ChannelFutureListener()
				{
					/**
					 * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
					 */
					@Override
					public void operationComplete(final ChannelFuture future) throws Exception
					{
						if (future.isSuccess())
						{
							// Handshake war erfolgreich. Fuege Channel in die
							// ChannelGroup hinzu um so auch UDP Nachrichten
							// zu Empfangen
							WebSocketServerHandler.this.wsGroup.add(future.getChannel());
						}
						else
						{
							// Handshake hat nicht geklappt. Feuere einen
							// exceptionCaught event
							Channels.fireExceptionCaught(future.getChannel(), future.getCause());
						}
					}
				});
			}
		}
		else
		{
			// Sende ein 404
			HttpResponse res =
					new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
			sendHttpResponse(ctx, req, res);
		}
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
		throws Exception
	{
		Object msg = e.getMessage();

		if (msg instanceof HttpRequest)
		{
			handleHttpRequest(ctx, (HttpRequest) msg);
		}
		else
		{
			// Ungueltige Nachricht, somit schliessen des Channel's
			ctx.getChannel().close();
		}
	}
}
