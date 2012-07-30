package workerthread;

/**
 * @author Thomas Freese
 */
public interface Queue
{
	/**
	 * @param r {@link RunnableTask}
	 */
	void put(RunnableTask r);

	/**
	 * @return {@link RunnableTask}
	 */
	RunnableTask take();
}
