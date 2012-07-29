package mediator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * @author Thomas Freese
 */
public class ContactSelectorPanel extends JPanel implements ActionListener
{
	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = 109282102129786363L;

	/**
     * 
     */
	private ContactMediator mediator;

	/**
     * 
     */
	private JComboBox<Contact> selector;

	/**
	 * Creates a new {@link ContactSelectorPanel} object.
	 */
	public ContactSelectorPanel()
	{
		createGui();
	}

	/**
	 * Creates a new {@link ContactSelectorPanel} object.
	 * 
	 * @param newMediator {@link ContactMediator}
	 */
	public ContactSelectorPanel(final ContactMediator newMediator)
	{
		setContactMediator(newMediator);
		createGui();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		this.mediator.selectContact((Contact) this.selector.getSelectedItem());
	}

	/**
	 * @param contact {@link Contact}
	 */
	public void addContact(final Contact contact)
	{
		this.selector.addItem(contact);
		this.selector.setSelectedItem(contact);
	}

	/**
     * 
     */
	public void createGui()
	{
		this.selector = new JComboBox<>(this.mediator.getAllContacts());
		this.selector.addActionListener(this);
		add(this.selector);
	}

	/**
	 * @param newMediator {@link ContactMediator}
	 */
	public void setContactMediator(final ContactMediator newMediator)
	{
		this.mediator = newMediator;
	}
}
