package successiveupdate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class UpdateServerDelegate
{
	/**
     * 
     */
	private static Map<String, Task> tasks = new HashMap<>();

	/**
	 * @param taskID String
	 * @param lastUpdate {@link Date}
	 * @return {@link Task}
	 * @throws UpdateException Falls was schief geht.
	 */
	public static Task getTask(final String taskID, final Date lastUpdate) throws UpdateException
	{
		if (tasks.containsKey(taskID))
		{
			Task storedTask = tasks.get(taskID);

			if (storedTask.getLastEditDate().after(lastUpdate))
			{
				return storedTask;
			}

			throw new UpdateException("Task " + taskID + " does not need to be updated",
					UpdateException.TASK_UNCHANGED);
		}

		return loadNewTask(taskID);
	}

	/**
	 * @param taskID String
	 * @return {@link Task}
	 */
	private static Task loadNewTask(final String taskID)
	{
		Task newTask = new TaskImpl(taskID, "", new Date(), null);

		tasks.put(taskID, newTask);

		return newTask;
	}

	/**
	 * @param taskID String
	 * @param task {@link Task}
	 * @throws UpdateException Falls was schief geht
	 */
	public static void updateTask(final String taskID, final Task task) throws UpdateException
	{
		if (tasks.containsKey(taskID))
		{
			if (task.getLastEditDate().equals(tasks.get(taskID).getLastEditDate()))
			{
				((TaskImpl) task).setLastEditDate(new Date());
				tasks.put(taskID, task);
			}
			else
			{
				throw new UpdateException("Task " + taskID
						+ " data must be refreshed before editing",
						UpdateException.TASK_OUT_OF_DATE);
			}
		}
	}
}
