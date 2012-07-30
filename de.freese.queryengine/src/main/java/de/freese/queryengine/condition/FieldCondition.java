/**
 * 05.04.2008
 */
package de.freese.queryengine.condition;

/**
 * Condition fuer ein bestimmtes Feld.
 * 
 * @author Thomas Freese
 */
public class FieldCondition implements ICondition
{
	/**
	 * 
	 */
	private final Object value1;

	/**
	 * 
	 */
	private final Object value2;

	/**
	 * 
	 */
	private final Operator operator;

	/**
	 * 
	 */
	private final String fieldName;

	/**
	 * Creates a new FieldCondition object.
	 * 
	 * @param operator Operator
	 * @param fieldName String
	 * @param value Object
	 */
	public FieldCondition(final Operator operator, final String fieldName, final Object value)
	{
		this(operator, fieldName, value, null);
	}

	/**
	 * Creates a new FieldCondition object.
	 * 
	 * @param operator Operator
	 * @param fieldName String
	 * @param value1 Object
	 * @param value2 Object
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public FieldCondition(final Operator operator, final String fieldName, final Object value1,
			final Object value2)
	{
		super();

		if (operator == null)
		{
			throw new IllegalArgumentException("Operator ist null !");
		}

		if (fieldName == null)
		{
			throw new IllegalArgumentException("Feldname ist null !");
		}

		if (value1 == null)
		{
			throw new IllegalArgumentException("Wert1 ist null !");
		}

		this.operator = operator;
		this.fieldName = fieldName;
		this.value1 = value1;
		this.value2 = value2;
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
	 * Liefert das Value1 der Condition.
	 * 
	 * @return Object
	 */
	public Object getValue1()
	{
		return this.value1;
	}

	/**
	 * Liefert das Value2 der Condition.
	 * 
	 * @return Object
	 */
	public Object getValue2()
	{
		return this.value2;
	}
}
