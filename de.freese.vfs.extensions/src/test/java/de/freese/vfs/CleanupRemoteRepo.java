/**
 * Created: 26.03.2012
 */

package de.freese.vfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.vfs.webdav.WebdavHTTPSFileSystemConfigBuilder;

/**
 * Klasse zum Aufräumen einen HTTPS Webdav Maven Repositories.
 * 
 * @author Thomas Freese
 */
public class CleanupRemoteRepo
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CleanupRemoteRepo.class);

	/**
	 * @param args final String[]
	 */
	public static void main(final String[] args)
	{
		CleanupRemoteRepo cleanup = new CleanupRemoteRepo();

		try
		{
			cleanup.init("https://sd2dav.1und1.de", "", "");
			cleanup.cleanUp("/maven/repository-snapshots");
			cleanup.close();
		}
		catch (Exception ex)
		{
			LOGGER.error(null, ex);
		}
	}

	/**
	 * 
	 */
	private FileObject fileObjectRoot = null;

	/**
	 * 
	 */
	private MyFileSystemManager fsManager = null;

	/**
	 * 
	 */
	private String host = null;

	/**
	 * 
	 */
	private FileSystemOptions opts = null;

	/**
	 * 
	 */
	private final Pattern snaptshotPattern;

	/**
	 * Erstellt ein neues {@link CleanupRemoteRepo} Object.
	 */
	public CleanupRemoteRepo()
	{
		super();

		String regex = "20\\d\\d\\d\\d\\d\\d.\\d\\d\\d\\d\\d\\d";
		this.snaptshotPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * @param rootPath String
	 * @throws FileSystemException Falls was schief geht.
	 */
	public void cleanUp(final String rootPath) throws FileSystemException
	{
		LOGGER.info("");

		this.fileObjectRoot = this.fsManager.resolveFile(getURI(rootPath), this.opts);

		if (!this.fileObjectRoot.exists())
		{
			LOGGER.warn("{} not found", this.host + rootPath);
			return;
		}

		if (!FileType.FOLDER.equals(this.fileObjectRoot.getType()))
		{
			LOGGER.warn("{} is not a folder", rootPath);
			return;
		}

		traverse(this.fileObjectRoot);
	}

	/**
	 * @throws FileSystemException Falls was schief geht.
	 */
	public void close() throws FileSystemException
	{
		LOGGER.info("");

		this.fileObjectRoot.close();
		this.fsManager.close();

		WebdavHTTPSFileSystemConfigBuilder.getInstance().setUserAuthenticator(this.opts, null);
	}

	/**
	 * @param path String
	 * @return String
	 */
	private String getURI(final String path)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("webdavHTTPS://");
		sb.append(this.host);

		if (path != null)
		{
			sb.append(path);
		}

		return sb.toString();
	}

	/**
	 * @param host String
	 * @param userName String
	 * @param password String
	 * @throws FileSystemException Falls was schief geht.
	 */
	public void init(final String host, final String userName, final String password)
		throws FileSystemException
	{
		this.opts = new FileSystemOptions();
		this.host = host;

		WebdavHTTPSFileSystemConfigBuilder.getInstance().setUserAuthenticator(this.opts,
				new StaticUserAuthenticator(null, userName, password));

		this.fsManager = new MyFileSystemManager();
		this.fsManager.init();
	}

	/**
	 * @param snapshotFiles {@link List}
	 * @throws FileSystemException Falls was schief geht.
	 */
	private void removeOldSnapshots(final List<FileObject> snapshotFiles)
		throws FileSystemException
	{
		// Dateien nach Timestamp gruppieren
		TreeMap<String, List<FileObject>> map = new TreeMap<>();

		for (FileObject file : snapshotFiles)
		{
			Matcher matcher = this.snaptshotPattern.matcher(file.getName().getURI());

			if (!matcher.find())
			{
				continue;
			}

			String key = matcher.group();

			List<FileObject> list = map.get(key);

			if (list == null)
			{
				LOGGER.info("Timestamp: {}", key);
				list = new ArrayList<>();
				map.put(key, list);
			}

			list.add(file);
		}

		// Die drei aktuellsten Snapshot Versionen nicht löschen.
		for (int i = 0; i < 3; i++)
		{
			Entry<String, List<FileObject>> entry = map.pollLastEntry();

			if (entry == null)
			{
				continue;
			}

			LOGGER.info("Keep: {}", entry.getKey());

			for (FileObject file : entry.getValue())
			{
				file.close();
			}
		}

		// Was jetzt noch in der TreeMap ist, kann weg.
		for (Entry<String, List<FileObject>> entry : map.entrySet())
		{
			for (FileObject file : entry.getValue())
			{
				LOGGER.info("Delete: {}", trimURI(file));
				file.delete();
				file.close();
			}
		}

		map.clear();
		map = null;
	}

	/**
	 * @param folder {@link FileObject}
	 * @throws FileSystemException Falls was schief geht.
	 */
	private void traverse(final FileObject folder) throws FileSystemException
	{
		FileObject[] childs = folder.getChildren();

		// Zuerst alle SubFolder durchlaufen.
		for (FileObject child : childs)
		{
			if (!FileType.FOLDER.equals(child.getType()))
			{
				continue;
			}

			traverse(child);
		}

		List<FileObject> snapshotFiles = null;

		// Dann alle normalen Dateien im Folder prüfen.
		for (FileObject child : childs)
		{
			if (!FileType.FILE.equals(child.getType()))
			{
				continue;
			}

			Matcher matcher = this.snaptshotPattern.matcher(child.getName().getURI());

			if (matcher.find())
			{
				if (snapshotFiles == null)
				{
					snapshotFiles = new ArrayList<>();
				}

				snapshotFiles.add(child);
			}
			else
			{
				child.close();
			}
		}

		if ((snapshotFiles != null) && !snapshotFiles.isEmpty())
		{
			LOGGER.info("Folder: {}", trimURI(folder));
			removeOldSnapshots(snapshotFiles);
		}

		folder.close();
	}

	/**
	 * Entfernt das Protokoll, Host und RootPath vom Namen.
	 * 
	 * @param fileObject {@link FileObject}
	 * @return FileObject
	 */
	private String trimURI(final FileObject fileObject)
	{
		String uri = fileObject.getName().getURI();
		String rootURI = this.fileObjectRoot.getName().getURI();
		String trimmedURI = uri.replace(rootURI, "");

		return trimmedURI;
	}
}
