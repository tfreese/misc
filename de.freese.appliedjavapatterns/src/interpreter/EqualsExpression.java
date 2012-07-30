package interpreter;

/**
 * @author Thomas Freese
 */
public class EqualsExpression extends ComparisonExpression
{
	/**
	 * Creates a new {@link EqualsExpression} object.
	 * 
	 * @param expressionA {@link Expression}
	 * @param expressionB {@link Expression}
	 */
	public EqualsExpression(final Expression expressionA, final Expression expressionB)
	{
		super(expressionA, expressionB);
	}

	/**
	 * @see interpreter.Expression#interpret(interpreter.Context)
	 */
	@Override
	public void interpret(final Context c)
	{
		this.expressionA.interpret(c);
		this.expressionB.interpret(c);
		Boolean result = Boolean.valueOf(c.get(this.expressionA).equals(c.get(this.expressionB)));

		c.addVariable(this, result);
	}
}
