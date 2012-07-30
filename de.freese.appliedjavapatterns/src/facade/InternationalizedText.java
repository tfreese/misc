package facade;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Thomas Freese
 */
public class InternationalizedText
{
	/**
     * 
     */
	private static final String DEFAULT_FILE_NAME = "";

	/**
     * 
     */
	private Properties textProperties = new Properties();

	/**
	 * Creates a new {@link InternationalizedText} object.
	 */
	public InternationalizedText()
	{
		this(DEFAULT_FILE_NAME);
	}

	/**
	 * Creates a new {@link InternationalizedText} object.
	 * 
	 * @param fileName String
	 */
	public InternationalizedText(final String fileName)
	{
		super();

		loadProperties(fileName);
	}

	/**
	 * @param key String
	 * @return String
	 */
	public String getProperty(final String key)
	{
		return getProperty(key, "");
	}

	/**
	 * @param key String
	 * @param defaultValue String
	 * @return String
	 */
	public String getProperty(final String key, final String defaultValue)
	{
		return this.textProperties.getProperty(key, defaultValue);
	}

	/**
	 * @param fileName String
	 */
	private void loadProperties(final String fileName)
	{
		try
		{
			FileInputStream input = new FileInputStream(fileName);

			this.textProperties.load(input);
		}
		catch (IOException exc)
		{
			this.textProperties = new Properties();
		}
	}

	/**
	 * @param newFileName String
	 */
	public void setFileName(final String newFileName)
	{
		if (newFileName != null)
		{
			loadProperties(newFileName);
		}
	}
}
