package interpreter;

/**
 * @author Thomas Freese
 */
public abstract class ComparisonExpression implements Expression
{
	/**
     * 
     */
	protected final Expression expressionA;

	/**
     * 
     */
	protected final Expression expressionB;

	/**
	 * Creates a new {@link ComparisonExpression} object.
	 * 
	 * @param expressionA {@link Expression}
	 * @param expressionB {@link Expression}
	 */
	public ComparisonExpression(final Expression expressionA, final Expression expressionB)
	{
		this.expressionA = expressionA;
		this.expressionB = expressionB;
	}
}
