/**
 * Created: 20.10.2012
 */

package de.freese.sonstiges.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zeigt nur Interessante Maven Infos an.
 * 
 * @author Thomas Freese
 */
public class MavenUpdates
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MavenUpdates.class);

	/**
	 * Liefert die möglichen Optionen der Kommandozeile.<br>
	 * Dies sind die JRE Programm Argumente.
	 * 
	 * @return {@link Options}
	 */
	private static Options getCommandOptions()
	{
		Options options = new Options();

		Option option = new Option("d", "directory", true, "Verzeichnis der POM");
		option.setRequired(true);
		options.addOption(option);

		return options;
	}

	/**
	 * @param args final String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		// StartParameter auslesen.
		Options options = getCommandOptions();
		CommandLine line = null;

		try
		{
			CommandLineParser parser = new GnuParser();
			line = parser.parse(options, args);
		}
		catch (Exception ex)
		{
			LOGGER.error(ex.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Main", options, true);

			System.exit(-1);
			return;
		}

		String directory = line.getOptionValue("directory");

		MavenUpdates updates = new MavenUpdates();
		updates.showDependencyUpdates(directory);
	}

	/**
	 * Erstellt ein neues {@link MavenUpdates} Object.
	 */
	public MavenUpdates()
	{
		super();
	}

	/**
	 * Auflistung aller Dependency Updates.
	 * 
	 * @param directory String
	 * @throws IOException Falls was schief geht.
	 * @throws InterruptedException Falls was schief geht.
	 */
	private void showDependencyUpdates(final String directory)
		throws IOException, InterruptedException
	{
		File dir = new File(directory);

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(dir);

		// Map<String, String> env = processBuilder.environment();
		// env.put("my-param", "true");

		String cmd = SystemUtils.IS_OS_WINDOWS ? "mvn.bat" : "mvn";

		processBuilder.command(cmd, "versions:display-dependency-updates",
				"-DprocessDependencyManagement=false");

		Process process = processBuilder.start();
		InputStream stdIn = process.getInputStream();

		System.out.println("The following dependencies in Dependencies have newer versions:");

		List<String> updateList = new ArrayList<>();
		boolean print = false;

		try (BufferedReader readerIn = new BufferedReader(new InputStreamReader(stdIn)))
		{
			String line = null;

			while ((line = readerIn.readLine()) != null)
			{
				if (StringUtils.isEmpty(line))
				{
					continue;
				}

				line = StringUtils.replace(line, "[INFO]", "");
				line = StringUtils.trim(line);

				if (print && StringUtils.isNotEmpty(line))
				{
					// System.out.println(line);
					updateList.add(line.trim());
				}

				if (line.contains("The following dependencies in Dependencies have newer versions"))
				{
					print = true;
				}
				else if (StringUtils.isEmpty(line) || line.endsWith("---"))
				{
					print = false;
				}
			}
		}

		for (String update : updateList)
		{
			System.out.println(update);
		}

		System.out.println("---------------");

		// Zeilenumbrüche normalisieren.
		for (int i = 0; i < updateList.size(); i++)
		{
			String update = updateList.get(i);

			if (StringUtils.isNotBlank(update) && !StringUtils.contains(update, "->"))
			{
				// Zeilenumbruch wegen zu langen Maven Koordinaten.
				update += "... " + updateList.get(i + 1);

				updateList.set(i, update);
				updateList.set(i + 1, "");
			}
		}

		// Format vereinheitlichen.
		Set<String> updateSet = new TreeSet<>();

		for (String update : updateList)
		{
			if (StringUtils.isBlank(update))
			{
				continue;
			}

			String splits[] = StringUtils.splitByWholeSeparator(update, " ..");

			String coords = splits[0];
			splits = StringUtils.splitByWholeSeparator(splits[1], ".. ");

			update = coords + "\t" + splits[1];
			updateSet.add(StringUtils.trim(update));
		}

		for (String update : updateSet)
		{
			System.out.println(update);
		}

		int exitVal = process.waitFor();
		System.out.println("Exit value: " + exitVal);
	}
}
