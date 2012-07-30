/**
 * 
 */
package de.freese.vfs;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileUtil;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;

import de.freese.vfs.webdav.WebdavHTTPSFileSystemConfigBuilder;

/**
 * @author Thomas Freese
 */
public class Main
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		FileSystemOptions opts = new FileSystemOptions();

		WebdavHTTPSFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts,
				new StaticUserAuthenticator(null, "...", "..."));

		MyFileSystemManager fsManager = new MyFileSystemManager();
		fsManager.init();

		FileObject tempDirRoot = null;
		tempDirRoot = fsManager.resolveFile("webdavHTTPS://https://sd2dav.1und1.de/maven", opts);
		// tempDirRoot = fsManager.getBaseFile();

		System.out.println("tempDirRoot:" + tempDirRoot.toString());

		if (tempDirRoot.exists())
		{
			for (FileObject fileObject : tempDirRoot.getChildren())
			{
				System.out.println("\tChild: " + fileObject.getName());
			}

			// TMP Verzeichniss löschen
			// tempDirRoot.delete(Selectors.EXCLUDE_SELF);
		}
		else
		{
			tempDirRoot.createFolder();
		}

		// Tmp-Datei schreiben.
		FileObject testObject = fsManager.resolveFile(tempDirRoot, "tmp:/bla/test.txt");
		// FileObject testObject = fsManager.resolveFile(tempDirRoot, "bla/test.txt");
		testObject.createFile();
		FileContent fc = testObject.getContent();
		OutputStream outputStream = fc.getOutputStream();
		PrintWriter bw = new PrintWriter(outputStream);
		bw.write("blabla");
		bw.close();
		outputStream.close();
		testObject.close();

		// Tmp-Datei lesen
		outputStream = new ByteArrayOutputStream();
		FileUtil.writeContent(testObject, outputStream);
		testObject.close();

		StringBuilder sb = new StringBuilder();
		sb.append(outputStream);

		System.out.println(sb);
		System.out.println(testObject.getName());

		// Root Verzeichniss schliessen
		tempDirRoot.close();

		// Close löscht alle Dateien.
		// fsManager.close();
	}
}
