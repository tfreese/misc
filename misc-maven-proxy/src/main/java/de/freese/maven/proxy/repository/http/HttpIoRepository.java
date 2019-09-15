/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy.repository.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Optional;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * HTTP-Implementierung eines Repositories mit IO-Streams.
 *
 * @author Thomas Freese
 */
public class HttpIoRepository extends AbstractHttpRepository
{
    /**
     * Erstellt ein neues {@link HttpIoRepository} Object.
     *
     * @param uri String; Quelle des Repositories
     * @throws URISyntaxException Falls was schief geht.
     */
    public HttpIoRepository(final String uri) throws URISyntaxException
    {
        super(new URI(uri));
    }

    /**
     * Erstellt ein neues {@link HttpIoRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     */
    public HttpIoRepository(final URI uri)
    {
        super(uri);
    }

    /**
     * Erstellt ein neues {@link HttpIoRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     * @param charset {@link Charset}; Kodierung
     */
    public HttpIoRepository(final URI uri, final Charset charset)
    {
        super(uri, charset);
    }

    /**
     * @see de.freese.maven.proxy.repository.http.AbstractHttpRepository#existImpl(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    protected MavenResponse existImpl(final MavenRequest mavenRequest) throws Exception
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
     * @see de.freese.maven.proxy.repository.http.AbstractHttpRepository#getResourceImpl(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    protected MavenResponse getResourceImpl(final MavenRequest mavenRequest) throws Exception
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
}
