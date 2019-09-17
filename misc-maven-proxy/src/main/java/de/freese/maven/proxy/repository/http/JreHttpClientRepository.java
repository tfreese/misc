/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy.repository.http;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * HTTP-Implementierung eines Repositories mit dem {@link HttpClient}.<br>
 * https://www.baeldung.com/httpclient-guide
 *
 * @author Thomas Freese
 */
public class JreHttpClientRepository extends AbstractHttpRepository
{
    /**
     *
     */
    private final HttpClient httpClient;

    /**
     * Erstellt ein neues {@link JreHttpClientRepository} Object.
     *
     * @param uri String; Quelle des Repositories
     * @param httpClient {@link HttpClient}
     * @throws URISyntaxException Falls was schief geht.
     */
    public JreHttpClientRepository(final String uri, final HttpClient httpClient) throws URISyntaxException
    {
        this(new URI(uri), httpClient);
    }

    /**
     * Erstellt ein neues {@link JreHttpClientRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     * @param httpClient {@link HttpClient}
     */
    public JreHttpClientRepository(final URI uri, final HttpClient httpClient)
    {
        super(uri);

        this.httpClient = Objects.requireNonNull(httpClient, "httpClient required");
    }

    /**
     * Liefert das absoluten Verzeichnis der Datei.
     *
     * @param baseUri {@link URI}
     * @param resource String
     * @return {@link URI}
     */
    protected URI createResourceUri(final URI baseUri, final String resource)
    {
        String path = baseUri.getPath();

        if (path.endsWith("/") && resource.startsWith("/"))
        {
            path += resource.substring(1);
        }
        else if (path.endsWith("/") && !resource.startsWith("/"))
        {
            path += resource;
        }
        else if (!path.endsWith("/") && resource.startsWith("/"))
        {
            path += resource;
        }

        URI uri = baseUri.resolve(path);

        return uri;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#exist(java.lang.String)
     */
    @Override
    public boolean exist(final String resource) throws Exception
    {
        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(createResourceUri(getUri(),resource))
                .method("HEAD", BodyPublishers.noBody())
                .header(HttpHeaderNames.USER_AGENT.toString(), "Maven-Proxy")
                .build();
        // @formatter:on

        HttpResponse<String> response = getHttpClient().send(request, BodyHandlers.ofString());

        return response.statusCode() == HTTP_OK;
    }

    /**
     * @return {@link HttpClient}
     */
    protected HttpClient getHttpClient()
    {
        return this.httpClient;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getInputStream(java.lang.String)
     */
    @Override
    public InputStream getInputStream(final String resource) throws Exception
    {
        // StringBuilder url = new StringBuilder(getUri().getScheme());
        // url.append("://");
        // url.append(getUri().getHost());
        //
        // if (getUri().getPort() > 0)
        // {
        // url.append(":").append(getUri().getPort());
        // }
        //
        // url.append(getUri().getPath());
        // url.append(mavenRequest.getHttpUri());
        //
        // URI uri = new URI(url.toString());

        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(createResourceUri(getUri(),resource))
                .GET()
                .header(HttpHeaderNames.USER_AGENT.toString(), "Maven-Proxy")
                .build();
        // @formatter:on

        HttpResponse<InputStream> response = getHttpClient().send(request, BodyHandlers.ofInputStream());

        if (response.statusCode() != HTTP_OK)
        {
            return null;
        }

        return response.body();

        // int contentLength = response.headers().firstValue("content-length").map(Integer::parseInt).orElse(0);
        //
        // // Response lesen.
        // try (InputStream input = response.body())
        // {
        // try (ByteArrayOutputStream baos = new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : 16 * 1024))
        // {
        // byte[] buffer = new byte[8 * 1024];
        // int bytesRead = -1;
        //
        // while ((bytesRead = input.read(buffer)) != -1)
        // {
        // baos.write(buffer, 0, bytesRead);
        // }
        //
        // baos.flush();
        // }
        // }
    }
}
