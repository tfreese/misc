// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

/**
 * @author Thomas Freese
 */
public class Test
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// Map<String, Charset> charsets = Charset.availableCharsets();
		//
		// for (String key : charsets.keySet())
		// {
		// System.out.println(key);
		// }

		int width = 200;
		int pos = 0;
		System.out.println(pos + " - " + getTorusKoord(width, pos, -1));

		for (int x = 0; x < 201; x++)
		{
			// System.out.println(x + " - " + getTorusKoord(width, x, -1));
		}

		for (int x = 201; x > 0; x--)
		{
			// System.out.println(x + " - " + (201 * (x - 1)) % 200);
		}
	}

	/**
	 * @param width int
	 * @param x int
	 * @param offSet int
	 * @return int
	 */
	private static int getTorusKoord(final int width, final int x, final int offSet)
	{
		if ((x == 0) && (offSet < 0))
		{
			return width + offSet;
		}

		return ((width + 1) * (x + offSet)) % width;
	}
}
