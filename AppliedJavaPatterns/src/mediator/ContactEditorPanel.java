package mediator;

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
public class ContactEditorPanel extends JPanel implements ActionListener
{
	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = 9003562937133614503L;

	/**
     * 
     */
	private JButton create = null;

	/**
     * 
     */
	private JButton update = null;

	/**
     * 
     */
	private JTextField firstName = null;

	/**
     * 
     */
	private JTextField lastName = null;

	/**
     * 
     */
	private JTextField title = null;

	/**
     * 
     */
	private JTextField organization = null;

	/**
     * 
     */
	private ContactMediator mediator;

	/**
	 * Creates a new {@link ContactEditorPanel} object.
	 */
	public ContactEditorPanel()
	{
		createGui();
	}

	/**
	 * Creates a new {@link ContactEditorPanel} object.
	 * 
	 * @param newMediator {@link ContactMediator}
	 */
	public ContactEditorPanel(final ContactMediator newMediator)
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
		Object source = evt.getSource();

		if (source == this.create)
		{
			createContact();
		}
		else if (source == this.update)
		{
			updateContact();
		}
	}

	/**
     * 
     */
	public void createContact()
	{
		this.mediator.createContact(this.firstName.getText(), this.lastName.getText(),
				this.title.getText(), this.organization.getText());
	}

	/**
     * 
     */
	public void createGui()
	{
		setLayout(new BorderLayout());

		JPanel editor = new JPanel();

		editor.setLayout(new GridLayout(4, 2));
		editor.add(new JLabel("First Name:"));
		this.firstName = new JTextField(20);
		editor.add(this.firstName);
		editor.add(new JLabel("Last Name:"));
		this.lastName = new JTextField(20);
		editor.add(this.lastName);
		editor.add(new JLabel("Title:"));
		this.title = new JTextField(20);
		editor.add(this.title);
		editor.add(new JLabel("Organization:"));
		this.organization = new JTextField(20);
		editor.add(this.organization);
		add(editor, BorderLayout.CENTER);

		JPanel control = new JPanel();

		this.create = new JButton("Create Contact");
		this.update = new JButton("Update Contact");
		this.create.addActionListener(this);
		this.update.addActionListener(this);
		control.add(this.create);
		control.add(this.update);
		add(control, BorderLayout.SOUTH);
	}

	/**
	 * @param contact {@link Contact}
	 */
	public void setContactFields(final Contact contact)
	{
		this.firstName.setText(contact.getFirstName());
		this.lastName.setText(contact.getLastName());
		this.title.setText(contact.getTitle());
		this.organization.setText(contact.getOrganization());
	}

	/**
	 * @param newMediator {@link ContactMediator}
	 */
	public void setContactMediator(final ContactMediator newMediator)
	{
		this.mediator = newMediator;
	}

	/**
     * 
     */
	public void updateContact()
	{
		this.mediator.updateContact(this.firstName.getText(), this.lastName.getText(),
				this.title.getText(), this.organization.getText());
	}
}
