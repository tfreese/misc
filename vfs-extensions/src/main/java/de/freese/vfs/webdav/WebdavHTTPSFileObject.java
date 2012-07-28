/**
 * Created: 24.03.2012
 */

package de.freese.vfs.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.vfs2.FileContentInfoFactory;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.NameScope;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.DefaultFileContent;
import org.apache.commons.vfs2.provider.URLFileName;
import org.apache.commons.vfs2.provider.http.HttpFileObject;
import org.apache.commons.vfs2.provider.webdav.ExceptionConverter;
import org.apache.commons.vfs2.provider.webdav.WebdavMethodRetryHandler;
import org.apache.commons.vfs2.util.FileObjectUtils;
import org.apache.commons.vfs2.util.MonitorOutputStream;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.CheckinMethod;
import org.apache.jackrabbit.webdav.client.methods.CheckoutMethod;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.MoveMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PropPatchMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.apache.jackrabbit.webdav.client.methods.UncheckoutMethod;
import org.apache.jackrabbit.webdav.client.methods.VersionControlMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.w3c.dom.Node;

/**
 * {@link FileObject} mit Unterstützung für HTTPS Verbindungen.
 * 
 * @author Thomas Freese
 */
public class WebdavHTTPSFileObject extends HttpFileObject
{
	/**
	 * @author Thomas Freese
	 */
	private class MyWebdavOutputStream extends MonitorOutputStream
	{
		/**
		 * 
		 */
		private final WebdavHTTPSFileObject fileObject;

		/**
		 * Erstellt ein neues {@link MyWebdavOutputStream} Object.
		 * 
		 * @param fileObject {@link WebdavHTTPSFileObject}
		 */
		public MyWebdavOutputStream(final WebdavHTTPSFileObject fileObject)
		{
			super(new ByteArrayOutputStream());

			this.fileObject = fileObject;
		}

		/**
		 * @param urlStr String
		 * @return boolean
		 */
		private boolean createVersion(final String urlStr)
		{
			try
			{
				VersionControlMethod method = new VersionControlMethod(urlStr);
				setupMethod(method);
				execute(method);

				return true;
			}
			catch (Exception ex)
			{
				return false;
			}
		}

		/**
		 * @see org.apache.commons.vfs2.util.MonitorOutputStream#onClose()
		 */
		@Override
		protected void onClose() throws IOException
		{
			RequestEntity entity =
					new ByteArrayRequestEntity(((ByteArrayOutputStream) this.out).toByteArray());
			URLFileName fileName = getName();
			String urlStr = urlString(fileName, false);

			if (WebdavHTTPSFileObject.this.builder.isVersioning(getFileSystem()
					.getFileSystemOptions()))
			{
				DavPropertySet set = null;
				boolean fileExists = true;
				boolean isCheckedIn = true;

				try
				{
					set = getPropertyNames(fileName);
				}
				catch (FileNotFoundException ex)
				{
					fileExists = false;
				}

				if (fileExists && (set != null))
				{
					if (set.contains(VersionControlledResource.CHECKED_OUT))
					{
						isCheckedIn = false;
					}
					else if (!set.contains(VersionControlledResource.CHECKED_IN))
					{
						DavProperty<?> prop = set.get(VersionControlledResource.AUTO_VERSION);

						if (prop != null)
						{
							prop = getProperty(fileName, VersionControlledResource.AUTO_VERSION);

							if (DeltaVConstants.XML_CHECKOUT_CHECKIN.equals(prop.getValue()))
							{
								createVersion(urlStr);
							}
						}
					}
				}

				if (fileExists && isCheckedIn)
				{
					try
					{
						CheckoutMethod checkout = new CheckoutMethod(urlStr);
						setupMethod(checkout);
						execute(checkout);
						isCheckedIn = false;
					}
					catch (FileSystemException ex)
					{
						// Ignore the exception checking out.
					}
				}

				try
				{
					PutMethod method = new PutMethod(urlStr);
					method.setRequestEntity(entity);
					setupMethod(method);
					execute(method);
					setUserName(fileName, urlStr);
				}
				catch (FileSystemException ex)
				{
					if (!isCheckedIn)
					{
						try
						{
							UncheckoutMethod method = new UncheckoutMethod(urlStr);
							setupMethod(method);
							execute(method);
							isCheckedIn = true;
						}
						catch (Exception ex2)
						{
							// Ignore the exception. Going to throw original.
						}

						throw ex;
					}
				}

				if (!fileExists)
				{
					createVersion(urlStr);

					try
					{
						DavPropertySet props = getPropertyNames(fileName);
						isCheckedIn = !props.contains(VersionControlledResource.CHECKED_OUT);
					}
					catch (FileNotFoundException ex)
					{
						// Ignore the error
					}
				}

				if (!isCheckedIn)
				{
					CheckinMethod checkin = new CheckinMethod(urlStr);
					setupMethod(checkin);
					execute(checkin);
				}
			}
			else
			{
				PutMethod method = new PutMethod(urlStr);
				method.setRequestEntity(entity);
				setupMethod(method);
				execute(method);

				try
				{
					setUserName(fileName, urlStr);
				}
				catch (IOException e)
				{
					// Ignore the exception if unable to set the user name.
				}
			}

			((DefaultFileContent) this.fileObject.getContent()).resetAttributes();
		}

		/**
		 * @param fileName {@link URLFileName}
		 * @param urlStr String
		 * @throws IOException Falls was schief geht.
		 */
		private void setUserName(final URLFileName fileName, final String urlStr)
			throws IOException
		{
			List<DefaultDavProperty<?>> list = new ArrayList<>();
			String name =
					WebdavHTTPSFileObject.this.builder.getCreatorName(getFileSystem()
							.getFileSystemOptions());
			String userName = fileName.getUserName();

			if (name == null)
			{
				name = userName;
			}
			else
			{
				if (userName != null)
				{
					String comment = "Modified by user " + userName;
					list.add(new DefaultDavProperty<>(DeltaVConstants.COMMENT, comment));
				}
			}

			list.add(new DefaultDavProperty<>(DeltaVConstants.CREATOR_DISPLAYNAME, name));
			PropPatchMethod method = new PropPatchMethod(urlStr, list);
			setupMethod(method);
			execute(method);
		}
	}

	/**
	 * 
	 */
	public static final DavPropertyName RESPONSE_CHARSET = DavPropertyName
			.create("response-charset");

	/**
	  * 
	  */
	private final WebdavHTTPSFileSystemConfigBuilder builder;

	/**
	 * 
	 */
	private final String urlCharset;

	/**
	 * Erstellt ein neues {@link WebdavHTTPSFileObject} Object.
	 * 
	 * @param name {@link AbstractFileName}
	 * @param fileSystem {@link WebdavHTTPSFileSystem}
	 */
	public WebdavHTTPSFileObject(final AbstractFileName name, final WebdavHTTPSFileSystem fileSystem)
	{
		super(name, fileSystem);

		this.builder = WebdavHTTPSFileSystemConfigBuilder.getInstance();
		this.urlCharset = this.builder.getUrlCharset(getFileSystem().getFileSystemOptions());
	}

	/**
	 * @param httpMethod {@link HttpMethodBase}
	 */
	protected void configureMethod(final HttpMethodBase httpMethod)
	{
		httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				WebdavMethodRetryHandler.getInstance());
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doCreateFolder()
	 */
	@Override
	protected void doCreateFolder() throws Exception
	{
		DavMethod method = new MkColMethod(urlString(getName(), false));
		setupMethod(method);

		try
		{
			execute(method);
		}
		catch (FileSystemException ex)
		{
			throw new FileSystemException("vfs.provider.webdav/create-collection.error", getName(),
					ex);
		}
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doDelete()
	 */
	@Override
	protected void doDelete() throws Exception
	{
		DavMethod method = new DeleteMethod(urlString(getName(), false));
		setupMethod(method);
		execute(method);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doGetAttributes()
	 */
	@Override
	protected Map<String, Object> doGetAttributes() throws Exception
	{
		final Map<String, Object> attributes = new HashMap<>();

		try
		{
			URLFileName fileName = getName();
			DavPropertySet properties =
					getProperties(fileName, DavConstants.PROPFIND_ALL_PROP,
							new DavPropertyNameSet(), false);

			// iterator() is documented to return DavProperty instances
			DavPropertyIterator iter = properties.iterator();

			while (iter.hasNext())
			{
				DavProperty<?> property = iter.next();
				attributes.put(property.getName().toString(), property.getValue());
			}

			properties = getPropertyNames(fileName);

			DavPropertyIterator iter2 = properties.iterator();

			while (iter2.hasNext())
			{
				DavProperty<?> property = iter2.next();

				if (!attributes.containsKey(property.getName().getName()))
				{
					property = getProperty(fileName, property.getName());

					if (property != null)
					{
						Object name = property.getName();
						Object value = property.getValue();

						if ((name != null) && (value != null))
						{
							attributes.put(name.toString(), value);
						}
					}
				}
			}

			return attributes;
		}
		catch (Exception ex)
		{
			throw new FileSystemException("vfs.provider.webdav/propfind.error", getName(), ex);
		}
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileObject#doGetContentSize()
	 */
	@Override
	protected long doGetContentSize() throws Exception
	{
		DavProperty<?> property = getProperty(getName(), DavConstants.PROPERTY_GETCONTENTLENGTH);

		if (property != null)
		{
			String value = (String) property.getValue();

			return Long.parseLong(value);
		}

		return 0;
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileObject#doGetLastModifiedTime()
	 */
	@Override
	protected long doGetLastModifiedTime() throws Exception
	{
		DavProperty<?> property = getProperty(getName(), DavConstants.PROPERTY_GETLASTMODIFIED);

		if (property != null)
		{
			String value = (String) property.getValue();

			return DateUtil.parseDate(value).getTime();
		}

		return 0;
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doGetOutputStream(boolean)
	 */
	@Override
	protected OutputStream doGetOutputStream(final boolean bAppend) throws Exception
	{
		return new MyWebdavOutputStream(this);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileObject#doGetType()
	 */
	@Override
	protected FileType doGetType() throws Exception
	{
		try
		{
			return isDirectory(getName()) ? FileType.FOLDER : FileType.FILE;
		}
		catch (FileNotFolderException ex)
		{
			return FileType.IMAGINARY;
		}
		catch (FileNotFoundException ex)
		{
			return FileType.IMAGINARY;
		}
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileObject#doListChildren()
	 */
	@Override
	protected String[] doListChildren() throws Exception
	{
		// use doListChildrenResolved for performance
		return null;
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doListChildrenResolved()
	 */
	@Override
	protected FileObject[] doListChildrenResolved() throws Exception
	{
		PropFindMethod method = null;

		try
		{
			URLFileName name = getName();

			if (isDirectory(name))
			{
				DavPropertyNameSet nameSet = new DavPropertyNameSet();
				nameSet.add(DavPropertyName.create(DavConstants.PROPERTY_DISPLAYNAME));

				method = new PropFindMethod(urlString(name, false), nameSet, DavConstants.DEPTH_1);
				execute(method);

				List<WebdavHTTPSFileObject> vfs = new ArrayList<>();

				if (method.succeeded())
				{
					MultiStatusResponse[] responses =
							method.getResponseBodyAsMultiStatus().getResponses();

					for (int i = 0; i < responses.length; ++i)
					{
						MultiStatusResponse response = responses[i];

						if (isCurrentFile(response.getHref(), name))
						{
							continue;
						}

						String resourceName = resourceName(response.getHref());

						if ((resourceName != null) && (resourceName.length() > 0))
						{
							WebdavHTTPSFileObject fo =
									(WebdavHTTPSFileObject) FileObjectUtils
											.getAbstractFileObject(getFileSystem().resolveFile(
													getFileSystem().getFileSystemManager()
															.resolveName(getName(), resourceName,
																	NameScope.CHILD)));
							vfs.add(fo);
						}
					}
				}

				return vfs.toArray(new WebdavHTTPSFileObject[vfs.size()]);
			}

			throw new FileNotFolderException(getName());
		}
		catch (FileNotFolderException ex)
		{
			throw ex;
		}
		catch (DavException ex)
		{
			throw new FileSystemException(ex.getMessage(), ex);
		}
		catch (IOException ex)
		{
			throw new FileSystemException(ex.getMessage(), ex);
		}
		finally
		{
			if (method != null)
			{
				method.releaseConnection();
			}
		}
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doRename(org.apache.commons.vfs2.FileObject)
	 */
	@Override
	protected void doRename(final FileObject newfile) throws Exception
	{
		String url = encodePath(urlString(getName(), false));
		String dest = urlString((URLFileName) newfile.getName(), false);
		DavMethod method = new MoveMethod(url, dest, false);
		setupMethod(method);
		execute(method);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#doSetAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	protected void doSetAttribute(final String attrName, final Object value) throws Exception
	{
		try
		{
			URLFileName fileName = getName();
			String urlStr = urlString(fileName, false);
			DavPropertySet properties = new DavPropertySet();
			DavPropertyNameSet propertyNameSet = new DavPropertyNameSet();
			DavProperty<?> property =
					new DefaultDavProperty<>(attrName, value, Namespace.EMPTY_NAMESPACE);

			if (value != null)
			{
				properties.add(property);
			}
			else
			{
				propertyNameSet.add(property.getName()); // remove property
			}

			PropPatchMethod method = new PropPatchMethod(urlStr, properties, propertyNameSet);
			setupMethod(method);
			execute(method);

			if (!method.succeeded())
			{
				throw new FileSystemException("Property '" + attrName + "' could not be set.");
			}
		}
		catch (FileSystemException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new FileSystemException("vfs.provider.webdav/propfind.error", getName(), ex);
		}
	}

	/**
	 * @param method {@link DavMethod}
	 * @throws FileSystemException Falls was schief geht.
	 */
	private void execute(final DavMethod method) throws FileSystemException
	{
		try
		{
			int status = getFileSystem().getClient().executeMethod(method);

			if ((status == HttpURLConnection.HTTP_NOT_FOUND)
					|| (status == HttpURLConnection.HTTP_GONE))
			{
				throw new FileNotFoundException(method.getURI());
			}

			method.checkSuccess();
		}
		catch (FileSystemException ex)
		{
			throw ex;
		}
		catch (IOException ex)
		{
			throw new FileSystemException(ex);
		}
		catch (DavException ex)
		{
			throw ExceptionConverter.generate(ex);
		}
		finally
		{
			if (method != null)
			{
				method.releaseConnection();
			}
		}
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileObject#getFileContentInfoFactory()
	 */
	@Override
	protected FileContentInfoFactory getFileContentInfoFactory()
	{
		return new WebdavHTTPSFileContentInfoFactory();
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#getFileSystem()
	 */
	@Override
	public WebdavHTTPSFileSystem getFileSystem()
	{
		return (WebdavHTTPSFileSystem) super.getFileSystem();
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileObject#getName()
	 */
	@Override
	public URLFileName getName()
	{
		return (URLFileName) super.getName();
	}

	/**
	 * @param name {@link URLFileName}
	 * @param nameSet {@link DavPropertyNameSet}
	 * @param addEncoding boolean
	 * @return {@link DavPropertySet}
	 * @throws FileSystemException Falls was schief geht.
	 */
	DavPropertySet getProperties(final URLFileName name, final DavPropertyNameSet nameSet,
									final boolean addEncoding) throws FileSystemException
	{
		return getProperties(name, DavConstants.PROPFIND_BY_PROPERTY, nameSet, addEncoding);
	}

	/**
	 * @param name {@link URLFileName}
	 * @param type int
	 * @param nameSet {@link DavPropertyNameSet}
	 * @param addEncoding boolean
	 * @return {@link DavPropertySet}
	 * @throws FileSystemException Falls was schief geht.
	 */
	DavPropertySet getProperties(final URLFileName name, final int type,
									final DavPropertyNameSet nameSet, final boolean addEncoding)
		throws FileSystemException
	{
		try
		{
			String urlStr = urlString(name, false);
			PropFindMethod method = new PropFindMethod(urlStr, type, nameSet, DavConstants.DEPTH_0);
			setupMethod(method);
			execute(method);

			if (method.succeeded())
			{
				MultiStatus multiStatus = method.getResponseBodyAsMultiStatus();
				MultiStatusResponse response = multiStatus.getResponses()[0];
				DavPropertySet props = response.getProperties(HttpStatus.SC_OK);

				if (addEncoding)
				{
					DavProperty<String> prop =
							new DefaultDavProperty<>(RESPONSE_CHARSET, method.getResponseCharSet());
					props.add(prop);
				}

				return props;
			}

			return new DavPropertySet();
		}
		catch (FileSystemException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new FileSystemException("vfs.provider.webdav/propfind.error", getName(), ex);
		}
	}

	/**
	 * @param fileName {@link URLFileName}
	 * @param name {@link DavPropertyName}
	 * @return {@link DavProperty}
	 * @throws FileSystemException Falls was schief geht.
	 */
	DavProperty<?> getProperty(final URLFileName fileName, final DavPropertyName name)
		throws FileSystemException
	{
		DavPropertyNameSet nameSet = new DavPropertyNameSet();
		nameSet.add(name);
		DavPropertySet propertySet = getProperties(fileName, nameSet, false);

		return propertySet.get(name);
	}

	/**
	 * @param fileName {@link URLFileName}
	 * @param property String
	 * @return {@link DavProperty}
	 * @throws FileSystemException Falls was schief geht.
	 */
	DavProperty<?> getProperty(final URLFileName fileName, final String property)
		throws FileSystemException
	{
		return getProperty(fileName, DavPropertyName.create(property));
	}

	/**
	 * @param name {@link URLFileName}
	 * @return {@link DavPropertySet}
	 * @throws FileSystemException Falls was schief geht.
	 */
	DavPropertySet getPropertyNames(final URLFileName name) throws FileSystemException
	{
		return getProperties(name, DavConstants.PROPFIND_PROPERTY_NAMES, new DavPropertyNameSet(),
				false);
	}

	/**
	 * @param href String
	 * @param fileName {@link URLFileName}
	 * @return boolean
	 */
	private boolean isCurrentFile(final String href, final URLFileName fileName)
	{
		String name = fileName.getPath();

		if (href.endsWith("/") && !name.endsWith("/"))
		{
			name += "/";
		}

		return href.equals(name);
	}

	/**
	 * @param name {@link URLFileName}
	 * @return boolean
	 * @throws IOException Falls was schief geht.
	 */
	boolean isDirectory(final URLFileName name) throws IOException
	{
		try
		{
			DavProperty<?> property = getProperty(name, DavConstants.PROPERTY_RESOURCETYPE);
			Node node = null;

			if ((property != null) && ((node = (Node) property.getValue()) != null))
			{
				return node.getLocalName().equals(DavConstants.XML_COLLECTION);
			}

			return false;
		}
		catch (FileNotFoundException ex)
		{
			throw new FileNotFolderException(name);
		}
	}

	/**
	 * Returns the resource name from the path.
	 * 
	 * @param path the path to the file.
	 * @return The resource name
	 */
	private String resourceName(final String path)
	{
		String p = path;

		if (p.endsWith("/"))
		{
			p = p.substring(0, path.length() - 1);
		}

		final int i = p.lastIndexOf("/");

		return (i >= 0) ? p.substring(i + 1) : p;
	}

	/**
	 * @see org.apache.commons.vfs2.provider.http.HttpFileObject#setupMethod(org.apache.commons.httpclient.HttpMethod)
	 */
	@Override
	protected void setupMethod(final HttpMethod method) throws FileSystemException, URIException
	{
		String pathEncoded = getName().getPathQueryEncoded(this.urlCharset);
		method.setPath(pathEncoded);
		// All the WebDav methods are EntityEnclosingMethods and are not allowed to redirect.
		method.setFollowRedirects(false);
		method.setRequestHeader("User-Agent", "Jakarta-Commons-VFS");
		method.addRequestHeader("Cache-control", "no-cache");
		method.addRequestHeader("Cache-store", "no-store");
		method.addRequestHeader("Pragma", "no-cache");
		method.addRequestHeader("Expires", "0");
	}

	/**
	 * Convert the FileName to an encoded url String.
	 * 
	 * @param name {@link URLFileName}
	 * @param includeUserInfo boolean, true if user information should be included.
	 * @return The encoded URL String.
	 */
	private String urlString(final URLFileName name, final boolean includeUserInfo)
	{
		return name.getHostName() + name.getPath();
		// String user = null;
		// String password = null;
		//
		// if (includeUserInfo)
		// {
		// user = name.getUserName();
		// password = name.getPassword();
		// }
		//
		// // TODO
		// URLFileName newFile =
		// new URLFileName("https", name.getHostName(), name.getPort(), name.getDefaultPort(),
		// user, password, name.getPath(), name.getType(), name.getQueryString());
		//
		// try
		// {
		// return newFile.getURIEncoded(this.urlCharset);
		// }
		// catch (Exception ex)
		// {
		// return name.getURI();
		// }
	}
}
