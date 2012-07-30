/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * Interface eines Repositories.
 * 
 * @author Thomas Freese
 */
public interface IRepository
{
	/**
	 * Beenden und aufräumen.
	 */
	public void dispose();

	/**
	 * Prüft, ob die Datei vorhanden ist.<br>
	 * Diese Methode muss Threadsicher sein !
	 * 
	 * @param mavenRequest {@link MavenRequest}
	 * @return {@link MavenResponse}
	 * @throws Exception Falls was schief geht.
	 */
	public MavenResponse exist(MavenRequest mavenRequest) throws Exception;

	/**
	 * Liefert den eindeitigen Namen des Repositories (URL, Path).
	 * 
	 * @return String
	 */
	public String getName();

	/**
	 * Laden der betreffenden Datei.<br>
	 * Diese Methode muss Threadsicher sein !
	 * 
	 * @param mavenRequest {@link MavenRequest}
	 * @return {@link MavenResponse}
	 * @throws Exception Falls was schief geht.
	 */
	public MavenResponse getResource(MavenRequest mavenRequest) throws Exception;

	/**
	 * Initialisierung NACH dem setzen aller notwendigen Parameter.
	 */
	public void init();

	/**
	 * @return boolean
	 */
	public boolean isActive();

	/**
	 * @param value boolean
	 */
	public void setActive(boolean value);
}
