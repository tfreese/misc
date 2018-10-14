/**
 * Created: 14.06.2012
 */

package de.freese.sonstiges.dsl;

import de.freese.sonstiges.dsl.tools.DateUtils;

/**
 * @author Thomas Freese
 */
public class TestDSL
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// Erstellen
		// @formatter:off
		Topic topic = new Topic("Topic 1");
		topic.newNewsSnippet("News 1")
			.containing("Content 1")
			.describe("Description 1")
			.taggedBy("T1","N1","DSL")
			.validFrom(DateUtils.today())
			.to(DateUtils.infinity())
			.add();
		// @formatter:on
		System.out.println(topic);

		// Ändern
		// @formatter:off
		topic.changeNewsSnippet("News 1")
			.containing("Content 11")
			.describe("Description 11")
			.taggedBy("ASL")
			.validTo(DateUtils.tomorrow())
			.add();
		// @formatter:on
		System.out.println(topic);
	}
}
