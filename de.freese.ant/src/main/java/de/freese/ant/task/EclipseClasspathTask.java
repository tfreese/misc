package de.freese.ant.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Creates an Ant Path-like structure based on the entries in an Eclipse .classpath file.
 * 
 * @author Thomas Freese
 */
public class EclipseClasspathTask extends Task
{
	/**
	 * 
	 */
	public static final String DEFAULT_FILENAME = ".classpath";

	/**
	 * 
	 */
	private File dir = null;

	/**
	 *
	 */
	private String fileName = DEFAULT_FILENAME;

	/**
	 * 
	 */
	private String pathID = null;

	/**
     * 
     */
	private List<String> prefFiles = null;

	/**
	 * 
	 */
	private boolean verbose = false;

	/**
	 * 
	 */
	private File workspace = null;

	/**
	 * Creates a new {@link EclipseClasspathTask} object.
	 */
	public EclipseClasspathTask()
	{
		super();

		this.prefFiles = new ArrayList<String>();

		this.prefFiles.add(".metadata/.plugins/org.eclipse.jdt.core/prefs.ini");
		this.prefFiles
				.add(".metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.jdt.core.prefs");
		this.prefFiles.add(".metadata/.plugins/org.eclipse.jdt.core/pref_store.ini");
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException
	{
		// If no _workspace was provided, use the default
		if (this.workspace == null)
		{
			log("Workspace is not set !!!");

			return;
		}

		// Get a mapping of Eclipse library variables
		Map<String, String> variables = getVariables();

		Document document = null;
		File inFile = null;

		/*
		 * Now create a file on the Eclipse classpath file (probably .classpath) and get a JDOM
		 * document from it.
		 */
		try
		{
			if (this.dir == null)
			{
				this.dir = getProject().getBaseDir();
			}

			inFile = new File(this.dir, this.fileName);

			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inFile);
		}
		catch (FileNotFoundException ex)
		{
			log("Unable to open Eclipse classpath file [" + inFile.getAbsolutePath() + "]");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		if (document == null)
		{
			return;
		}

		// This is the Ant Path-like structure
		Path classPath = (Path) getProject().getReference(this.pathID);

		if (classPath == null)
		{
			classPath = new Path(getProject());
		}

		// Get all the elements that relate to the classpath and walk them
		NodeList nodelList = document.getElementsByTagName("classpathentry");

		if (nodelList != null)
		{
			for (int i = 0; i < nodelList.getLength(); i++)
			{
				Element element = (Element) nodelList.item(i);

				String kind = element.getAttribute("kind");
				String path = element.getAttribute("path");

				String location = null;

				/*
				 * If the "kind" attribute is either "var" or "lib" we can process it. The "lib"
				 * types are just copied straight from the attribute. The "var" types have to be
				 * mapped via the variable mapping. Any other types, we skip.
				 */
				if (kind.equals("var"))
				{
					location = variables.get(kind);

					if (location == null)
					{
						log("Unable to map variable [" + kind + "]");

						continue;
					}

					if (isVerbose())
					{
						log("Adding " + location + ", (" + kind + ")");
					}
				}
				else if (kind.equals("lib"))
				{
					location = path;

					if (isVerbose())
					{
						log("Adding " + location);
					}
				}
				else
				{
					if (isVerbose())
					{
						log("Skipping entry of type: [" + kind + "]");
					}

					continue;
				}

				try
				{
					File file = new File(location);

					if (!file.isAbsolute())
					{
						file = new File(this.dir, location);
					}

					if (!file.exists())
					{
						file = new File(this.workspace.getAbsolutePath(), location);
					}

					Path.PathElement e = classPath.createPathElement();

					e.setLocation(file);
				}
				catch (Exception e)
				{
					log("Error processing [" + location + "]: " + e.getMessage());
				}
			}
		}

		if (this.pathID == null)
		{
			log("PathID is not set !!!");

			return;
		}

		// Set the new path into the project
		getProject().addReference(this.pathID, classPath);

		if (isVerbose())
		{
			log("\n" + this.pathID + ": " + classPath.toString());
		}
	}

	/**
	 * Find and parse the Eclipse prefs.ini file which contains the library variables.
	 * 
	 * @return Map of variables to paths
	 */
	private Map<String, String> getVariables()
	{
		Map<String, String> vars = new HashMap<String, String>();

		File prefs = null;

		/*
		 * Because the Eclipse folks keep moving the preferences files around, we have to look in a
		 * few different places...
		 */
		for (String element : this.prefFiles)
		{
			prefs = new File(this.workspace, element);

			if (isVerbose())
			{
				log("Checking " + prefs.toString());
			}

			if (prefs.exists())
			{
				break;
			}
		}

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(prefs));

			String line = null;
			Pattern valRegex = Pattern.compile("/?(\\w).?:(.+)");

			while ((line = in.readLine()) != null)
			{
				int index = line.indexOf("classpathVariable");

				if (index >= 0)
				{
					String slice = line.substring(index);
					String[] chunks = slice.split("=");

					if (chunks.length == 2)
					{
						String name = chunks[0].substring(chunks[0].lastIndexOf(".") + 1);

						String value = null;

						/*
						 * The variables, at least on Win32, have the \ and : reversed and some have
						 * a / at the head of the string. I don't know why. This "corrects" that.
						 */
						Matcher valMatcher = valRegex.matcher(chunks[1]);

						if (valMatcher.matches())
						{
							value = valMatcher.group(1) + ":" + valMatcher.group(2);
						}
						else
						{
							value = chunks[1];
						}

						vars.put(name, value);
					}
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			if (prefs != null)
			{
				log("Unable to find Eclipse preferences file at [" + prefs.getAbsolutePath() + "]");
			}
			else
			{
				log("Unable to find Eclipse preferences file");
			}

		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return vars;
	}

	/**
	 * @return Returns the verbose.
	 */
	private boolean isVerbose()
	{
		return this.verbose;
	}

	/**
	 * @param directory {@link File}
	 */
	public void setDir(final File directory)
	{
		this.dir = directory;
	}

	/**
	 * @param fileName String
	 */
	public void setFileName(final String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 * @param pathName String
	 */
	public void setPathID(final String pathName)
	{
		this.pathID = pathName;
	}

	/**
	 * @param verbose boolean
	 */
	public void setVerbose(final boolean verbose)
	{
		this.verbose = verbose;
	}

	/**
	 * @param workspace {@link File}
	 */
	public void setWorkspace(final File workspace)
	{
		this.workspace = workspace;
	}
}
