// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.littlemina.http;

import java.util.Map;
import java.util.Map.Entry;

/**
 * A HTTP request message.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 576402 $, $Date: 2007-09-17 21:37:27 +0900 (?, 17 9? 2007) $
 */
public class HttpRequestMessage
{
	/**
	 * @param s String[]
	 * @param separator char
	 * @return String
	 */
	public static String arrayToString(final String[] s, final char separator)
	{
		if ((s == null) || (s.length == 0))
		{
			return "";
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < s.length; i++)
		{
			if (i > 0)
			{
				builder.append(separator);
			}

			builder.append(s[i]);
		}

		return builder.toString();
	}

	/**
	 *
	 */
	private Map<String, String[]> headers = null;

	/**
	 * Erstellt ein neues {@link HttpRequestMessage} Object.
	 */
	public HttpRequestMessage()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getContext()
	{
		String[] context = this.headers.get("Context");

		return context == null ? "" : context[0];
	}

	/**
	 * @param name String
	 * @return String[]
	 */
	public String[] getHeader(final String name)
	{
		return this.headers.get(name);
	}

	/**
	 * @return {@link Map}
	 */
	public Map<String, String[]> getHeaders()
	{
		return this.headers;
	}

	/**
	 * @param name String
	 * @return String
	 */
	public String getParameter(final String name)
	{
		String[] param = this.headers.get("@".concat(name));

		return param == null ? "" : param[0];
	}

	/**
	 * @param name String
	 * @return String[]
	 */
	public String[] getParameters(final String name)
	{
		String[] param = this.headers.get("@".concat(name));

		return param == null ? new String[] {} : param;
	}

	/**
	 * @param headers {@link Map}
	 */
	public void setHeaders(final Map<String, String[]> headers)
	{
		this.headers = headers;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		for (Entry<String, String[]> e : this.headers.entrySet())
		{
			builder.append(e.getKey() + " : " + arrayToString(e.getValue(), ',') + "\n");
		}

		return builder.toString();
	}
}
