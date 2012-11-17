/**
 * Created: 28.06.2012
 */

package de.freese.docbook;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.junit.Test;

/**
 * @author Thomas Freese
 */
public class TestJavaDoc
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		TestJavaDoc testJavaDoc = new TestJavaDoc();
		testJavaDoc.testJavaDoc();
	}

	/**
	 * Erstellt ein neues {@link TestJavaDoc} Object.
	 */
	public TestJavaDoc()
	{
		super();
	}

	/**
	 * @throws Exception Falls was schief geht.
	 */
	@Test
	public void testJavaDoc() throws Exception
	{
		URL helpURL = new File("mydocu.jar").toURI().toURL();
		System.out.println(helpURL);

		try (URLClassLoader classLoader = new URLClassLoader(new URL[]
		{
			helpURL
		}))
		{
			URL helpSetURL = new URL("jar:" + helpURL.toExternalForm() + "!/jhelpset.hs");

			JHelp helpViewer = new JHelp(new HelpSet(classLoader, helpSetURL));

			JFrame frame = new JFrame();
			frame.setTitle("IPPS Help");
			frame.getContentPane().add(helpViewer);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(1024, 800);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}
}
