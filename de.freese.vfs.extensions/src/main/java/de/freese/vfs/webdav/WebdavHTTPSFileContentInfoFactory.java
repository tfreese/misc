/**
 * Created: 24.03.2012
 */

package de.freese.vfs.webdav;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileContentInfoFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileContentInfo;
import org.apache.commons.vfs2.util.FileObjectUtils;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

/**
 * Determines the content information for files accessed via WebDAV.
 * 
 * @author Thomas Freese
 */
class WebdavHTTPSFileContentInfoFactory implements FileContentInfoFactory
{
	/**
	 * Erstellt ein neues {@link WebdavHTTPSFileContentInfoFactory} Object.
	 */
	WebdavHTTPSFileContentInfoFactory()
	{
		super();
	}

	/**
	 * @see org.apache.commons.vfs2.FileContentInfoFactory#create(org.apache.commons.vfs2.FileContent)
	 */
	@Override
	public FileContentInfo create(final FileContent fileContent) throws FileSystemException
	{
		WebdavHTTPSFileObject file =
				(WebdavHTTPSFileObject) (FileObjectUtils.getAbstractFileObject(fileContent.getFile()));

		String contentType = null;
		String contentEncoding = null;

		DavPropertyNameSet nameSet = new DavPropertyNameSet();
		nameSet.add(DavPropertyName.GETCONTENTTYPE);
		DavPropertySet propertySet = file.getProperties(file.getName(), nameSet, true);
		DavProperty<?> property = propertySet.get(DavPropertyName.GETCONTENTTYPE);

		if (property != null)
		{
			contentType = (String) property.getValue();
		}

		property = propertySet.get(WebdavHTTPSFileObject.RESPONSE_CHARSET);

		if (property != null)
		{
			contentEncoding = (String) property.getValue();
		}

		return new DefaultFileContentInfo(contentType, contentEncoding);
	}
}
