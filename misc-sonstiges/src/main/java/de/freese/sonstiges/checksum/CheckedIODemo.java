package de.freese.sonstiges.checksum;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;

/**
 * @author Thomas Freese
 */
public class CheckedIODemo
{
	/**
	 * @param args String[]
	 * @throws IOException Falls was schief geht.
	 */
	public static void main(final String[] args) throws IOException
	{
		Adler32 inChecker = new Adler32();
		Adler32 outChecker = new Adler32();

		try (CheckedInputStream in =
				new CheckedInputStream(new FileInputStream("words.txt"), inChecker))
		{
			try (CheckedOutputStream out =
					new CheckedOutputStream(new FileOutputStream("outagain.txt"), outChecker))
			{
				int c = 0;

				while ((c = in.read()) != -1)
				{
					out.write(c);
				}

				System.out.println("Input stream check sum: " + inChecker.getValue());
				System.out.println("Output stream check sum: " + outChecker.getValue());
			}
		}
		catch (FileNotFoundException e)
		{
			System.err.println("CheckedIODemo: " + e);
			System.exit(-1);

			return;
		}
	}
}
