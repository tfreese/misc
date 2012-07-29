package observer;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Thomas Freese
 */
public class TaskHistoryPanel extends JPanel implements TaskChangeObserver
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2707666152527187214L;

	/**
     * 
     */
	private JTextArea displayRegion;

	/**
	 * Creates a new {@link TaskHistoryPanel} object.
	 */
	public TaskHistoryPanel()
	{
		super();

		createGui();
	}

	/**
     * 
     */
	public void createGui()
	{
		setLayout(new BorderLayout());
		this.displayRegion = new JTextArea(10, 40);
		this.displayRegion.setEditable(false);
		add(new JScrollPane(this.displayRegion));
	}

	/**
	 * @see observer.TaskChangeObserver#taskAdded(observer.Task)
	 */
	@Override
	public void taskAdded(final Task task)
	{
		this.displayRegion.append("Created task " + task + "\n");
	}

	/**
	 * @see observer.TaskChangeObserver#taskChanged(observer.Task)
	 */
	@Override
	public void taskChanged(final Task task)
	{
		this.displayRegion.append("Updated task " + task + "\n");
	}

	/**
	 * @see observer.TaskChangeObserver#taskSelected(observer.Task)
	 */
	@Override
	public void taskSelected(final Task task)
	{
		this.displayRegion.append("Selected task " + task + "\n");
	}
}
