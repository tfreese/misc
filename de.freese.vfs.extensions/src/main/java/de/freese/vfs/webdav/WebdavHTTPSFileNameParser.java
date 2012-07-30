/**
 * Created: 24.03.2012
 */

package de.freese.vfs.webdav;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.URLFileNameParser;

/**
 * Buxfix da VFS nicht berücksichtigt, das '@'Zeichen in Username und Passwort vorkommen können.<br>
 * Ebenso wir in der Defaukt-Implementierung nicht berücksichtigt das einige Webdav-Anbieter<br>
 * die Protokollangabe beim Host benötigen.
 * 
 * @author Thomas Freese
 */
public class WebdavHTTPSFileNameParser extends URLFileNameParser
{
	/**
	 * Erstellt ein neues {@link WebdavHTTPSFileNameParser} Object.
	 */
	public WebdavHTTPSFileNameParser()
	{
		super(80);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.HostFileNameParser#extractHostName(java.lang.StringBuilder)
	 */
	@Override
	protected String extractHostName(final StringBuilder name)
	{
		final int maxlen = name.length();
		int pos = 0;

		for (; pos < maxlen; pos++)
		{
			final char ch = name.charAt(pos);

			if ((ch == '.'))
			{
				break;
			}
		}

		for (; pos < maxlen; pos++)
		{
			final char ch = name.charAt(pos);

			if ((ch == '/') || (ch == ':'))
			{
				break;
			}
		}

		if (pos == 0)
		{
			return null;
		}

		final String hostname = name.substring(0, pos);
		name.delete(0, pos);

		return hostname;
	}

	/**
	 * @see org.apache.commons.vfs2.provider.HostFileNameParser#extractPort(java.lang.StringBuilder,
	 *      java.lang.String)
	 */
	@Override
	protected int extractPort(final StringBuilder name, final String uri)
		throws FileSystemException
	{
		if ((name.length() == 0) || (name.charAt(0) != ':'))
		{
			return getDefaultPort();
		}

		final int maxlen = name.length();
		int pos = 1;

		for (; pos < maxlen; pos++)
		{
			final char ch = name.charAt(pos);

			if ((ch < '0') || (ch > '9'))
			{
				break;
			}
		}

		final String port = name.substring(1, pos);
		name.delete(0, pos);
		//
		// if (port.length() == 0)
		// {
		// throw new FileSystemException("vfs.provider/missing-port.error", uri);
		// }
		//
		return Integer.parseInt(port);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.HostFileNameParser#extractUserInfo(java.lang.StringBuilder)
	 */
	@Override
	protected String extractUserInfo(final StringBuilder name)
	{
		int lastAtPos = name.lastIndexOf("@");

		if (lastAtPos == -1)
		{
			return null;
		}

		String userInfo = name.substring(0, lastAtPos);
		name.delete(0, lastAtPos + 1);

		return userInfo;
	}
}
