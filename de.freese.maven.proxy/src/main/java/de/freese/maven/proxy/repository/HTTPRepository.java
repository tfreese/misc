/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map.Entry;

import de.freese.maven.proxy.model.HTTPHeader;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * HTTP-Implementierung eines Repositories.
 * 
 * @author Thomas Freese
 */
public class HTTPRepository extends AbstractRemoteRepository
{
	/**
	 * (0x0D, 0x0A), (13,10), (\r\n)
	 */
	private static final String CRLF = "\r\n";

	/**
	 *
	 */
	private String path = null;

	/**
	 * 
	 */
	private String hostname = null;

	/**
	 * 
	 */
	private int port = -1;

	/**
	 * Erstellt ein neues {@link HTTPRepository} Object.
	 */
	public HTTPRepository()
	{
		super();
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#dispose()
	 */
	@Override
	public void dispose()
	{
		// Empty
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#exist(de.freese.maven.proxy.model.MavenRequest)
	 */
	@Override
	public MavenResponse exist(final MavenRequest mavenRequest) throws Exception
	{
		// HTTP Request bauen.
		HTTPHeader header = new HTTPHeader(mavenRequest.getHttpHeader());
		header.setConnectionValue("close");
		header.setHostValue(getUri().getHost());

		String firstLine =
				String.format("%s %s%s %s", header.getMethod(), this.path, header.getContext(),
						header.getProtocol());

		StringBuilder request = new StringBuilder();
		request.append(firstLine).append(CRLF);

		for (Entry<String, String> entry : header.getHeaders().entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();

			request.append(key).append(": ").append(value).append(CRLF);
		}

		request.append(CRLF); // Headerabschluss

		// Abchicken
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(this.hostname, this.port));

		CharBuffer requestChars = CharBuffer.wrap(request);
		ByteBuffer requestBytes = getCharsetEncoder().encode(requestChars);

		socketChannel.write(requestBytes);

		BufferedReader reader =
				new BufferedReader(Channels.newReader(socketChannel, getCharsetDecoder(), -1));

		HTTPHeader headerResponse = HTTPHeader.parseHeader(reader);

		// ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		// ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.capacity());
		// WritableByteChannel destination = Channels.newChannel(baos);

		// // Auslesen
		// while (socketChannel.read(buffer) != -1)
		// {
		// buffer.flip();
		//
		// while (buffer.hasRemaining())
		// {
		// destination.write(buffer);
		// }
		//
		// buffer.clear();
		// }
		//
		// try
		// {
		// socketChannel.close();
		// }
		// catch (Exception ex)
		// {
		// // Ignore
		// }
		//
		// try
		// {
		// baos.close();
		// }
		// catch (Exception ex)
		// {
		// // Ignore
		// }
		//
		// BufferedReader reader =
		// new BufferedReader(new StringReader(new String(baos.toByteArray(),
		// getCharsetDecoder().charset())));

		reader.close();

		MavenResponse mavenResponse = new MavenResponse();
		mavenResponse.setHttpHeader(headerResponse);

		return mavenResponse;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#getResource(de.freese.maven.proxy.model.MavenRequest)
	 */
	@Override
	public MavenResponse getResource(final MavenRequest mavenRequest) throws Exception
	{
		// HTTP Request bauen.
		HTTPHeader header = new HTTPHeader(mavenRequest.getHttpHeader());
		header.setConnectionValue("close");
		header.setHostValue(getUri().getHost());

		String firstLine =
				String.format("%s %s%s %s", header.getMethod(), this.path, header.getContext(),
						header.getProtocol());

		StringBuilder request = new StringBuilder();
		request.append(firstLine).append(CRLF);

		for (Entry<String, String> entry : header.getHeaders().entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();

			request.append(key).append(": ").append(value).append(CRLF);
		}

		request.append(CRLF); // Headerabschluss

		// request.append("Cache-control: no-cache").append("\r\n");
		// request.append("Cache-store: no-store").append("\r\n");
		// request.append("Pragma: no-cache").append("\r\n");

		if (getLogger().isDebugEnabled())
		{
			getLogger().debug("{}\n {}", toString(), request.toString());
		}

		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(this.hostname, this.port));

		/**
		 * Allocate a 32 Kilobyte byte buffer for reading the response.<br>
		 * Hopefully we'll get a low-level "direct" buffer.
		 */
		ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);

		// Channel to write to it.
		ByteArrayOutputStream baosHeader = new ByteArrayOutputStream(1024);

		ByteArrayOutputStream baosResource = new ByteArrayOutputStream(buffer.capacity());
		WritableByteChannel resourceChannel = Channels.newChannel(baosResource);

		// CharBuffer charBuffer = CharBuffer.allocate(buffer.capacity());

		// Now wrap a CharBuffer around that request string
		CharBuffer requestChars = CharBuffer.wrap(request);
		// Use the charset to encode the request into a byte buffer
		ByteBuffer requestBytes = getCharsetEncoder().encode(requestChars);

		socketChannel.write(requestBytes);

		// Have we discarded the HTTP response headers yet?
		boolean skipHeaders = false;
		// The code sent by the server.
		// int responseCode = -1;

		buffer.clear();

		// Now loop, reading data from the server channel and writing it
		// to the destination channel until the server indicates that it
		// has no more data.
		while (socketChannel.read(buffer) != -1)
		{
			// Read data, and check for end.
			// Prepare to extract data from buffer.
			buffer.flip();

			if (!skipHeaders)
			{

				try
				{
					// Erste Zeile lesen bis \r\n.
					//
					// First, though, read the HTTP response code.
					// Assume that we get the complete first line of the
					// response when the first read() call returns. Assume also
					// that the first 9 bytes are the ASCII characters
					// "HTTP/1.1 ", and that the response code is the ASCII
					// characters in the following three bytes.
					byte[] buf = new byte[1];

					for (;;)
					{
						buffer.get(buf);
						baosHeader.write(buf);

						if ((buf[0] == 10) && (buffer.get(buffer.position() - 2) == 13))
						{
							break;
						}
					}

					// Bis zum Headerabschluss lesen.
					//
					// All HTTP reponses begin with a set of HTTP headers, which
					// we need to discard. The headers end with the string
					// "\r\n\r\n", or the bytes 13,10,13,10. If we haven't already
					// skipped them then do so now.
					for (;;)
					{
						buffer.get(buf);

						if ((buf[0] == 10) && (buffer.get(buffer.position() - 2) == 13)
								&& (buffer.get(buffer.position() - 3) == 10)
								&& (buffer.get(buffer.position() - 4) == 13))
						{
							skipHeaders = true;
							break;
						}

						baosHeader.write(buf);
					}
				}
				catch (BufferUnderflowException ex)
				{
					// If we arrive here, it means we reached the end of
					// the buffer and didn't find the end of the headers.
					// There is a chance that the last 1, 2, or 3 bytes in
					// the buffer were the beginning of the \r\n\r\n
					// sequence, so back up a bit.
					buffer.position(buffer.position() - 3);
					// Now discard the headers we have read
					buffer.compact();
					// And go read more data from the server.
					continue;
				}
			}

			// // Decode buffer
			// decoder.decode(buffer, charBuffer, false);
			// // Display
			// charBuffer.flip();
			// System.out.println(charBuffer);
			// charBuffer.clear();

			// Write the data out; drain the buffer fully.
			while (buffer.hasRemaining())
			{
				resourceChannel.write(buffer);
			}

			// Now that the buffer is drained, put it into fill mode
			// in preparation for reading more data into it.
			buffer.clear(); // data.compact() also works here
		}

		socketChannel.close();

		byte[] resource = baosResource.toByteArray();
		baosResource.close();

		BufferedReader reader =
				new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
						baosHeader.toByteArray())));

		HTTPHeader headerResponse = HTTPHeader.parseHeader(reader);

		baosHeader.close();
		reader.close();

		MavenResponse mavenResponse = new MavenResponse();
		mavenResponse.setHttpHeader(headerResponse);
		mavenResponse.setResource(resource);

		return mavenResponse;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#init()
	 */
	@Override
	public void init()
	{
		String scheme = getUri().getScheme();

		if ((scheme == null) || (!scheme.equals("http") && !scheme.equals("https")))
		{
			getLogger().error("Must use HTTP/S protocol, repository disabled");
			setActive(false);
			return;
		}

		this.hostname = getUri().getHost();

		this.port = getUri().getPort();

		if (this.port == -1)
		{
			// Use default port if none specified.
			this.port = 80;
		}

		this.path = getUri().getRawPath();

		if ((this.path == null) || (this.path.length() == 0))
		{
			this.path = "";
		}
	}
}
