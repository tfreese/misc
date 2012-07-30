/**
 *
 */
package de.freese.queryengine.condition;

/**
 * Klasse mit Operatoren fuer eine {@link ICondition}.
 * 
 * @author Thomas Freese
 */
public final class Operator
{
	/**
     *
     */
	public static final Operator AND = new Operator("AND");

	/**
     * 
     */
	public static final Operator BETWEEN = new Operator("BETWEEN");

	/**
     *
     */
	public static final Operator EQUALS = new Operator("EQUALS");

	/**
     *
     */
	public static final Operator GREATER = new Operator("GREATER");

	/**
     * 
     */
	public static final Operator LIKE = new Operator("LIKE");

	/**
     * 
     */
	public static final Operator LOWER = new Operator("LOWER");

	/**
     * 
     */
	public static final Operator OR = new Operator("OR");

	/**
     *
     */
	public static final Operator NOT = new Operator("NOT");

	/**
     * 
     */
	public static final Operator IN = new Operator("IN");

	/**
     * 
     */
	public static final Operator EQUALS_GREATER = new Operator(EQUALS.getOperator() + "_"
			+ GREATER.getOperator());

	/**
     * 
     */
	public static final Operator EQUALS_LOWER = new Operator(EQUALS.getOperator() + "_"
			+ LOWER.getOperator());

	/**
     * 
     */
	public static final Operator NOT_BETWEEN = new Operator(NOT.getOperator() + "_"
			+ BETWEEN.getOperator());

	/**
     * 
     */
	public static final Operator NOT_EQUALS = new Operator(NOT.getOperator() + "_"
			+ EQUALS.getOperator());

	/**
     * 
     */
	private String operator = null;

	/**
	 * Creates a new {@link Operator} object.
	 * 
	 * @param operator String
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public Operator(final String operator)
	{
		super();

		if (operator == null)
		{
			throw new IllegalArgumentException("Operator ist null !");
		}

		this.operator = operator;
	}

	/**
	 * Liefert den Operator.
	 * 
	 * @return String
	 */
	public String getOperator()
	{
		return this.operator;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if ((obj == null) || !(obj instanceof Operator))
		{
			return false;
		}

		if (obj == this)
		{
			return true;
		}

		Operator other = (Operator) obj;

		return getOperator().equals(other.getOperator());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getOperator().hashCode();
	}
}
