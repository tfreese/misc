package observer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class TaskEditorPanel extends JPanel implements ActionListener, TaskChangeObserver
{
	/**
	 *
	 */
	private static final long serialVersionUID = 8316855880318085405L;

	/**
     * 
     */
	private JButton add = null;

	/**
     * 
     */
	private JButton update = null;

	/**
     * 
     */
	private JButton exit = null;

	/**
     * 
     */
	private JPanel controlPanel = null;

	/**
     * 
     */
	private JPanel editPanel = null;

	/**
     * 
     */
	private Task editTask = null;

	/**
     * 
     */
	private TaskChangeObservable notifier = null;

	/**
     * 
     */
	private JTextField taskName = null;

	/**
     * 
     */
	private JTextField taskNotes = null;

	/**
     * 
     */
	private JTextField taskTime = null;

	/**
	 * Creates a new {@link TaskEditorPanel} object.
	 * 
	 * @param newNotifier {@link TaskChangeObservable}
	 */
	public TaskEditorPanel(final TaskChangeObservable newNotifier)
	{
		super();

		this.notifier = newNotifier;
		createGui();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent event)
	{
		Object source = event.getSource();

		if (source == this.add)
		{
			double timeRequired = 0.0;

			try
			{
				timeRequired = Double.parseDouble(this.taskTime.getText());
			}
			catch (NumberFormatException exc)
			{
				// Ignore
			}

			this.notifier.addTask(new Task(this.taskName.getText(), this.taskNotes.getText(),
					timeRequired));
		}
		else if (source == this.update)
		{
			this.editTask.setName(this.taskName.getText());
			this.editTask.setNotes(this.taskNotes.getText());

			try
			{
				this.editTask.setTimeRequired(Double.parseDouble(this.taskTime.getText()));
			}
			catch (NumberFormatException exc)
			{
				// Ignore
			}

			this.notifier.updateTask(this.editTask);
		}
		else if (source == this.exit)
		{
			System.exit(0);
		}
	}

	/**
     * 
     */
	public void createGui()
	{
		setLayout(new BorderLayout());
		this.editPanel = new JPanel();
		this.editPanel.setLayout(new GridLayout(3, 2));
		this.taskName = new JTextField(20);
		this.taskNotes = new JTextField(20);
		this.taskTime = new JTextField(20);
		this.editPanel.add(new JLabel("Task Name"));
		this.editPanel.add(this.taskName);
		this.editPanel.add(new JLabel("Task Notes"));
		this.editPanel.add(this.taskNotes);
		this.editPanel.add(new JLabel("Time Required"));
		this.editPanel.add(this.taskTime);

		this.controlPanel = new JPanel();
		this.add = new JButton("Add Task");
		this.update = new JButton("Update Task");
		this.exit = new JButton("Exit");
		this.controlPanel.add(this.add);
		this.controlPanel.add(this.update);
		this.controlPanel.add(this.exit);
		this.add.addActionListener(this);
		this.update.addActionListener(this);
		this.exit.addActionListener(this);
		add(this.controlPanel, BorderLayout.SOUTH);
		add(this.editPanel, BorderLayout.CENTER);
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
		// Empty
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
		this.editTask = task;
		this.taskName.setText(task.getName());
		this.taskNotes.setText(task.getNotes());
		this.taskTime.setText("" + task.getTimeRequired());
	}
}
