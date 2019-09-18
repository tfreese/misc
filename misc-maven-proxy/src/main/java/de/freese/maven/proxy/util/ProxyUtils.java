/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public final class ProxyUtils
{
    /**
    *
    */
    public static void enableHttpProxy()
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
     * Shutdown des {@link ExecutorService}.
     *
     * @param executorService {@link ExecutorService}
     * @param logger {@link Logger}
     */
    public static void shutdown(final ExecutorService executorService, final Logger logger)
    {
        logger.info("shutdown ExecutorService");
        executorService.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Timed out while waiting for executorService");
                }

                // Cancel currently executing tasks.
                for (Runnable remainingTask : executorService.shutdownNow())
                {
                    if (remainingTask instanceof Future)
                    {
                        ((Future<?>) remainingTask).cancel(true);
                    }
                }

                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS))
                {
                    logger.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Interrupted while waiting for executorService");
            }

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Erstellt ein neues {@link ProxyUtils} Object.
     */
    private ProxyUtils()
    {
        super();
    }

}
