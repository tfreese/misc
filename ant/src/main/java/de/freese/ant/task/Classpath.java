/*
 * Created on 11.11.2003
 * @author Thomas Freese
 */
package de.freese.ant.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Dieser Ant-Task erzeugt aus der .classpath-Datei eines Eclipse Projektes den Classpath, wie er
 * für das Compilieren mit Ant benötigt wird.<br>
 * Das manuelle Anlegen und Pflege des Classpaths entfällt.
 * 
 * @author Thomas Freese
 */
public class Classpath extends Task
{
	/**
	 *
	 */
	private String binPath = "";

	/**
	 * 
	 */
	private String fileName = "";

	/**
	 * 
	 */
	private String outputproperty = "";

	/**
	 * 
	 */
	private String srcPath = "";

	/**
	 * Default Konstruktor
	 */
	public Classpath()
	{
		super();
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException
	{
		// /-Zeichen am Ende der Pfade löschen
		if (this.binPath.charAt(this.binPath.length() - 1) == '/')
		{
			this.binPath = this.binPath.substring(0, this.binPath.length() - 1);
		}

		if (this.srcPath.charAt(this.srcPath.length() - 1) == '/')
		{
			this.srcPath = this.srcPath.substring(0, this.srcPath.length() - 1);
		}

		File file = new File(this.srcPath + "/" + this.fileName);
		String classpath = "";

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			String token = "";
			StringTokenizer stok = null;

			while ((line = br.readLine()) != null)
			{
				line = line.trim();

				// Gültiger Eintrag, wenn vom Typ "lib", aber keine 'src' oder 'resources' Einträge
				if ((line.indexOf("kind=\"lib\"") != -1)
						|| ((line.indexOf("kind=\"src\"") != -1)
								&& (line.indexOf("path=\"src\"") == -1) && (line
								.indexOf("path=\"resources\"") == -1)))
				{
					// Zeile nach Leerzeichen aufspalten
					stok = new StringTokenizer(line, " ");

					while (stok.hasMoreTokens())
					{
						token = stok.nextToken();
					}

					// Auslesen der relevanten Informationen
					token = token.substring(7, token.length() - 3);

					// Zuweisung der Einträge zu den übergebenen Pfaden
					// Jars -> srcPath, sonst binPath
					if (token.startsWith("Jars"))
					{
						classpath += (this.srcPath + "/" + token + ";");
					}
					else
					{
						classpath += (this.binPath + "/" + token + ";");
					}
				}
			}

			// letztes Semikolon löschen
			classpath = classpath.substring(0, classpath.length() - 1);

			// Datei Stream schliessen
			br.close();
		}
		catch (FileNotFoundException e1)
		{
			throw new BuildException(e1);
		}
		catch (IOException e2)
		{
			throw new BuildException(e2);
		}

		// Classpath in die Ant Property schreiben
		getProject().setProperty(this.outputproperty, classpath);
	}

	/**
	 * @see org.apache.tools.ant.Task#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Erzeugt aus Eclipse .classpath Datei einen String zum Compilieren";
	}

	/**
	 * @see org.apache.tools.ant.Task#getTaskName()
	 */
	@Override
	public String getTaskName()
	{
		return "Classpath";
	}

	/**
	 * Setter für den Bin-Path
	 * 
	 * @param string Pfad zu den Compilaten
	 */
	public void setBinPath(final String string)
	{
		this.binPath = string;
	}

	/**
	 * Setter für den Namen der .classpath-Datei
	 * 
	 * @param string Pfad oder Name zur .classpath-Datei
	 */
	public void setFileName(final String string)
	{
		this.fileName = string;
	}

	/**
	 * Name des existierenden Properties in dem der Classpath gespeichert wird.
	 * 
	 * @param string Existierender Property-Name
	 */
	public void setOutputproperty(final String string)
	{
		this.outputproperty = string;
	}

	/**
	 * Setter für den Pfad zu den Jars
	 * 
	 * @param string Jar-Pfad
	 */
	public void setSrcPath(final String string)
	{
		this.srcPath = string;
	}
}
