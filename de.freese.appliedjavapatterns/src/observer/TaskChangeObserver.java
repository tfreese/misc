package observer;

/**
 * @author Thomas Freese
 */
public interface TaskChangeObserver
{
	/**
	 * @param task {@link Task}
	 */
	public void taskAdded(Task task);

	/**
	 * @param task {@link Task}
	 */
	public void taskChanged(Task task);

	/**
	 * @param task {@link Task}
	 */
	public void taskSelected(Task task);
}
