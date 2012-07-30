package interpreter;

/**
 * @author Thomas Freese
 */
public abstract class CompoundExpression implements Expression
{
	/**
     * 
     */
	protected final ComparisonExpression expressionA;

	/**
     * 
     */
	protected final ComparisonExpression expressionB;

	/**
	 * Creates a new {@link CompoundExpression} object.
	 * 
	 * @param expressionA {@link ComparisonExpression}
	 * @param expressionB {@link ComparisonExpression}
	 */
	public CompoundExpression(final ComparisonExpression expressionA,
			final ComparisonExpression expressionB)
	{
		this.expressionA = expressionA;
		this.expressionB = expressionB;
	}
}
