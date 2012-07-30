/**
 * Created: 09.08.2011
 */

package de.freese.sonstiges.rsync;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Thomas Freese
 */
public class TestRsync
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		StringBuilder command = new StringBuilder("rsync");
		command.append(" --verbose");
		command.append(" --stats");
		command.append(" --human-readable");
		command.append(" --progress");
		command.append(" --checksum");
		command.append(" --recursive");
		command.append(" --delete-after");
		command.append(" --delete-excluded");
		command.append(" --perms");
		command.append(" --owner");
		command.append(" --times");
		command.append(" --group");
		command.append(" --links");
		command.append(" --force");
		command.append(" --exclude-from=rsyncExcludes.conf");
		// command.append(" --exclude='");
		// command.append(" --exclude=.DS_Store");
		// command.append(" --exclude=._*");
		// command.append(" --exclude=.localized");
		// command.append(" --exclude='*/EVE\\ Online*/**'");
		// command.append(" --exclude=*/.metadata/**");
		// command.append(" --exclude=*/bin/**");
		// command.append(" --exclude=/.svn");
		// command.append(" --exclude=*.class");
		// command.append(" --exclude=*.svn-base");
		// command.append("'");
		// command.append(" /Users/tommy/arbeit");
		// command.append(" /Users/tommy/test");

		// Remote
		// command.append(" --compress -e ssh");
		// command.append(" /Users/tommy/test");
		// command.append(" tommy@192.168.155.5:/backup/test ");

		System.out.println(command.toString());

		try
		{
			Process process = Runtime.getRuntime().exec(command.toString());

			BufferedReader errorReader =
					new BufferedReader(new InputStreamReader(process.getErrorStream()));
			BufferedReader inputReader =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			// BufferedWriter outputWriter =
			// new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

			// outputWriter.write("tolotos");

			String line = null;

			while ((line = inputReader.readLine()) != null)
			{
				System.out.println(line);
			}

			while ((line = errorReader.readLine()) != null)
			{
				System.err.println(line);
			}

			int exitVal = process.waitFor();
			System.out.println("Exit value: " + exitVal);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
