package successiveupdate;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public class TaskResponse implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5465144035047477798L;

	/**
     * 
     */
	private Date lastUpdate;

	/**
     * 
     */
	private Task task;

	/**
	 * Creates a new {@link TaskResponse} object.
	 * 
	 * @param newUpdate {@link Date}
	 * @param newTask {@link Task}
	 */
	public TaskResponse(final Date newUpdate, final Task newTask)
	{
		super();

		this.lastUpdate = newUpdate;
		this.task = newTask;
	}

	/**
	 * @return {@link Date}
	 */
	public Date getLastUpdate()
	{
		return this.lastUpdate;
	}

	/**
	 * @return {@link Task}
	 */
	public Task getTask()
	{
		return this.task;
	}

	/**
	 * @param newDate {@link Date}
	 */
	public void setLastUpdate(final Date newDate)
	{
		if (newDate.after(this.lastUpdate))
		{
			this.lastUpdate = newDate;
		}
	}
}
