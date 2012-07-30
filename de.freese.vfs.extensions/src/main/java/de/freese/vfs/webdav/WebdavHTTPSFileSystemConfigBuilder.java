/**
 * Created: 24.03.2012
 */

package de.freese.vfs.webdav;

import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder;

/**
 * Konfigurationsobjekt für das Webdav-{@link FileSystem}.
 * 
 * @author Thomas Freese
 */
public class WebdavHTTPSFileSystemConfigBuilder extends HttpFileSystemConfigBuilder
{
	/**
	 * 
	 */
	private static final WebdavHTTPSFileSystemConfigBuilder INSTANCE =
			new WebdavHTTPSFileSystemConfigBuilder();

	/**
	 * @return {@link WebdavHTTPSFileSystemConfigBuilder}
	 */
	public static WebdavHTTPSFileSystemConfigBuilder getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Erstellt ein neues {@link WebdavHTTPSFileSystemConfigBuilder} Object.
	 */
	private WebdavHTTPSFileSystemConfigBuilder()
	{
		super("webdav.");
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder#getConfigClass()
	 */
	@Override
	protected Class<? extends FileSystem> getConfigClass()
	{
		return WebdavHTTPSFileSystem.class;
	}

	/**
	 * Return the user name to be associated with changes to the file.
	 * 
	 * @param opts The FileSystem options
	 * @return The creatorName.
	 */
	public String getCreatorName(final FileSystemOptions opts)
	{
		return getString(opts, "creatorName");
	}

	/**
	 * @see #setUserAuthenticator
	 * @param opts The FileSystemOptions.
	 * @return The UserAuthenticator.
	 */
	public UserAuthenticator getUserAuthenticator(final FileSystemOptions opts)
	{
		return DefaultFileSystemConfigBuilder.getInstance().getUserAuthenticator(opts);
	}

	/**
	 * The cookies to add to the request.
	 * 
	 * @param opts The FileSystem options.
	 * @return true if versioning is enabled.
	 */
	public boolean isVersioning(final FileSystemOptions opts)
	{
		return getBoolean(opts, "versioning", false);
	}

	/**
	 * The user name to be associated with changes to the file.
	 * 
	 * @param opts The FileSystem options
	 * @param creatorName The creator name to be associated with the file.
	 */
	public void setCreatorName(final FileSystemOptions opts, final String creatorName)
	{
		setParam(opts, "creatorName", creatorName);
	}

	/**
	 * Sets the user authenticator to get authentication informations.
	 * 
	 * @param opts The FileSystemOptions.
	 * @param userAuthenticator The UserAuthenticator.
	 * @throws FileSystemException if an error occurs setting the UserAuthenticator.
	 */
	public void setUserAuthenticator(final FileSystemOptions opts,
										final UserAuthenticator userAuthenticator)
		throws FileSystemException
	{
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, userAuthenticator);
	}

	/**
	 * Whether to use versioning.
	 * 
	 * @param opts The FileSystem options.
	 * @param versioning true if versioning should be enabled.
	 */
	public void setVersioning(final FileSystemOptions opts, final boolean versioning)
	{
		setParam(opts, "versioning", Boolean.valueOf(versioning));
	}
}
