/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import java.net.URI;
import java.nio.charset.Charset;

/**
 * Interface eines Repositories.
 * 
 * @author Thomas Freese
 */
public interface IRemoteRepository extends IRepository
{
	/**
	 * Zeichensatz für die Codierung.
	 * 
	 * @param charset {@link Charset}
	 */
	public void setCharset(Charset charset);

	/**
	 * Resourenquelle des Repositories.
	 * 
	 * @param value {@link URI}
	 */
	public void setUri(URI value);
}
