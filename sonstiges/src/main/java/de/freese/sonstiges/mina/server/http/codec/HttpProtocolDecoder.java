package de.freese.sonstiges.mina.server.http.codec;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.mina.server.http.HttpRequestMessage;

/**
 * @author Thomas Freese
 */
public class HttpProtocolDecoder implements ProtocolDecoder
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpProtocolDecoder.class);

	/**
	 * 
	 */
	private final CharsetDecoder decoder;

	/**
	 * Erstellt ein neues {@link HttpProtocolDecoder} Object.
	 * 
	 * @param decoder {@link CharsetDecoder}
	 */
	public HttpProtocolDecoder(final CharsetDecoder decoder)
	{
		super();

		this.decoder = decoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolDecoder#decode(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.core.buffer.IoBuffer,
	 *      org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	public void decode(final IoSession iosession, final IoBuffer iobuffer,
						final ProtocolDecoderOutput protocoldecoderoutput) throws Exception
	{
		// LOGGER.info(iosession.toString());
		//
		// CharBuffer charBuffer = this.decoder.decode(iobuffer.buf());
		//
		// protocoldecoderoutput.write(charBuffer.toString());

		HttpRequestMessage requestMessage = decodeBody(iobuffer);

		protocoldecoderoutput.write(requestMessage);
	}

	/**
	 * @param in {@link IoBuffer}
	 * @return {@link HttpRequestMessage}
	 * @throws Exception Falls was schief geht.
	 */
	private HttpRequestMessage decodeBody(final IoBuffer in) throws Exception
	{
		HttpRequestMessage request = new HttpRequestMessage();

		request.setHeaders(parseRequest(new StringReader(in.getString(this.decoder))));

		return request;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolDecoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(final IoSession iosession) throws Exception
	{
		// LOGGER.info(iosession.toString());
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolDecoder#finishDecode(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	public void finishDecode(final IoSession iosession,
								final ProtocolDecoderOutput protocoldecoderoutput) throws Exception
	{
		// LOGGER.info(iosession.toString());
	}

	/**
	 * @param is {@link Reader}
	 * @return {@link Map}
	 * @throws Exception Falls was schief geht.
	 */
	private Map<String, String[]> parseRequest(final Reader is) throws Exception
	{
		Map<String, String[]> map = new HashMap<>();
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
}
