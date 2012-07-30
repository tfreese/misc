/**
 * 05.04.2008
 */
package de.freese.queryengine.condition;

import de.freese.queryengine.Query;

/**
 * Queryobject fuer eine Subquery der QueryEngine.
 * 
 * @author Thomas Freese
 */
public final class SubQueryCondition implements ICondition
{
	/**
     * 
     */
	private final Operator operator;

	/**
     *
     */
	private final Query query;

	/**
     * 
     */
	private final String fieldName;

	/**
	 * Creates a new SubQuery object.
	 * 
	 * @param operator Operator
	 * @param fieldName String
	 * @param query Query
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public SubQueryCondition(final Operator operator, final String fieldName, final Query query)
	{
		super();

		if (operator == null)
		{
			throw new IllegalArgumentException("Operator ist null !");
		}

		if (fieldName == null)
		{
			throw new IllegalArgumentException("FiledName ist null !");
		}

		if (query == null)
		{
			throw new IllegalArgumentException("Query ist null !");
		}

		this.operator = operator;
		this.fieldName = fieldName;
		this.query = query;
	}

	/**
	 * @see de.freese.queryengine.condition.ICondition#getFieldName()
	 */
	@Override
	public String getFieldName()
	{
		return this.fieldName;
	}

	/**
	 * @see de.freese.queryengine.condition.ICondition#getOperator()
	 */
	@Override
	public Operator getOperator()
	{
		return this.operator;
	}

	/**
	 * Liefert die Query.
	 * 
	 * @return Query
	 */
	public Query getQuery()
	{
		return this.query;
	}
}
