package observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class TaskChangeObservable
{
	/**
     * 
     */
	private List<TaskChangeObserver> observers = new ArrayList<>();

	/**
	 * Erstellt ein neues {@link TaskChangeObservable} Object.
	 */
	TaskChangeObservable()
	{
		super();
	}

	/**
	 * @param task {@link Task}
	 */
	public void addTask(final Task task)
	{
		Iterator<TaskChangeObserver> elements = this.observers.iterator();

		while (elements.hasNext())
		{
			elements.next().taskAdded(task);
		}
	}

	/**
	 * @param observer {@link TaskChangeObserver}
	 */
	public void addTaskChangeObserver(final TaskChangeObserver observer)
	{
		if (!this.observers.contains(observer))
		{
			this.observers.add(observer);
		}
	}

	/**
	 * @param observer {@link TaskChangeObserver}
	 */
	public void removeTaskChangeObserver(final TaskChangeObserver observer)
	{
		this.observers.remove(observer);
	}

	/**
	 * @param task {@link Task}
	 */
	public void selectTask(final Task task)
	{
		Iterator<TaskChangeObserver> elements = this.observers.iterator();

		while (elements.hasNext())
		{
			elements.next().taskSelected(task);
		}
	}

	/**
	 * @param task {@link Task}
	 */
	public void updateTask(final Task task)
	{
		Iterator<TaskChangeObserver> elements = this.observers.iterator();

		while (elements.hasNext())
		{
			elements.next().taskChanged(task);
		}
	}
}
