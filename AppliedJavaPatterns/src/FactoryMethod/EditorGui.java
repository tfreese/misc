package FactoryMethod;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Thomas Freese
 */
public class EditorGui implements ActionListener
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
			exitApplication();
		}
	}

	/**
     * 
     */
	private JPanel controlPanel = null;

	/**
     * 
     */
	private JPanel displayPanel = null;

	/**
     * 
     */
	private JPanel editorPanel = null;

	/**
     * 
     */
	private JTextArea display = null;

	/**
     * 
     */
	private ItemEditor editor = null;

	/**
     * 
     */
	private JFrame mainFrame = null;

	/**
     * 
     */
	private JButton update = null;

	/**
	 * 
	 */
	private JButton exit = null;

	/**
	 * Creates a new {@link EditorGui} object.
	 * 
	 * @param edit {@link ItemEditor}
	 */
	public EditorGui(final ItemEditor edit)
	{
		this.editor = edit;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		Object originator = evt.getSource();

		if (originator == this.update)
		{
			updateItem();
		}
		else if (originator == this.exit)
		{
			exitApplication();
		}
	}

	/**
     * 
     */
	public void createGui()
	{
		this.mainFrame = new JFrame("Factory Pattern Example");
		Container content = this.mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		this.editorPanel = new JPanel();
		this.editorPanel.add(this.editor.getGUI());
		content.add(this.editorPanel);

		this.displayPanel = new JPanel();
		this.display = new JTextArea(10, 40);
		this.display.setEditable(false);
		this.displayPanel.add(this.display);
		content.add(this.displayPanel);

		this.controlPanel = new JPanel();
		this.update = new JButton("Update Item");
		this.exit = new JButton("Exit");
		this.controlPanel.add(this.update);
		this.controlPanel.add(this.exit);
		content.add(this.controlPanel);

		this.update.addActionListener(this);
		this.exit.addActionListener(this);

		this.mainFrame.addWindowListener(new WindowCloseManager());
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}

	/**
     * 
     */
	private void exitApplication()
	{
		System.exit(0);
	}

	/**
     * 
     */
	private void updateItem()
	{
		this.editor.commitChanges();
		this.display.setText(this.editor.toString());
	}
}
