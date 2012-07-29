package successiveupdate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface Task extends Serializable
{
	/**
	 * @param task {@link Task}
	 */
	public void addSubTask(Task task);

	/**
	 * @return {@link Date}
	 */
	public Date getLastEditDate();

	/**
	 * @return {@link List}
	 */
	public List<Task> getSubTasks();

	/**
	 * @return String
	 */
	public String getTaskDetails();

	/**
	 * @return String
	 */
	public String getTaskID();

	/**
	 * @return String
	 */
	public String getTaskName();

	/**
	 * @param task String
	 */
	public void removeSubTask(Task task);

	/**
	 * @param newDetails String
	 */
	public void setTaskDetails(String newDetails);

	/**
	 * @param newName String
	 */
	public void setTaskName(String newName);
}
