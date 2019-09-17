/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy.old.repository.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import de.freese.maven.proxy.old.model.MavenRequest;
import de.freese.maven.proxy.old.model.MavenResponse;

/**
 * HTTP-Implementierung eines Repositories.
 *
 * @author Thomas Freese
 */
public class HttpNioRepository extends AbstractHttpRepository
{
    /**
     * Erstellt ein neues {@link HttpNioRepository} Object.
     *
     * @param uri String; Quelle des Repositories
     * @throws URISyntaxException Falls was schief geht.
     */
    public HttpNioRepository(final String uri) throws URISyntaxException
    {
        super(new URI(uri));
    }

    /**
     * Erstellt ein neues {@link HttpNioRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     */
    public HttpNioRepository(final URI uri)
    {
        super(uri);
    }

    /**
     * Erstellt ein neues {@link HttpNioRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     * @param charset {@link Charset}; Kodierung
     */
    public HttpNioRepository(final URI uri, final Charset charset)
    {
        super(uri, charset);
    }

    /**
     * @see de.freese.maven.proxy.old.repository.http.AbstractHttpRepository#existImpl(de.freese.maven.proxy.old.model.MavenRequest)
     */
    @Override
    protected MavenResponse existImpl(final MavenRequest mavenRequest) throws Exception
    {
        String statusLine =
                String.format("%s %s%s %s", mavenRequest.getHttpMethod(), getUri().getPath(), mavenRequest.getHttpUri(), mavenRequest.getHttpProtocol());

        StringBuilder request = new StringBuilder();
        request.append(statusLine).append(CRLF);

        mavenRequest.getHeaders().forEach((header, value) -> {
            request.append(header).append(": ").append(value).append(CRLF);
        });

        request.append(CRLF); // Headerabschluss

        // request.append("Cache-control: no-cache").append("\r\n");
        // request.append("Cache-store: no-store").append("\r\n");
        // request.append("Pragma: no-cache").append("\r\n");
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("{}\n {}", toString(), request.toString());
        }

        MavenResponse mavenResponse = null;

        // Abschicken
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, getCharsetDecoder(), -1)))
        {
            if (getUri().getPort() > 0)
            {
                socketChannel.connect(new InetSocketAddress(getUri().getHost(), getUri().getPort()));
            }
            else
            {
                socketChannel.connect(new InetSocketAddress(getUri().getHost(), 80));
            }

            CharBuffer requestChars = CharBuffer.wrap(request);
            ByteBuffer requestBytes = getCharsetEncoder().encode(requestChars);

            socketChannel.write(requestBytes);

            mavenResponse = MavenResponse.create(reader);

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
            // BufferedReader reader =
            // new BufferedReader(new StringReader(new String(baos.toByteArray(),
            // getCharsetDecoder().charset())));
        }
        finally
        {
            getCharsetDecoder().reset();
        }

        return mavenResponse;
    }

    /**
     * @see de.freese.maven.proxy.old.repository.http.AbstractHttpRepository#getResourceImpl(de.freese.maven.proxy.old.model.MavenRequest)
     */
    @Override
    protected MavenResponse getResourceImpl(final MavenRequest mavenRequest) throws Exception
    {
        String firstLine =
                String.format("%s %s%s %s", mavenRequest.getHttpMethod(), getUri().getPath(), mavenRequest.getHttpUri(), mavenRequest.getHttpProtocol());

        StringBuilder request = new StringBuilder();
        request.append(firstLine).append(CRLF);

        mavenRequest.getHeaders().forEach((header, value) -> {
            request.append(header).append(": ").append(value).append(CRLF);
        });

        request.append(CRLF); // Headerabschluss

        // request.append("Cache-control: no-cache").append("\r\n");
        // request.append("Cache-store: no-store").append("\r\n");
        // request.append("Pragma: no-cache").append("\r\n");
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("{}\n {}", toString(), request.toString());
        }

        MavenResponse mavenResponse = null;

        /**
         * Allocate a 32 Kilobyte byte buffer for reading the response.<br>
         * Hopefully we'll get a low-level "direct" buffer.
         */
        ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);

        try (SocketChannel socketChannel = SocketChannel.open();
             ByteArrayOutputStream baosHeader = new ByteArrayOutputStream(1024);
             ByteArrayOutputStream baosResource = new ByteArrayOutputStream(buffer.capacity());
             WritableByteChannel resourceChannel = Channels.newChannel(baosResource))
        {
            if (getUri().getPort() > 0)
            {
                socketChannel.connect(new InetSocketAddress(getUri().getHost(), getUri().getPort()));
            }
            else
            {
                socketChannel.connect(new InetSocketAddress(getUri().getHost(), 80));
            }

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

                            if ((buf[0] == 10) && (buffer.get(buffer.position() - 2) == 13) && (buffer.get(buffer.position() - 3) == 10)
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

            byte[] resource = baosResource.toByteArray();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baosHeader.toByteArray()))))
            {
                mavenResponse = MavenResponse.create(reader);

                baosHeader.close();
            }

            mavenResponse.setResource(resource);
        }
        finally
        {
            getCharsetEncoder().reset();
        }

        return mavenResponse;
    }
}
