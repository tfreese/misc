package cloudsession;

import javax.servlet.http.HttpSession;

/**
 * Interface für Zugriff auf Inhalt der {@link HttpSession}.
 * 
 * @author Thomas Freese
 */
public interface CloudSession
{
	/**
	 * @param sessionID String
	 * @param name String
	 * @return Object
	 */
	public Object getSessionValue(String sessionID, String name);

	/**
	 * @param sessionID String
	 */
	public void remove(String sessionID);

	/**
	 * @param sessionID String
	 * @param name String
	 * @param value Object
	 */
	public void setSessionValue(String sessionID, String name, Object value);
}
