/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy;

import java.net.URI;
import java.nio.charset.Charset;

import de.freese.maven.proxy.repository.HTTPRepository;
import de.freese.maven.proxy.repository.IRemoteRepository;
import de.freese.maven.proxy.repository.VirtualRepository;

/**
 * Startet den Maven Proxy.<br>
 * Maven Konfiguration:
 * 
 * <pre>
 * &lt;mirror&gt;
 * 	&lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 * 	&lt;id&gt;myProxy&lt;/id>&gt;
 * 	&lt;name&gt;myProxy&lt;/name&gt;
 * 	&lt;url&gt;http://localhost:8080&lt;/url&gt;
 * &lt;/mirror&gt;
 * </pre>
 * 
 * @author Thomas Freese
 */
public class Main
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		Charset charset = Charset.forName("ISO-8859-1");

		VirtualRepository virtualRepository = new VirtualRepository();

		IRemoteRepository repository = new HTTPRepository();
		repository.setActive(true);
		repository.setUri(new URI("http://repo1.maven.org/maven2"));
		repository.setCharset(charset);
		repository.init();
		virtualRepository.addRepository(repository);

		// repository = new HTTPRepository();
		// repository.setActive(true);
		// repository.setUri(new URI("http://.../maven/repository-thirdparty"));
		// repository.setCharset(charset);
		// repository.init();
		// virtualRepository.addRepository(repository);

		repository = new HTTPRepository();
		repository.setActive(true);
		repository.setUri(new URI("http://projectlombok.org/mavenrepo"));
		repository.setCharset(charset);
		repository.init();
		virtualRepository.addRepository(repository);

		repository = new HTTPRepository();
		repository.setActive(true);
		repository.setUri(new URI("http://repository.jboss.org/nexus/content/groups/public-jboss"));
		repository.setCharset(charset);
		repository.init();
		virtualRepository.addRepository(repository);

		repository = new HTTPRepository();
		repository.setActive(true);
		repository.setUri(new URI(
				"https://repository.jboss.org/nexus/content/repositories/releases"));
		repository.setCharset(charset);
		repository.init();
		virtualRepository.addRepository(repository);

		final MavenProxy proxy = new MavenProxy();
		proxy.setPort(8080);
		proxy.setCharset(charset);
		proxy.setVirtualRepository(virtualRepository);

		proxy.start();

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			/**
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run()
			{
				proxy.shutdown();
			}
		});
	}
}
