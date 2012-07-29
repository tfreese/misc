package interpreter;

import java.lang.reflect.Method;

/**
 * @author Thomas Freese
 */
public class VariableExpression implements Expression
{
	/**
     * 
     */
	private final Object lookup;

	/**
     * 
     */
	private final String methodName;

	/**
	 * Creates a new {@link VariableExpression} object.
	 * 
	 * @param newLookup Object
	 * @param newMethodName String
	 */
	public VariableExpression(final Object newLookup, final String newMethodName)
	{
		this.lookup = newLookup;
		this.methodName = newMethodName;
	}

	/**
	 * @see interpreter.Expression#interpret(interpreter.Context)
	 */
	@Override
	public void interpret(final Context c)
	{
		try
		{
			Object source = c.get(this.lookup);

			if (source != null)
			{
				Method method = source.getClass().getMethod(this.methodName);
				Object result = method.invoke(source);

				c.addVariable(this, result);
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		// catch (NoSuchMethodException exc)
		// {
		// // Ignore
		// }
		// catch (IllegalAccessException exc)
		// {
		// // Ignore
		// }
		// catch (InvocationTargetException exc)
		// {
		// // Ignore
		// }
	}
}
