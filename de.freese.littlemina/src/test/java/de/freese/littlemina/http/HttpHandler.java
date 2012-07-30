// Created: 12.01.2010
/**
 * 12.01.2010
 */
package de.freese.littlemina.http;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.buffer.IoBuffer;
import de.freese.littlemina.core.session.IoSession;

/**
 * {@link IoHandler} fuer das HTTP-Protokoll.
 * 
 * @author Thomas Freese
 */
public class HttpHandler implements IoHandler
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);

	// /**
	// * The end of line character sequence used by most IETF protocols. That is a carriage return
	// * followed by a newline: "\r\n" (NETASCII_EOL)
	// */
	// private static final String CRLF = "\r\n";

	/**
	 * The end of line character sequence used by most IETF protocols. That is a carriage return
	 * followed by a newline: "\r\n" (NETASCII_EOL)
	 */
	private static final byte[] CRLF = new byte[]
	{
			0x0D, 0x0A
	};

	/**
	 *
	 */
	private final Charset charset;

	/**
	 *
	 */
	private final CharsetDecoder decoder;

	/**
	 *
	 */
	private final CharsetEncoder encoder;

	/**
	 * Erstellt ein neues {@link HttpHandler} Object.
	 */
	public HttpHandler()
	{
		super();

		this.charset = Charset.forName("ISO-8859-1");
		this.decoder = this.charset.newDecoder();
		this.encoder = this.charset.newEncoder();
	}

	/**
	 * @see de.freese.littlemina.core.IoHandler#messageReceived(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void messageReceived(final IoSession session) throws Exception
	{
		IoBuffer inputBuffer = session.getBuffer();
		HttpRequestMessage httpRequestMessage =
				parseHttpRequest(inputBuffer.getString(this.decoder));
		LOGGER.info(httpRequestMessage.toString());

		// Response
		HttpResponseMessage response = new HttpResponseMessage();
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		response.setContentType("text/html");
		response.appendBody("<html>");
		response.appendBody("<head></head>");
		response.appendBody("<body>");
		response.appendBody("TAESCHDT<br>" + new Date().toString());
		response.appendBody("</body>");
		response.appendBody("</html>");

		IoBuffer buffer = IoBuffer.allocate(1);
		buffer.setAutoExpand(true);

		// Header Response

		buffer.putString("HTTP/1.1 ", this.encoder);
		buffer.putString(String.valueOf(response.getResponseCode()), this.encoder);

		switch (response.getResponseCode())
		{
			case HttpResponseMessage.HTTP_STATUS_SUCCESS:
				buffer.putString(" OK", this.encoder);
				break;
			case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
				buffer.putString(" Not Found", this.encoder);
				break;
		}

		buffer.put(CRLF);

		for (Entry<String, String> entry : response.getHeaders().entrySet())
		{
			buffer.putString(entry.getKey(), this.encoder);
			buffer.putString(": ", this.encoder);
			buffer.putString(entry.getValue(), this.encoder);
			buffer.put(CRLF);
		}

		// Body Response
		buffer.putString("Content-Length: ", this.encoder);
		buffer.putString(String.valueOf(response.getBodyLength()), this.encoder);
		buffer.put(CRLF);
		buffer.put(CRLF);
		buffer.put(response.getBody());

		buffer.flip();

		session.write(buffer);

		session.close();
	}

	/**
	 * @param request String
	 * @return {@link HttpRequestMessage}
	 * @throws Exception Falls was schief geht.
	 */
	private HttpRequestMessage parseHttpRequest(final String request) throws Exception
	{
		HttpRequestMessage httpRequestMessage = new HttpRequestMessage();

		httpRequestMessage.setHeaders(parseRequest(new StringReader(request)));

		return httpRequestMessage;
	}

	/**
	 * @param is {@link Reader}
	 * @return {@link Map}
	 * @throws Exception Falls was schief geht.
	 */
	private Map<String, String[]> parseRequest(final Reader is) throws Exception
	{
		Map<String, String[]> map = new HashMap<String, String[]>();
		BufferedReader rdr = new BufferedReader(is);

		// Get request URL.
		String line = rdr.readLine();
		String[] url = line.split(" ");

		if (url.length < 3)
		{
			return map;
		}

		map.put("URI", new String[]
		{
			line
		});
		map.put("Method", new String[]
		{
			url[0].toUpperCase()
		});
		map.put("Context", new String[]
		{
			url[1].substring(1)
		});
		map.put("Protocol", new String[]
		{
			url[2]
		});

		// Read header
		while (((line = rdr.readLine()) != null) && (line.length() > 0))
		{
			String[] tokens = line.split(": ");
			map.put(tokens[0], new String[]
			{
				tokens[1]
			});
		}

		// If method 'POST' then read Content-Length worth of data
		if (url[0].equalsIgnoreCase("POST"))
		{
			int len = Integer.parseInt(map.get("Content-Length")[0]);
			char[] buf = new char[len];

			if (rdr.read(buf) == len)
			{
				line = String.copyValueOf(buf);
			}
		}
		else if (url[0].equalsIgnoreCase("GET"))
		{
			int idx = url[1].indexOf('?');

			if (idx != -1)
			{
				map.put("Context", new String[]
				{
					url[1].substring(1, idx)
				});

				line = url[1].substring(idx + 1);
			}
			else
			{
				line = null;
			}
		}

		if (line != null)
		{
			String[] match = line.split("\\&");

			for (String element : match)
			{
				String[] params = new String[1];
				String[] tokens = element.split("=");

				switch (tokens.length)
				{
					case 0:
						map.put("@".concat(element), new String[] {});
						break;
					case 1:
						map.put("@".concat(tokens[0]), new String[] {});
						break;
					default:
						String name = "@".concat(tokens[0]);

						if (map.containsKey(name))
						{
							params = map.get(name);
							String[] tmp = new String[params.length + 1];

							for (int j = 0; j < params.length; j++)
							{
								tmp[j] = params[j];
							}

							params = null;
							params = tmp;
						}
						params[params.length - 1] = tokens[1].trim();
						map.put(name, params);
				}
			}
		}

		return map;
	}

	/**
	 * @see de.freese.littlemina.core.IoHandler#sessionClosed(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(final IoSession session) throws Exception
	{
		LOGGER.info(session.toString());
	}

	/**
	 * @see de.freese.littlemina.core.IoHandler#sessionOpened(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(final IoSession session) throws Exception
	{
		LOGGER.info(session.toString());
	}
}
