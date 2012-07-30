package observer;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 * @author Thomas Freese
 */
public class ObserverGui
{
	/**
	 * @author Thomas Freese
	 */
	private class WindowCloseManager extends WindowAdapter
	{
		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosing(final WindowEvent evt)
		{
			System.exit(0);
		}
	}

	/**
     * 
     */
	public void createGui()
	{
		JFrame mainFrame = new JFrame("Observer Pattern Example");
		Container content = mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		TaskChangeObservable observable = new TaskChangeObservable();
		TaskSelectorPanel select = new TaskSelectorPanel(observable);
		TaskHistoryPanel history = new TaskHistoryPanel();
		TaskEditorPanel edit = new TaskEditorPanel(observable);

		observable.addTaskChangeObserver(select);
		observable.addTaskChangeObserver(history);
		observable.addTaskChangeObserver(edit);
		observable.addTask(new Task());
		content.add(select);
		content.add(history);
		content.add(edit);
		mainFrame.addWindowListener(new WindowCloseManager());
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}
