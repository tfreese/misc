/**
 * Created: 28.12.2011
 */

package de.freese.sonstiges.server.mina.maven;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;

/**
 * @author Thomas Freese
 */
public class HTTPDownload
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		Charset charset = Charset.forName("ISO-8859-1");
		String url = null;
		url =
				"http://localhost:8089/artifactory/remote-repos/javax/servlet/servlet-api/2.5/maven-metadata.xml";
		// url =
		// "http://freese-home.de/maven/repository-thirdparty/com/jgoodies/binding/2.5.0/binding-2.5.0.jar";
		// url = "http://repo1.maven.org/maven2/javax/servlet/servlet-api/2.5/maven-metadata.xml";

		testNio(charset, url);
		// testUrlResource(charset, address, context);
	}

	/**
	 * @param charset {@link Charset}
	 * @param url String
	 * @throws Exception Falls was schief geht.
	 */
	private static void testNio(final Charset charset, final String url) throws Exception
	{
		// Parse the URL. Note we use the new java.net.URI, not URL here.
		URI uri = new URI(url);

		// Now query and verify the various parts of the URI.
		String scheme = uri.getScheme();

		if ((scheme == null) || !scheme.equals("http"))
		{
			throw new IllegalArgumentException("Must use 'http:' protocol");
		}

		String hostname = uri.getHost();

		int port = uri.getPort();

		if (port == -1)
		{
			// Use default port if none specified.
			port = 80;
		}

		String path = uri.getRawPath();

		if ((path == null) || (path.length() == 0))
		{
			path = "/";
		}

		String query = uri.getRawQuery();
		query = (query == null) ? "" : '?' + query;

		SocketAddress serverAddress = new InetSocketAddress(hostname, port);

		SocketChannel channel = null;

		try
		{
			// Setup
			// CharsetDecoder decoder = charset.newDecoder();
			CharsetEncoder encoder = charset.newEncoder();

			// Channel to write to it.
			WritableByteChannel destination = Channels.newChannel(System.out);

			// Allocate a 32 Kilobyte byte buffer for reading the response.
			// Hopefully we'll get a low-level "direct" buffer.
			ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);
			// CharBuffer charBuffer = CharBuffer.allocate(buffer.capacity());

			StringBuilder request = new StringBuilder();
			request.append("GET " + path + query + " HTTP/1.1").append("\r\n");
			// request.append("HEAD HTTP/1.1").append("\r\n");
			request.append("Pragma: no-cache").append("\r\n");
			request.append("Host: ").append(hostname).append("\r\n"); // Benötigt für HTTP 1.1
			request.append("Connection: close").append("\r\n"); // keep-alive
			request.append("\r\n"); // Headerabschluss

			// request.append("Accept-Encoding: gzip").append("\r\n");
			// request.append("User-Agent:").append(HTTPDownload.class.getName()).append("\r\n");
			// request.append("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\r\n");

			System.out.println(request);

			// Connect
			channel = SocketChannel.open();
			channel.connect(serverAddress);

			// Send request
			// Now wrap a CharBuffer around that request string
			CharBuffer requestChars = CharBuffer.wrap(request);
			// Use the charset to encode the request into a byte buffer
			ByteBuffer requestBytes = encoder.encode(requestChars);

			channel.write(requestBytes);

			// Have we discarded the HTTP response headers yet?
			boolean skippedHeaders = false;
			// The code sent by the server.
			int responseCode = -1;

			// Now loop, reading data from the server channel and writing it
			// to the destination channel until the server indicates that it
			// has no more data.
			while (channel.read(buffer) != -1)
			{
				// Read data, and check for end.
				// Prepare to extract data from buffer.
				buffer.flip();

				// All HTTP reponses begin with a set of HTTP headers, which
				// we need to discard. The headers end with the string
				// "\r\n\r\n", or the bytes 13,10,13,10. If we haven't already
				// skipped them then do so now.
				if (!skippedHeaders)
				{
					// First, though, read the HTTP response code.
					// Assume that we get the complete first line of the
					// response when the first read() call returns. Assume also
					// that the first 9 bytes are the ASCII characters
					// "HTTP/1.1 ", and that the response code is the ASCII
					// characters in the following three bytes.
					if (responseCode == -1)
					{
						responseCode =
								(100 * (buffer.get(9) - '0')) + (10 * (buffer.get(10) - '0'))
										+ (1 * (buffer.get(11) - '0'));

						// If there was an error, report it and quit.
						// Note that we do not handle redirect responses.
						if ((responseCode < 200) || (responseCode >= 300))
						{
							System.err.println("HTTP Error: " + responseCode);
							System.exit(1);
						}
					}

					// Now skip the rest of the headers.
					try
					{
						for (;;)
						{
							if ((buffer.get() == 13) && (buffer.get() == 10)
									&& (buffer.get() == 13) && (buffer.get() == 10))
							{
								skippedHeaders = true;
								break;
							}
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
					destination.write(buffer);
				}

				// Now that the buffer is drained, put it into fill mode
				// in preparation for reading more data into it.
				buffer.clear(); // data.compact() also works here
			}
		}
		finally
		{
			if (channel != null)
			{
				try
				{
					channel.close();
				}
				catch (IOException ignored)
				{
					// Ignore
				}
			}
		}
	}

	/**
	 * @param charset {@link Charset}
	 * @param url String
	 * @throws Exception Falls was schief geht.
	 */
	private static void testUrlResource(final Charset charset, final String url) throws Exception
	{
		UrlResource resource = new UrlResource(url);
		System.out.println(resource.getURL());
		System.out.println("exist: " + resource.exists());
		System.out.println("readable: " + resource.isReadable());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(resource.getInputStream(), baos);

		System.out.println("size: " + baos.size() + " bytes");

		IOUtils.closeQuietly(baos);
	}

	/**
	 * Erstellt ein neues {@link HTTPDownload} Object.
	 */
	public HTTPDownload()
	{
		super();
	}
}
