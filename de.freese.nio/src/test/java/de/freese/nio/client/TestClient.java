// Created: 12.07.2009
/**
 * 12.07.2009
 */
package de.freese.nio.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author Thomas Freese
 */
public class TestClient
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		// TestClient cl = new TestClient();
		// cl.makeConnection();

		SocketChannel socketChannel = SocketChannel.open();
		InetSocketAddress inetSocketAddress =
				new InetSocketAddress(InetAddress.getLocalHost(), 8080);
		socketChannel.connect(inetSocketAddress);
		socketChannel.configureBlocking(true);

		ByteBuffer bytebuf = ByteBuffer.wrap("Test".getBytes());
		socketChannel.write(bytebuf);

		ByteBuffer buf = ByteBuffer.allocate(1024 * 2);

		int numBytesRead = socketChannel.read(buf);

		while (numBytesRead > 0)
		{
			buf.flip();
			// Charset charset = Charset.forName("iso-8859-1");
			Charset charset = Charset.forName("US-ASCII");
			CharsetDecoder decoder = charset.newDecoder();
			CharBuffer charBuffer = decoder.decode(buf);
			String result = charBuffer.toString();
			System.out.println(result);
			buf.clear();

			numBytesRead = socketChannel.read(buf);
		}

		socketChannel.close();
	}

	/**
	 * Erstellt ein neues {@link TestClient} Object.
	 */
	public TestClient()
	{
		super();
	}
}
