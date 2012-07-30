/**
 * Created: 29.08.2011
 */

package de.freese.sonstiges.snowball;

import org.tartarus.snowball.SnowballProgram;

/**
 * @author Thomas Freese
 */
public class TestSnowball
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		String language = "German";
		// language = "German2";
		Class<?> stemClass = Class.forName("org.tartarus.snowball.ext." + language + "Stemmer");
		SnowballProgram stemmer = (SnowballProgram) stemClass.newInstance();

		stemmer.setCurrent("wälder");
		stemmer.stem();
		System.out.println(stemmer.getCurrent());
	}
}
