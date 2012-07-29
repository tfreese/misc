package observer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * @author Thomas Freese
 */
public class TaskSelectorPanel extends JPanel implements ActionListener, TaskChangeObserver
{
	/**
	 *
	 */
	private static final long serialVersionUID = -567676316307246423L;

	/**
     * 
     */
	private TaskChangeObservable notifier;

	/**
     * 
     */
	private JComboBox<Task> selector = new JComboBox<>();

	/**
	 * Creates a new {@link TaskSelectorPanel} object.
	 * 
	 * @param newNotifier {@link TaskChangeObservable}
	 */
	public TaskSelectorPanel(final TaskChangeObservable newNotifier)
	{
		super();

		this.notifier = newNotifier;
		createGui();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		this.notifier.selectTask((Task) this.selector.getSelectedItem());
	}

	/**
     * 
     */
	public void createGui()
	{
		this.selector = new JComboBox<>();
		this.selector.addActionListener(this);
		add(this.selector);
	}

	/**
	 * @param newNotifier {@link TaskChangeObservable}
	 */
	public void setTaskChangeObservable(final TaskChangeObservable newNotifier)
	{
		this.notifier = newNotifier;
	}

	/**
	 * @see observer.TaskChangeObserver#taskAdded(observer.Task)
	 */
	@Override
	public void taskAdded(final Task task)
	{
		this.selector.addItem(task);
	}

	/**
	 * @see observer.TaskChangeObserver#taskChanged(observer.Task)
	 */
	@Override
	public void taskChanged(final Task task)
	{
		// Empty
	}

	/**
	 * @see observer.TaskChangeObserver#taskSelected(observer.Task)
	 */
	@Override
	public void taskSelected(final Task task)
	{
		// Empty
	}
}
