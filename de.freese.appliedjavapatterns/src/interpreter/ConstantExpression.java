package interpreter;

/**
 * @author Thomas Freese
 */
public class ConstantExpression implements Expression
{
	/**
     * 
     */
	private Object value;

	/**
	 * Creates a new {@link ConstantExpression} object.
	 * 
	 * @param newValue Object
	 */
	public ConstantExpression(final Object newValue)
	{
		this.value = newValue;
	}

	/**
	 * @see interpreter.Expression#interpret(interpreter.Context)
	 */
	@Override
	public void interpret(final Context c)
	{
		c.addVariable(this, this.value);
	}
}
