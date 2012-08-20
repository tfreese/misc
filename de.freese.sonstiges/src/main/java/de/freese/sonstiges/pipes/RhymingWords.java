package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * @author Thomas Freese
 */
public class RhymingWords
{
	/**
	 * @param args String[]
	 * @throws IOException Falls was schief geht.
	 */
	public static void main(final String[] args) throws IOException
	{
		File file = new File("words.txt");
		System.out.println(file.getAbsolutePath());
		FileReader words = new FileReader(file);

		// do the reversing and sorting
		Reader rhymedWords = reverse(sort(reverse(words)));

		// write new list to standard out
		BufferedReader in = new BufferedReader(rhymedWords);
		String input;

		while ((input = in.readLine()) != null)
		{
			System.out.println(input);
		}

		in.close();
	}

	/**
	 * @param source {@link Reader}
	 * @return {@link Reader}
	 * @throws IOException Falls was schief geht.
	 */
	public static Reader reverse(final Reader source) throws IOException
	{
		BufferedReader in = new BufferedReader(source);

		PipedWriter pipeOut = new PipedWriter();
		PipedReader pipeIn = new PipedReader(pipeOut);
		PrintWriter out = new PrintWriter(pipeOut);

		new ReverseThread(out, in).start();

		return pipeIn;
	}

	/**
	 * @param source {@link Reader}
	 * @return {@link Reader}
	 * @throws IOException Falls was schief geht.
	 */
	public static Reader sort(final Reader source) throws IOException
	{
		BufferedReader in = new BufferedReader(source);

		PipedWriter pipeOut = new PipedWriter();
		PipedReader pipeIn = new PipedReader(pipeOut);
		PrintWriter out = new PrintWriter(pipeOut);

		new SortThread(out, in).start();

		return pipeIn;
	}
}