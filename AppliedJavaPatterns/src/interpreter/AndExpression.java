package interpreter;

/**
 * @author Thomas Freese
 */
public class AndExpression extends CompoundExpression
{
	/**
	 * Creates a new {@link AndExpression} object.
	 * 
	 * @param expressionA {@link ComparisonExpression}
	 * @param expressionB {@link ComparisonExpression}
	 */
	public AndExpression(final ComparisonExpression expressionA,
			final ComparisonExpression expressionB)
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
		Boolean result =
				new Boolean(((Boolean) c.get(this.expressionA)).booleanValue()
						&& ((Boolean) c.get(this.expressionB)).booleanValue());

		c.addVariable(this, result);
	}
}
