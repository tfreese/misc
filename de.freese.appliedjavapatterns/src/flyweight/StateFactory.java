package flyweight;

/**
 * @author Thomas Freese
 */
public class StateFactory
{
	/**
     * 
     */
	public static final State CLEAN = new CleanState();

	/**
     * 
     */
	public static final State DIRTY = new DirtyState();

	/**
     * 
     */
	private static State currentState = CLEAN;

	/**
	 * @return {@link State}
	 */
	public static State getCurrentState()
	{
		return currentState;
	}

	/**
	 * @param state {@link State}
	 */
	public static void setCurrentState(final State state)
	{
		currentState = state;
	}
}
