package successiveupdate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class TaskImpl implements Task
{
	/**
	 *
	 */
	private static final long serialVersionUID = 551481321024109589L;

	/**
     * 
     */
	private Date lastEditDate;

	/**
     * 
     */
	private List<Task> subTasks = new ArrayList<>();

	/**
     * 
     */
	private String taskDetails;

	/**
     * 
     */
	private String taskID;

	/**
     * 
     */
	private String taskName;

	/**
	 * Creates a new {@link TaskImpl} object.
	 */
	public TaskImpl()
	{
		super();

		this.lastEditDate = new Date();
		this.taskName = "";
		this.taskDetails = "";
	}

	/**
	 * Creates a new {@link TaskImpl} object.
	 * 
	 * @param newTaskName String
	 * @param newTaskDetails String
	 * @param newEditDate {@link Date}
	 * @param newSubTasks {@link List}
	 */
	public TaskImpl(final String newTaskName, final String newTaskDetails, final Date newEditDate,
			final List<Task> newSubTasks)
	{
		super();

		this.lastEditDate = newEditDate;
		this.taskName = newTaskName;
		this.taskDetails = newTaskDetails;

		if (newSubTasks != null)
		{
			this.subTasks = newSubTasks;
		}
	}

	/**
	 * @see successiveupdate.Task#addSubTask(successiveupdate.Task)
	 */
	@Override
	public void addSubTask(final Task task)
	{
		if (!this.subTasks.contains(task))
		{
			this.subTasks.add(task);
		}
	}

	/**
	 * @see successiveupdate.Task#getLastEditDate()
	 */
	@Override
	public Date getLastEditDate()
	{
		return this.lastEditDate;
	}

	/**
	 * @see successiveupdate.Task#getSubTasks()
	 */
	@Override
	public List<Task> getSubTasks()
	{
		return this.subTasks;
	}

	/**
	 * @see successiveupdate.Task#getTaskDetails()
	 */
	@Override
	public String getTaskDetails()
	{
		return this.taskDetails;
	}

	/**
	 * @see successiveupdate.Task#getTaskID()
	 */
	@Override
	public String getTaskID()
	{
		return this.taskID;
	}

	/**
	 * @see successiveupdate.Task#getTaskName()
	 */
	@Override
	public String getTaskName()
	{
		return this.taskName;
	}

	/**
	 * @see successiveupdate.Task#removeSubTask(successiveupdate.Task)
	 */
	@Override
	public void removeSubTask(final Task task)
	{
		this.subTasks.remove(task);
	}

	/**
	 * @param newDate {@link Date}
	 */
	public void setLastEditDate(final Date newDate)
	{
		if (newDate.after(this.lastEditDate))
		{
			this.lastEditDate = newDate;
		}
	}

	/**
	 * @see successiveupdate.Task#setTaskDetails(java.lang.String)
	 */
	@Override
	public void setTaskDetails(final String newDetails)
	{
		this.taskDetails = newDetails;
	}

	/**
	 * @see successiveupdate.Task#setTaskName(java.lang.String)
	 */
	@Override
	public void setTaskName(final String newName)
	{
		this.taskName = newName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.taskName + " " + this.taskDetails;
	}
}
