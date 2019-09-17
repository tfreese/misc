/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy.old.repository.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.freese.maven.proxy.old.model.AbstractMavenHttpObject;
import de.freese.maven.proxy.old.model.MavenRequest;
import de.freese.maven.proxy.old.model.MavenResponse;

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
     * @throws URISyntaxException Falls was schief geht.
     */
    public JreHttpClientRepository(final String uri) throws URISyntaxException
    {
        this(new URI(uri));
    }

    /**
     * Erstellt ein neues {@link JreHttpClientRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     */
    public JreHttpClientRepository(final URI uri)
    {
        this(uri, Charset.forName("ISO-8859-1"));
    }

    /**
     * Erstellt ein neues {@link JreHttpClientRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     * @param charset {@link Charset}; Kodierung
     */
    public JreHttpClientRepository(final URI uri, final Charset charset)
    {
        super(uri, charset);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // @formatter:off
        HttpClient.Builder builder = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NEVER)
                .proxy(ProxySelector.getDefault())
                .connectTimeout(Duration.ofSeconds(5))
                .executor(executorService)
                ;
        // @formatter:on
        // .authenticator(Authenticator.getDefault())
        // .cookieHandler(CookieHandler.getDefault())
        // .sslContext(SSLContext.getDefault())
        // .sslParameters(new SSLParameters())

        this.httpClient = builder.build();
    }

    /**
     * @see de.freese.maven.proxy.old.repository.http.AbstractHttpRepository#existImpl(de.freese.maven.proxy.old.model.MavenRequest)
     */
    @Override
    protected MavenResponse existImpl(final MavenRequest mavenRequest) throws Exception
    {
        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri())
                .method("HEAD", BodyPublishers.ofString(mavenRequest.getHttpUri()))

                .build();
        // @formatter:on

        HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());

        if (response.statusCode() != AbstractMavenHttpObject.HTTP_OK)
        {
            return null;
        }

        MavenResponse mavenResponse = MavenResponse.create(response);

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
        // // url.append(mavenRequest.getHttpUri());
        //
        // URI uri = new URI(url.toString());
        // Proxy proxy = ProxySelector.getDefault().select(uri).get(0);
        //
        // HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection(proxy);
        //
        // // Header kopieren.
        // mavenRequest.getHeaders().forEach((headerName, headerValue) -> {
        // String actualHeaderValue = Optional.ofNullable(headerValue).orElse("");
        // connection.addRequestProperty(headerName, actualHeaderValue);
        // });
        //
        // connection.setRequestMethod("HEAD");
        // connection.setDoInput(true);
        // connection.setInstanceFollowRedirects(true);
        // connection.connect();
        //
        // // Response lesen.
        // mavenResponse = MavenResponse.create(connection);
        // // try (InputStream response = connection.getInputStream();
        // // BufferedReader reader = new BufferedReader(new InputStreamReader(response)))
        // // {
        // // mavenResponse = MavenResponse.create(reader);
        // // }
        //
        // connection.disconnect();

        return mavenResponse;
    }

    /**
     * @see de.freese.maven.proxy.old.repository.http.AbstractHttpRepository#getResourceImpl(de.freese.maven.proxy.old.model.MavenRequest)
     */
    @Override
    protected MavenResponse getResourceImpl(final MavenRequest mavenRequest) throws Exception
    {
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

        // @formatter:off
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        // @formatter:on

        HttpResponse<InputStream> response = this.httpClient.send(request, BodyHandlers.ofInputStream());

        if (response.statusCode() != AbstractMavenHttpObject.HTTP_OK)
        {
            return null;
        }

        int contentLength = response.headers().firstValue("content-length").map(Integer::parseInt).orElse(0);

        MavenResponse mavenResponse = MavenResponse.create(response);

        // Response lesen.
        try (InputStream input = response.body())
        {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : 16 * 1024))
            {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead = -1;

                while ((bytesRead = input.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, bytesRead);
                }

                baos.flush();

                mavenResponse.setResource(baos.toByteArray());
            }
        }

        return mavenResponse;
    }
}
