package mediator;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Thomas Freese
 */
public class ContactDisplayPanel extends JPanel
{
	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = 6501812477090237026L;

	/**
     * 
     */
	private JTextArea displayRegion;

	/**
	 *
	 */
	private ContactMediator mediator;

	/**
	 * Creates a new {@link ContactDisplayPanel} object.
	 */
	public ContactDisplayPanel()
	{
		super();

		createGui();
	}

	/**
	 * Creates a new {@link ContactDisplayPanel} object.
	 * 
	 * @param newMediator {@link ContactMediator}
	 */
	public ContactDisplayPanel(final ContactMediator newMediator)
	{
		super();

		setContactMediator(newMediator);
		createGui();
	}

	/**
	 * @param contact {@link Contact}
	 */
	public void contactChanged(final Contact contact)
	{
		this.displayRegion.setText("Contact\n\tName: " + contact.getFirstName() + " "
				+ contact.getLastName() + "\n\tTitle: " + contact.getTitle() + "\n\tOrganization: "
				+ contact.getOrganization());
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
	 * @return {@link ContactMediator}
	 */
	public ContactMediator getMediator()
	{
		return this.mediator;
	}

	/**
	 * @param newMediator {@link ContactMediator}
	 */
	public void setContactMediator(final ContactMediator newMediator)
	{
		this.mediator = newMediator;
	}
}
