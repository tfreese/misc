/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy.repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Optional;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * HTTP-Implementierung eines Repositories.
 *
 * @author Thomas Freese
 */
public class HTTPRepository extends AbstractRepository
{
    /**
     * (0x0D, 0x0A), (13,10), (\r\n)
     */
    private static final String CRLF = "\r\n";

    /**
     * Erstellt ein neues {@link HTTPRepository} Object.
     *
     * @param uri String; Ressourenquelle des Repositories
     * @throws URISyntaxException Falls was schief geht.
     */
    public HTTPRepository(final String uri) throws URISyntaxException
    {
        this(new URI(uri));
    }

    /**
     * Erstellt ein neues {@link HTTPRepository} Object.
     *
     * @param uri String; Ressourenquelle des Repositories
     * @param charset {@link Charset}; Kodierung
     * @throws URISyntaxException Falls was schief geht.
     */
    public HTTPRepository(final String uri, final Charset charset) throws URISyntaxException
    {
        this(new URI(uri), charset);
    }

    /**
     * Erstellt ein neues {@link HTTPRepository} Object.
     *
     * @param uri {@link URI}; Ressourenquelle des Repositories
     */
    public HTTPRepository(final URI uri)
    {
        this(uri, Charset.forName("ISO-8859-1"));
    }

    /**
     * Erstellt ein neues {@link HTTPRepository} Object.
     *
     * @param uri {@link URI}; Ressourenquelle des Repositories
     * @param charset {@link Charset}; Kodierung
     */
    public HTTPRepository(final URI uri, final Charset charset)
    {
        super(uri, charset);

        String scheme = uri.getScheme();

        if ((scheme == null) || (!scheme.equals("http") && !scheme.equals("https")))
        {
            getLogger().error("Must use HTTP/S protocol, repository disabled");
            setActive(false);
            return;
        }
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#dispose()
     */
    @Override
    public void dispose()
    {
        // Empty
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#exist(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    public MavenResponse exist(final MavenRequest mavenRequest) throws Exception
    {
        // HTTP Request bauen.
        // mavenRequest.setConnectionValue("close");
        mavenRequest.setHostValue(getUri().getHost());

        String statusLine =
                String.format("%s %s%s %s", mavenRequest.getHttpMethod(), getUri().getPath(), mavenRequest.getHttpUri(), mavenRequest.getHttpProtocol());

        StringBuilder request = new StringBuilder();
        request.append(statusLine).append(CRLF);

        mavenRequest.getHeaders().forEach((header, value) -> {
            request.append(header).append(": ").append(value).append(CRLF);
        });

        request.append(CRLF); // Headerabschluss

        MavenResponse mavenResponse = existNIO(request);
        // MavenResponse mavenResponse = existIO(mavenRequest);

        if (mavenResponse != null)
        {
            mavenResponse.setHttpUri(mavenRequest.getHttpUri());
        }

        return mavenResponse;
    }

    /**
     * @param mavenRequest {@link MavenRequest}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    MavenResponse existIO(final MavenRequest mavenRequest) throws Exception
    {
        MavenResponse mavenResponse = null;

        StringBuilder url = new StringBuilder(getUri().getScheme());
        url.append("://");
        url.append(getUri().getHost());

        if (getUri().getPort() > 0)
        {
            url.append(":").append(getUri().getPort());
        }

        url.append(getUri().getPath());
        // url.append(mavenRequest.getHttpUri());

        URI uri = new URI(url.toString());
        Proxy proxy = ProxySelector.getDefault().select(uri).get(0);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection(proxy);

        // Header kopieren.
        mavenRequest.getHeaders().forEach((headerName, headerValue) -> {
            String actualHeaderValue = Optional.ofNullable(headerValue).orElse("");
            connection.addRequestProperty(headerName, actualHeaderValue);
        });

        connection.setRequestMethod("HEAD");
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.connect();

        // Response lesen.
        mavenResponse = MavenResponse.create(connection);
        // try (InputStream response = connection.getInputStream();
        // BufferedReader reader = new BufferedReader(new InputStreamReader(response)))
        // {
        // mavenResponse = MavenResponse.create(reader);
        // }

        connection.disconnect();

        return mavenResponse;
    }

    /**
     * @param request {@link CharSequence}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    MavenResponse existNIO(final CharSequence request) throws Exception
    {
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

        return mavenResponse;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getResource(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    public MavenResponse getResource(final MavenRequest mavenRequest) throws Exception
    {
        // HTTP Request bauen.
        mavenRequest.setConnectionValue("close");
        mavenRequest.setHostValue(getUri().getHost());

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

        MavenResponse mavenResponse = getResourceNIO(request);
        // MavenResponse mavenResponse = getResourceIO(mavenRequest);

        if (mavenResponse != null)
        {
            mavenResponse.setHttpUri(mavenRequest.getHttpUri());
        }

        return mavenResponse;
    }

    /**
     * @param mavenRequest {@link MavenRequest}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    MavenResponse getResourceIO(final MavenRequest mavenRequest) throws Exception
    {
        MavenResponse mavenResponse = null;

        StringBuilder url = new StringBuilder(getUri().getScheme());
        url.append("://");
        url.append(getUri().getHost());

        if (getUri().getPort() > 0)
        {
            url.append(":").append(getUri().getPort());
        }

        url.append(getUri().getPath());
        url.append(mavenRequest.getHttpUri());

        URI uri = new URI(url.toString());
        Proxy proxy = ProxySelector.getDefault().select(uri).get(0);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection(proxy);

        // Header kopieren.
        mavenRequest.getHeaders().forEach((headerName, headerValue) -> {
            String actualHeaderValue = Optional.ofNullable(headerValue).orElse("");
            connection.addRequestProperty(headerName, actualHeaderValue);
        });

        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.connect();

        mavenResponse = MavenResponse.create(connection);

        // Response lesen.
        try (InputStream response = connection.getInputStream())// ;
        // BufferedReader reader = new BufferedReader(new InputStreamReader(response)))
        {
            // mavenResponse = MavenResponse.create(reader);

            int contentLength = mavenResponse.getContentLength();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : 16 * 1024))
            {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead = -1;

                while ((bytesRead = response.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, bytesRead);
                }

                baos.flush();

                mavenResponse.setResource(baos.toByteArray());
            }
        }

        connection.disconnect();

        return mavenResponse;
    }

    /**
     * @param request {@link CharSequence}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    MavenResponse getResourceNIO(final CharSequence request) throws Exception
    {
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

        return mavenResponse;
    }
}
