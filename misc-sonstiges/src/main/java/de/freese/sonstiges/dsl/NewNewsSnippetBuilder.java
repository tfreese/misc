/**
 * Created: 14.06.2012
 */

package de.freese.sonstiges.dsl;

import java.util.Date;

import de.freese.sonstiges.dsl.tools.DateRangeBuilder;
import de.freese.sonstiges.dsl.tools.DateRangeBuilderCallback;

/**
 * @author Thomas Freese
 */
public class NewNewsSnippetBuilder extends AbstractNewsSnippetBuilder<NewNewsSnippetBuilder>
{
	/**
	 * Erstellt ein neues {@link NewNewsSnippetBuilder} Object.
	 * 
	 * @param topic {@link Topic}
	 * @param title String
	 */
	public NewNewsSnippetBuilder(final Topic topic, final String title)
	{
		super(topic, title);
	}

	/**
	 * @see de.freese.sonstiges.dsl.AbstractNewsSnippetBuilder#add()
	 */
	@Override
	public void add()
	{
		getTopic().getNewsSnippets().add(getObjectUnderConstruction());
	}

	/**
	 * @param from {@link Date}
	 * @return {@link AbstractNewsSnippetBuilder}
	 */
	public DateRangeBuilder<NewNewsSnippetBuilder> validFrom(final Date from)
	{
		DateRangeBuilder<NewNewsSnippetBuilder> dateRangeBuilder =
				new DateRangeBuilder<>(from, this, new DateRangeBuilderCallback()
				{
					/**
					 * @see de.freese.sonstiges.dsl.tools.DateRangeBuilderCallback#setDateRange(java.util.Date,
					 *      java.util.Date)
					 */
					@Override
					public void setDateRange(final Date from, final Date to)
					{
						getObjectUnderConstruction().setValidFrom(from);
						getObjectUnderConstruction().setValidTo(to);
					}
				});

		return dateRangeBuilder;
	}
}
