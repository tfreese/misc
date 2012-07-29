package mediator;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 * @author Thomas Freese
 */
public class MediatorGui
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
	private ContactMediator mediator;

	/**
     * 
     */
	public void createGui()
	{
		JFrame mainFrame = new JFrame("Mediator example");
		Container content = mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		ContactSelectorPanel select = new ContactSelectorPanel(this.mediator);
		ContactDisplayPanel display = new ContactDisplayPanel(this.mediator);
		ContactEditorPanel edit = new ContactEditorPanel(this.mediator);

		content.add(select);
		content.add(display);
		content.add(edit);
		this.mediator.setContactSelectorPanel(select);
		this.mediator.setContactDisplayPanel(display);
		this.mediator.setContactEditorPanel(edit);
		mainFrame.addWindowListener(new WindowCloseManager());
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/**
	 * @param newMediator {@link ContactMediator}
	 */
	public void setContactMediator(final ContactMediator newMediator)
	{
		this.mediator = newMediator;
	}
}
