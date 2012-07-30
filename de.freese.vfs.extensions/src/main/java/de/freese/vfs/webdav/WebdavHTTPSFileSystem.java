/**
 * Created: 24.03.2012
 */

package de.freese.vfs.webdav;

import java.util.Collection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.commons.vfs2.provider.http.HttpFileSystem;

/**
 * {@link FileSystem} mit Unterstützung für HTTPS Verbindungen.
 * 
 * @author Thomas Freese
 */
public class WebdavHTTPSFileSystem extends HttpFileSystem
{
	/**
	 * Erstellt ein neues {@link WebdavHTTPSFileSystem} Object.
	 * 
	 * @param rootName {@link GenericFileName}
	 * @param client {@link HttpClient}
	 * @param fileSystemOptions {@link FileSystemOptions}
	 */
	WebdavHTTPSFileSystem(final GenericFileName rootName, final HttpClient client,
			final FileSystemOptions fileSystemOptions)
	{
		super(rootName, client, fileSystemOptions);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileSystem#addCapabilities(java.util.Collection)
	 */
	@Override
	protected void addCapabilities(final Collection<Capability> caps)
	{
		caps.addAll(WebdavHTTPSFileProvider.CAPABILITIES);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileSystem#createFile(org.apache.commons.vfs2.provider.AbstractFileName)
	 */
	@Override
	protected FileObject createFile(final AbstractFileName name) throws Exception
	{
		return new WebdavHTTPSFileObject(name, this);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileSystem#getClient()
	 */
	@Override
	public HttpClient getClient()
	{
		return super.getClient();
	}
}
