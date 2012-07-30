/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.model;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enthält die Daten des HTTP Headers.
 * 
 * @author Thomas Freese
 */
public class HTTPHeaderOld
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HTTPHeaderOld.class);

	/**
	 * 
	 */
	public static final String CONTEXT = "Context";

	/**
	 * 
	 */
	public static final String URI = "URI";

	/**
	 * 
	 */
	public static final String METHOD = "Method";

	/**
	 * 
	 */
	public static final String PROTOCOL = "Protocol";

	/**
	 * @param reader {@link BufferedReader}
	 * @return {@link HTTPHeaderOld}
	 * @throws Exception Falls was schief geht.
	 */
	public static HTTPHeaderOld parseHeader(final BufferedReader reader) throws Exception
	{
		HTTPHeaderOld header = new HTTPHeaderOld();
		Map<String, String[]> map = header.headerMap;

		// Get request URL.
		String line = reader.readLine();

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(line);
		}

		String[] url = line.split(" ");

		if (url.length < 3)
		{
			return header;
		}

		map.put(URI, new String[]
		{
			line
		});
		map.put(METHOD, new String[]
		{
			url[0].toUpperCase()
		});
		map.put(CONTEXT, new String[]
		{
			// url[1].substring(1)
				url[1]
			});
		map.put(PROTOCOL, new String[]
		{
			url[2]
		});

		// Read header
		while (((line = reader.readLine()) != null) && (line.length() > 0))
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug(line);
			}

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

			if (reader.read(buf) == len)
			{
				line = String.copyValueOf(buf);
			}
		}
		else if (url[0].equalsIgnoreCase("GET"))
		{
			int idx = url[1].indexOf('?');

			if (idx != -1)
			{
				map.put(CONTEXT, new String[]
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

		return header;
	}

	/**
	 * 
	 */
	private final Map<String, String[]> headerMap;

	/**
	 * Erstellt ein neues {@link HTTPHeaderOld} Object.
	 */
	public HTTPHeaderOld()
	{
		super();

		this.headerMap = new LinkedHashMap<>();
	}

	/**
	 * Erstellt ein neues {@link HTTPHeaderOld} Object.
	 * 
	 * @param src {@link HTTPHeaderOld}, kopiert alle Inhalte.
	 */
	public HTTPHeaderOld(final HTTPHeaderOld src)
	{
		this();

		this.headerMap.putAll(src.headerMap);
	}

	/**
	 * @return String
	 */
	public String getContext()
	{
		String[] value = getHeaders().get(CONTEXT);

		return (value == null) || (value.length == 0) ? null : value[0];
	}

	/**
	 * @return Map<String,String[]>
	 */
	public Map<String, String[]> getHeaders()
	{
		return this.headerMap;
	}

	/**
	 * @return String
	 */
	public String getProtocol()
	{
		String[] value = getHeaders().get(PROTOCOL);

		return (value == null) || (value.length == 0) ? null : value[0];
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		Iterator<Entry<String, String[]>> iterator = this.headerMap.entrySet().iterator();

		while (iterator.hasNext())
		{
			Entry<String, String[]> entry = iterator.next();

			String key = entry.getKey();
			String[] values = entry.getValue();

			builder.append(key);
			builder.append("=");
			builder.append(Arrays.toString(values));

			if (iterator.hasNext())
			{
				builder.append("; ");
			}
		}

		return builder.toString();
	}
}
