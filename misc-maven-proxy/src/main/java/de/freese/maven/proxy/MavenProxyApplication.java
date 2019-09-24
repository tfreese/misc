/**
 * Created: 28.12.2011
 */
package de.freese.maven.proxy;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.util.MavenProxyThreadFactory;
import de.freese.maven.proxy.util.ProxyUtils;

/**
 * Startet den Maven Proxy.<br>
 * Maven Konfiguration:
 *
 * <pre>
 * &lt;mirror&gt;
 *   &lt;id&gt;local-proxy&lt;/id>&gt;
 *   &lt;name&gt;local-proxy&lt;/name&gt;
 *   &lt;url&gt;http://localhost:7999&lt;/url&gt;
 *   &lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 * &lt;/mirror&gt;
 * </pre>
 *
 * curl -v localhost:7999/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.pom<br>
 * curl -v -X GET localhost:7999/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.pom<br>
 * curl -v -X PUT localhost:7999 -d "..."<br>
 *
 * @author Thomas Freese
 */
public class MavenProxyApplication
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyApplication.class);

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        String directory = System.getProperty("mavenproxy.directory");
        // directory = "/mnt/ssd512GB/maven-proxy";

        if ((directory == null) || directory.isBlank())
        {
            LOGGER.error("A WorkingDirectory must be set by '-Dmavenproxy.directory=...'");
            return;
        }

        Path workingDirectory = Paths.get(directory);

        if (!Files.isWritable(workingDirectory))
        {
            LOGGER.error("The WorkingDirectory does not exist or is not writeable: {}", directory);
            return;
        }

        Integer port = Integer.getInteger("mavenproxy.port");

        if (port == null)
        {
            LOGGER.error("A Port must be set by '-Dmavenproxy.port=...'");
            return;
        }

        // ProxyUtils.setupProxy();
        // Charset charset = Charset.forName("ISO-8859-1");
        // Charset charset = StandardCharsets.ISO_8859_1;

        // int poolSize = Math.max(16, Runtime.getRuntime().availableProcessors() * 2);
        int poolSize = 16;

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize, new MavenProxyThreadFactory());

        MavenProxy proxy = new MavenProxy();
        proxy.setPort(port);
        proxy.setWorkingDirectory(workingDirectory);
        proxy.setExecutorService(executorService);
        proxy.addHttpRepository(URI.create("https://repo1.maven.org/maven2"));
        proxy.addHttpRepository(URI.create("https://repo.spring.io/libs-snapshot"));
        proxy.addHttpRepository(URI.create("https://repository.primefaces.org"));

        new Thread(proxy::start, "Maven-Proxy").start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            proxy.stop();

            ProxyUtils.shutdown(executorService, LOGGER);
        }, "Shutdown"));
    }
}
