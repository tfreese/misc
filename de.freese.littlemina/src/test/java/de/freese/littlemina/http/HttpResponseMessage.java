// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.littlemina.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A HTTP response message.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 581234 $, $Date: 2007-10-02 22:39:48 +0900 (?, 02 10? 2007) $
 */
public class HttpResponseMessage
{

	/**
	 *
	 */
	public static final int HTTP_STATUS_NOT_FOUND = 404;

	/**
	 *
	 */
	public static final int HTTP_STATUS_SUCCESS = 200;

	/**
	 *
	 */
	private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

	/**
	*
	*/
	private final Map<String, String> headers = new HashMap<String, String>();

	/**
	 *
	 */
	private int responseCode = HTTP_STATUS_SUCCESS;

	/**
	 * Erstellt ein neues {@link HttpResponseMessage} Object.
	 */
	public HttpResponseMessage()
	{
		super();

		this.headers.put("Server", "MEINER !");
		this.headers.put("Cache-Control", "private");
		this.headers.put("Content-Type", "text/html; charset=iso-8859-1");
		this.headers.put("Connection", "keep-alive");
		this.headers.put("Keep-Alive", "200");
		this.headers.put("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
				.format(new Date()));
		this.headers.put("Last-Modified", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")
				.format(new Date()));
	}

	/**
	 * @param b byte[]
	 */
	public void appendBody(final byte[] b)
	{
		try
		{
			this.body.write(b);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * @param s String
	 */
	public void appendBody(final String s)
	{
		appendBody(s.getBytes());
	}

	/**
	 * @return byte[]
	 */
	public byte[] getBody()
	{
		return this.body.toByteArray();
	}

	/**
	 * @return int
	 */
	public int getBodyLength()
	{
		return this.body.size();
	}

	/**
	 * @return {@link Map}
	 */
	public Map<String, String> getHeaders()
	{
		return this.headers;
	}

	/**
	 * @return int
	 */
	public int getResponseCode()
	{
		return this.responseCode;
	}

	/**
	 * @param contentType String
	 */
	public void setContentType(final String contentType)
	{
		this.headers.put("Content-Type", contentType);
	}

	/**
	 * @param responseCode int
	 */
	public void setResponseCode(final int responseCode)
	{
		this.responseCode = responseCode;
	}
}
