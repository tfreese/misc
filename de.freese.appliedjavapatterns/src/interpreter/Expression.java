package interpreter;

/**
 * @author Thomas Freese
 */
public interface Expression
{
	/**
	 * @param c {@link Context}
	 */
	void interpret(Context c);
}
