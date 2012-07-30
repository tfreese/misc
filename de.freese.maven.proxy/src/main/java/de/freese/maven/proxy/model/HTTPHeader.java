/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.model;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enthält die Daten des HTTP Headers.<br>
 * 
 * <pre>
 * 	 HTTP/1.1 200 OK
 * 	 Server: Artifactory/2.4.2 
 * 	 Content-Length: 144
 * 	 Content-Type: application/xml
 * 	 Last-Modified: Wed, 28 Dec 2011 10:24:17 GMT
 * 	 ETag: dec1ee1f8b021c38414e8d05175261c3d826d413
 * 	 X-Checksum-Sha1: dec1ee1f8b021c38414e8d05175261c3d826d413
 * 	 X-Checksum-Md5: 0d2e440c64465bcc5cc3177c75776e72
 * 	
 * 	 HTTP/1.1 200 OK
 * 	 Server: Artifactory/2.4.2
 * 	 Content-Length: 170296
 * 	 Content-Type: application/java-archive
 * 	 Last-Modified: Wed, 12 Oct 2011 15:17:59 GMT
 * 	 ETag: 11619aad3676b9e877b783babe9d4a291c7aea45
 * 	 X-Checksum-Sha1: 11619aad3676b9e877b783babe9d4a291c7aea45
 * 	 X-Checksum-Md5: 8e055e568b029fae070b1c0eeaaa810d
 * 
 * 	 GET /artifactory/remote-repos/de/freese/base/0.0.1-SNAPSHOT/maven-metadata.xml HTTP/1.1
 * 	 Accept-Encoding: gzip
 * 	 Pragma: no-cache
 * 	 User-Agent: Apache-Maven/3.0.3 (Java 1.7.0_01; Windows 7 6.1)
 * 	 Host: localhost:8088
 * 	 Accept: text/html, image/gif, image/jpeg, *; q=.2, *\/*; q=.2
 * 	 Connection: keep-alive
 * 
 * 	 HTTP/1.1 404 Maven metadata not found for 'de/freese/base/0.0.1-SNAPSHOT/maven-metadata.xml'.
 * 	 Server: Artifactory/2.4.2
 * 	 Content-Type: text/html;charset=ISO-8859-1
 * 	 Cache-Control: must-revalidate,no-cache,no-store
 * 	 Content-Length: 1580
 * </pre>
 * 
 * @author Thomas Freese
 */
public class HTTPHeader
{
	/**
	 * 
	 */
	private static final String CONNECTION = "Connection";

	/**
	 * 
	 */
	private static final String CONTENT_LENGTH = "Content-Length";

	/**
	 * 
	 */
	private static final String HOST = "Host";

	/**
	 * 
	 */
	public final static int HTTP_NOT_FOUND = 404;

	/**
	 * 
	 */
	public final static int HTTP_OK = 200;

	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HTTPHeader.class);

	/**
	 * 
	 */
	private static final String SERVER = "Server";

	/**
	 * 
	 */
	private static final String USER_AGENT = "User-Agent";

	/**
	 * @param reader {@link BufferedReader}
	 * @return {@link HTTPHeader}
	 * @throws Exception Falls was schief geht.
	 */
	public static HTTPHeader parseHeader(final BufferedReader reader) throws Exception
	{
		HTTPHeader header = new HTTPHeader();
		Map<String, String> map = header.headerMap;

		String line = reader.readLine();

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(line);
		}

		if (line == null)
		{
			return header;
		}

		// Entscheidet über Request oder Response.
		header.firstLine = line;

		String[] url = header.firstLine.split(" ", 3);

		if (url[0].startsWith("HTTP"))
		{
			// Antwort
			header.protocol = url[0];
			header.responseCode = Integer.parseInt(url[1]);
			header.responseMessage = url[2];
		}
		else
		{
			// Request
			header.method = url[0];
			header.context = url[1];
			header.protocol = url[2];
		}

		// Read header
		while (((line = reader.readLine()) != null) && (line.length() > 0))
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug(line);
			}

			String[] tokens = line.split(": ");

			map.put(tokens[0], tokens[1]);
		}

		// // If method 'POST' then read Content-Length worth of data
		// if (url[0].equalsIgnoreCase("POST"))
		// {
		// int len = Integer.parseInt(map.get("Content-Length")[0]);
		// char[] buf = new char[len];
		//
		// if (reader.read(buf) == len)
		// {
		// line = String.copyValueOf(buf);
		// }
		// }
		// else if (url[0].equalsIgnoreCase("GET"))
		// {
		// int idx = url[1].indexOf('?');
		//
		// if (idx != -1)
		// {
		// map.put(CONTEXT, new String[]
		// {
		// url[1].substring(1, idx)
		// });
		//
		// line = url[1].substring(idx + 1);
		// }
		// else
		// {
		// line = null;
		// }
		// }
		//
		// if (line != null)
		// {
		// String[] match = line.split("\\&");
		//
		// for (String element : match)
		// {
		// String[] params = new String[1];
		// String[] tokens = element.split("=");
		//
		// switch (tokens.length)
		// {
		// case 0:
		// map.put("@".concat(element), new String[] {});
		// break;
		// case 1:
		// map.put("@".concat(tokens[0]), new String[] {});
		// break;
		// default:
		// String name = "@".concat(tokens[0]);
		//
		// if (map.containsKey(name))
		// {
		// params = map.get(name);
		// String[] tmp = new String[params.length + 1];
		// for (int j = 0; j < params.length; j++)
		// {
		// tmp[j] = params[j];
		// }
		// params = null;
		// params = tmp;
		// }
		//
		// params[params.length - 1] = tokens[1].trim();
		// map.put(name, params);
		// }
		// }
		// }

		return header;
	}

	/**
	 * 
	 */
	private String context = null;

	/**
	 * 
	 */
	private String firstLine = null;

	/**
	 * 
	 */
	private final Map<String, String> headerMap;

	/**
	 * 
	 */
	private String method = null;

	/**
	 * 
	 */
	private String protocol = null;

	/**
	 * 
	 */
	private int responseCode = -1;

	/**
	 * 
	 */
	private String responseMessage = null;

	/**
	 * Erstellt ein neues {@link HTTPHeader} Object.
	 */
	public HTTPHeader()
	{
		super();

		this.headerMap = new LinkedHashMap<>();
	}

	/**
	 * Erstellt ein neues {@link HTTPHeader} Object.
	 * 
	 * @param src {@link HTTPHeader}, kopiert alle Inhalte.
	 */
	public HTTPHeader(final HTTPHeader src)
	{
		this();

		this.headerMap.putAll(src.headerMap);

		this.firstLine = src.firstLine;
		this.context = src.context;
		this.method = src.method;
		this.protocol = src.protocol;
		this.responseCode = src.responseCode;
		this.responseMessage = src.responseMessage;
	}

	/**
	 * @return int
	 */
	public int getContentLength()
	{
		String value = this.headerMap.get(CONTENT_LENGTH);

		return value == null ? 0 : Integer.parseInt(value);
	}

	/**
	 * Null wenn es ein Response ist.
	 * 
	 * @return String
	 */
	public String getContext()
	{
		return this.context;
	}

	/**
	 * @return String
	 */
	public String getFirstLine()
	{
		return this.firstLine;
	}

	/**
	 * @return Map<String,String>
	 */
	public Map<String, String> getHeaders()
	{
		return this.headerMap;
	}

	/**
	 * Null wenn es ein Response ist.
	 * 
	 * @return String
	 */
	public String getMethod()
	{
		return this.method;
	}

	/**
	 * @return String
	 */
	public String getProtocol()
	{
		return this.protocol;
	}

	/**
	 * -1 wenn es ein Request ist.
	 * 
	 * @return int
	 */
	public int getResponseCode()
	{
		return this.responseCode;
	}

	/**
	 * Null wenn es ein Request ist.
	 * 
	 * @return String
	 */
	public String getResponseMessage()
	{
		return this.responseMessage;
	}

	/**
	 * Liefert true, wenn die Header eine Antwort sind.
	 * 
	 * @return boolean
	 */
	public boolean isResponse()
	{
		return this.firstLine.startsWith("HTTP");
	}

	/**
	 * keep-alive, close
	 * 
	 * @param value String
	 */
	public void setConnectionValue(final String value)
	{
		this.headerMap.put(CONNECTION, value);
	}

	/**
	 * @param value String
	 */
	public void setContentLengthValue(final String value)
	{
		this.headerMap.put(CONTENT_LENGTH, value);
	}

	/**
	 * @param value String
	 */
	public void setHostValue(final String value)
	{
		this.headerMap.put(HOST, value);
	}

	/**
	 * @param value String
	 */
	public void setServerValue(final String value)
	{
		this.headerMap.put(SERVER, value);
	}

	/**
	 * @param value String
	 */
	public void setUserAgentValue(final String value)
	{
		this.headerMap.put(USER_AGENT, value);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		Iterator<Entry<String, String>> iterator = this.headerMap.entrySet().iterator();

		while (iterator.hasNext())
		{
			Entry<String, String> entry = iterator.next();

			String key = entry.getKey();
			String value = entry.getValue();

			builder.append(key);
			builder.append("[");
			builder.append(value);
			builder.append("]");

			if (iterator.hasNext())
			{
				builder.append(" ");
			}
		}

		return builder.toString();
	}
}
