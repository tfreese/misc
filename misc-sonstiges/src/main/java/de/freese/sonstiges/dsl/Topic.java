/**
 * Created: 14.06.2012
 */

package de.freese.sonstiges.dsl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Topic
{
	/**
	 * 
	 */
	private final String name;

	/**
	 * 
	 */
	private List<NewsSnippet> newsSnippets = new ArrayList<>();

	/**
	 * Erstellt ein neues {@link Topic} Object.
	 * 
	 * @param name String
	 */
	public Topic(final String name)
	{
		super();

		this.name = name;
	}

	/**
	 * @param title String
	 * @return {@link ChangeNewsSnippetBuilder}
	 */
	public ChangeNewsSnippetBuilder changeNewsSnippet(final String title)
	{
		NewsSnippet snippet = null;

		for (NewsSnippet ns : this.newsSnippets)
		{
			if (title.equals(ns.getTitle()))
			{
				snippet = ns;
				break;
			}
		}

		if (snippet == null)
		{
			String message = String.format("NewsSnippet \"%s\" not found", title);
			throw new RuntimeException(message);
		}

		ChangeNewsSnippetBuilder builder = new ChangeNewsSnippetBuilder(this, snippet);

		return builder;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return {@link List}<NewsSnippet>
	 */
	public List<NewsSnippet> getNewsSnippets()
	{
		return this.newsSnippets;
	}

	/**
	 * @param title String
	 * @return {@link NewNewsSnippetBuilder}
	 */
	public NewNewsSnippetBuilder newNewsSnippet(final String title)
	{
		NewNewsSnippetBuilder builder = new NewNewsSnippetBuilder(this, title);

		return builder;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [name=");
		builder.append(this.name);
		builder.append(", newsSnippets=");
		builder.append(this.newsSnippets);
		builder.append("]");

		return builder.toString();
	}
}
