package interpreter;

/**
 * @author Thomas Freese
 */
public class ContainsExpression extends ComparisonExpression
{
	/**
	 * Creates a new {@link ContainsExpression} object.
	 * 
	 * @param expressionA {@link Expression}
	 * @param expressionB {@link Expression}
	 */
	public ContainsExpression(final Expression expressionA, final Expression expressionB)
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
		Object exprAResult = c.get(this.expressionA);
		Object exprBResult = c.get(this.expressionB);

		if ((exprAResult instanceof String) && (exprBResult instanceof String))
		{
			if (((String) exprAResult).indexOf((String) exprBResult) != -1)
			{
				c.addVariable(this, Boolean.TRUE);

				return;
			}
		}

		c.addVariable(this, Boolean.FALSE);

		return;
	}
}
