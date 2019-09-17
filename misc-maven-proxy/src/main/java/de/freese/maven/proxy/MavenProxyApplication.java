/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import de.freese.maven.proxy.netty.NettyMavenProxy;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.JreHttpClientRepository;

/**
 * Startet den Maven Proxy.<br>
 * Maven Konfiguration:
 *
 * <pre>
 * &lt;mirror&gt;
 *   &lt;id&gt;myProxy&lt;/id>&gt;
 *   &lt;name&gt;myProxy&lt;/name&gt;
 *   &lt;url&gt;http://localhost:8080&lt;/url&gt;
 *   &lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 * &lt;/mirror&gt;
 * </pre>
 *
 * @author Thomas Freese
 */
public class MavenProxyApplication
{
    /**
     *
     */
    static void enableProxy()
    {
        String proxyHost = "194.114.63.23";
        String proxyPort = "8080";
        String nonProxyHosts = "localhost|127.0.0.1";
        String userID = "USER";
        String password = "...";
        // String domain = "DOMAIN";

        System.setProperty("java.net.useSystemProxies", "true");

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // System.setProperty("http.proxyUser", userID);
        // System.setProperty("http.proxyPassword", password);
        // System.setProperty("http.auth.ntlm.domain", domain);
        System.setProperty("http.nonProxyHosts", nonProxyHosts);

        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        // System.setProperty("https.proxyUser", userID);
        // System.setProperty("https.proxyPassword", password);
        // System.setProperty("https.auth.ntlm.domain", domain);
        System.setProperty("https.nonProxyHosts", nonProxyHosts);

        // Bei Fehler: java.net.ProtocolException: Server redirected too many times (20)
        // System.setProperty("http.maxRedirects", "99");
        // Default cookie manager.
        // CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        // String encoded = new String(Base64.encodeBase64((getHTTPUsername() + ":" + getHTTPPassword()).getBytes()));
        // con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
        java.net.Authenticator.setDefault(new java.net.Authenticator()
        {
            /**
             * @see java.net.Authenticator#getPasswordAuthentication()
             */
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(userID, password.toCharArray());
            }
        });

        // Test
        if (Boolean.getBoolean("java.net.useSystemProxies"))
        {
            try
            {
                URL url = new URL("http://www.google.de");
                // URL url = new URL("https://search.maven.org");

                // Ausgabe verfügbarer Proxies für eine URL.
                List<Proxy> proxies = ProxySelector.getDefault().select(url.toURI());
                proxies.forEach(System.out::println);

                // SocketAddress proxyAddress = new InetSocketAddress("194.114.63.23", 8080);
                // Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
                Proxy proxy = proxies.get(0);

                URLConnection connection = url.openConnection(proxy);
                // URLConnection connection = url.openConnection();

                try (InputStream response = connection.getInputStream();
                     BufferedReader in = new BufferedReader(new InputStreamReader(response)))
                {
                    String line = null;

                    while ((line = in.readLine()) != null)
                    {
                        System.out.println(line);
                    }
                }

                ((HttpURLConnection) connection).disconnect();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // enableProxy();
        // Charset charset = Charset.forName("ISO-8859-1");
        // Charset charset = StandardCharsets.ISO_8859_1;

        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(5, Runtime.getRuntime().availableProcessors() * 2));

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

        HttpClient httpClient = builder.build();

        final MavenProxy proxy = new NettyMavenProxy(executorService, new FileRepository(URI.create("file:///tmp/mavenProxy")),
                List.of(new JreHttpClientRepository(URI.create("http://repo1.maven.org/maven2"), httpClient),
                        new JreHttpClientRepository(URI.create("http://repository.jboss.org/nexus/content/groups/public-jboss"), httpClient),
                        new JreHttpClientRepository(URI.create("https://repository.jboss.org/nexus/content/repositories/releases"), httpClient)));
        proxy.setPort(8085);

        proxy.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            proxy.shutdown();

            executorService.shutdown();

            while (!executorService.isTerminated())
            {
                try
                {
                    executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                }
                catch (InterruptedException ex)
                {
                    // Ignore
                }
            }

            System.exit(0);
        }));
    }
}
