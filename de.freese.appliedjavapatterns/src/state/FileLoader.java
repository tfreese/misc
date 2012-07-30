package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public class FileLoader
{
	/**
	 * @param inputFile {@link File}
	 * @return Object
	 */
	public static Object loadData(final File inputFile)
	{
		Object returnValue = null;

		try
		{
			if (inputFile.exists())
			{
				if (inputFile.isFile())
				{
					ObjectInputStream readIn =
							new ObjectInputStream(new FileInputStream(inputFile));

					returnValue = readIn.readObject();
					readIn.close();
				}
				else
				{
					System.err.println(inputFile + " is a directory.");
				}
			}
			else
			{
				System.err.println("File " + inputFile + " does not exist.");
			}
		}
		catch (ClassNotFoundException exc)
		{
			exc.printStackTrace();
		}
		catch (IOException exc)
		{
			exc.printStackTrace();
		}

		return returnValue;
	}

	/**
	 * @param outputFile {@link File}
	 * @param data {@link Serializable}
	 */
	public static void storeData(final File outputFile, final Serializable data)
	{
		try
		{
			ObjectOutputStream writeOut = new ObjectOutputStream(new FileOutputStream(outputFile));

			writeOut.writeObject(data);
			writeOut.close();
		}
		catch (IOException exc)
		{
			exc.printStackTrace();
		}
	}
}
