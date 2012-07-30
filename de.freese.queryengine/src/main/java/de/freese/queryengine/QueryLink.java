/**
 * 10.04.2008
 */
package de.freese.queryengine;

/**
 * Verknuepft 2 Query's miteinander.
 * 
 * @author Thomas Freese
 */
public final class QueryLink
{
	/**
	 *
	 */
	private final Object childField;

	/**
	 * 
	 */
	private final Object parentField;

	/**
	 * 
	 */
	private final Query childQuery;

	/**
	 *
	 */
	private final Query parentQuery;

	/**
	 * Creates a new QueryLink object.
	 * 
	 * @param parentQuery {@link Query}
	 * @param parentField Object
	 * @param childQuery {@link Query}
	 * @param childField Object
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public QueryLink(final Query parentQuery, final Object parentField, final Query childQuery,
			final Object childField)
	{
		super();

		if (parentQuery == null)
		{
			throw new IllegalArgumentException("ParentQuery ist null !");
		}

		if (parentField == null)
		{
			throw new IllegalArgumentException("ParentField ist null !");
		}

		if (childQuery == null)
		{
			throw new IllegalArgumentException("ChildQuery ist null !");
		}

		if (childField == null)
		{
			throw new IllegalArgumentException("ChildField ist null !");
		}

		this.parentQuery = parentQuery;
		this.parentField = parentField;
		this.childQuery = childQuery;
		this.childField = childField;
	}

	/**
	 * ChildField.
	 * 
	 * @return Object
	 */
	public Object getChildField()
	{
		return this.childField;
	}

	/**
	 * ChildQuery.
	 * 
	 * @return {@link Query}
	 */
	public Query getChildQuery()
	{
		return this.childQuery;
	}

	/**
	 * ParentField.
	 * 
	 * @return Object
	 */
	public Object getParentField()
	{
		return this.parentField;
	}

	/**
	 * ParentQuery.
	 * 
	 * @return {@link Query}
	 */
	public Query getParentQuery()
	{
		return this.parentQuery;
	}
}
