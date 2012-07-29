package cloudsession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import util.XmlSerializer;

/**
 * @author Thomas Freese
 */
public class DummySessionService implements CloudSession
{
	/**
	 * 
	 */
	private final String propFileName = "session.properties";

	/**
	 * Erstellt ein neues {@link DummySessionService} Object.
	 */
	public DummySessionService()
	{
		super();
	}

	/**
	 * @return {@link Properties}
	 */
	private Properties getProps()
	{
		Properties props = new Properties();

		try
		{
			File propFile = new File(this.propFileName);

			if (!propFile.exists())
			{
				propFile.createNewFile();
			}

			FileInputStream fis = new FileInputStream(this.propFileName);
			props.load(fis);
			fis.close();

			return props;
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param sessionID String
	 * @param name String
	 * @return String
	 */
	private String getPropsKey(final String sessionID, final String name)
	{
		return "[" + sessionID + "][" + name + "]";
	}

	/**
	 * @see cloudsession.CloudSession#getSessionValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Object getSessionValue(final String sessionID, final String name)
	{
		return XmlSerializer.fromXML(getProps().getProperty(getPropsKey(sessionID, name)));
	}

	/**
	 * @see cloudsession.CloudSession#remove(java.lang.String)
	 */
	@Override
	public void remove(final String sessionID)
	{
		Properties props = getProps();
		Enumeration<Object> keys = props.keys();

		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();

			if (key.contains(sessionID))
			{
				props.remove(key);
			}
		}

		storeProps(props);
	}

	/**
	 * @param props {@link Properties}
	 */
	private void removeOldProps(final Properties props)
	{
		// TODO
		// Enumeration<Object> keys = props.keys();
		// while (keys.hasMoreElements()) {
		//
		// }
	}

	/**
	 * @see cloudsession.CloudSession#setSessionValue(java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setSessionValue(final String sessionID, final String name, final Object value)
	{
		Properties props = getProps();
		removeOldProps(props);
		props.put(getPropsKey(sessionID, name), XmlSerializer.toXML(value));
		storeProps(props);
	}

	/**
	 * @param props {@link Properties}
	 */
	private void storeProps(final Properties props)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(this.propFileName);
			props.store(fos, "comments");
			fos.close();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
