package mvc;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Thomas Freese
 */
public class ContactDisplayView extends JPanel implements ContactView
{
	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = 7709132736674167261L;

	/**
     * 
     */
	private JTextArea display;

	/**
	 * Creates a new {@link ContactDisplayView} object.
	 */
	public ContactDisplayView()
	{
		createGui();
	}

	/**
     * 
     */
	public void createGui()
	{
		setLayout(new BorderLayout());
		this.display = new JTextArea(10, 40);
		this.display.setEditable(false);
		JScrollPane scrollDisplay = new JScrollPane(this.display);

		this.add(scrollDisplay, BorderLayout.CENTER);
	}

	/**
	 * @see mvc.ContactView#refreshContactView(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void refreshContactView(final String newFirstName, final String newLastName,
									final String newTitle, final String newOrganization)
	{
		this.display.setText("UPDATED CONTACT:\nNEW VALUES:\n" + "\tName: " + newFirstName + " "
				+ newLastName + "\n" + "\tTitle: " + newTitle + "\n" + "\tOrganization: "
				+ newOrganization);
	}
}
