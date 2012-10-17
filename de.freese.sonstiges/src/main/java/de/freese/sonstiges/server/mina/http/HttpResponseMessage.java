package de.freese.sonstiges.server.mina.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * A HTTP response message.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 581234 $, $Date: 2007-10-02 22:39:48 +0900 (?, 02 10? 2007) $
 */
public class HttpResponseMessage
{
	/**
	 * The end of line character sequence used by most IETF protocols. That is a carriage return
	 * followed by a newline: "\r\n" (NETASCII_EOL)
	 */
	public static final String CRLF = "\r\n";

	/**
	 * 
	 */
	public static final byte[] CRLF_BYTES = CRLF.getBytes();

	/**
	 * 
	 */
	public static final int HTTP_STATUS_NOT_FOUND = 404;

	/**
	 * HTTP response codes
	 */
	public static final int HTTP_STATUS_SUCCESS = 200;

	/**
	 * Storage for body of HTTP response.
	 */
	private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

	/**
	 * Map<String, String>
	 */
	private final Map<String, String> headers = new HashMap<>();

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
		this.headers.put("Date",
				new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
		this.headers.put("Last-Modified",
				new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
	}

	/**
	 * @param b byte[]
	 * @throws IOException Falls was schief geht.
	 */
	public void appendBody(final byte[] b) throws IOException
	{
		this.body.write(b);
	}

	/**
	 * @param s String
	 * @throws IOException Falls was schief geht.
	 */
	public void appendBody(final String s) throws IOException
	{
		this.body.write(s.getBytes());
		this.body.write(CRLF_BYTES);
	}

	/**
	 * @return {@link IoBuffer} 
	 */
	public IoBuffer getBody()
	{
		return IoBuffer.wrap(this.body.toByteArray());
	}

	/**
	 * @return int
	 */
	public int getBodyLength()
	{
		return this.body.size();
	}

	/**
	 * @return Map
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
