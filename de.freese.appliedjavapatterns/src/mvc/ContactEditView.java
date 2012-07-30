package mvc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class ContactEditView extends JPanel implements ContactView
{
	/**
	 * @author Thomas Freese
	 */
	private class ExitHandler implements ActionListener
	{
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent event)
		{
			System.exit(0);
		}
	}

	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = -2065843892454066506L;

	/**
     * 
     */
	private static final String UPDATE_BUTTON = "Update";

	/**
     * 
     */
	private static final String EXIT_BUTTON = "Exit";

	/**
     * 
     */
	private static final String CONTACT_FIRST_NAME = "First Name  ";

	/**
     * 
     */
	private static final String CONTACT_LAST_NAME = "Last Name  ";

	/**
     * 
     */
	private static final String CONTACT_TITLE = "Title  ";

	/**
     * 
     */
	private static final String CONTACT_ORG = "Organization  ";

	/**
     * 
     */
	private static final int FNAME_COL_WIDTH = 25;

	/**
     * 
     */
	private static final int LNAME_COL_WIDTH = 40;

	/**
     * 
     */
	private static final int TITLE_COL_WIDTH = 25;

	/**
     * 
     */
	private static final int ORG_COL_WIDTH = 40;

	/**
     * 
     */
	private ContactEditController controller;

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
	private JLabel firstNameLabel = null;

	/**
     * 
     */
	private JLabel lastNameLabel = null;

	/**
     * 
     */
	private JLabel titleLabel = null;

	/**
     * 
     */
	private JLabel organizationLabel = null;

	/**
     * 
     */
	private JButton update = null;

	/**
     * 
     */
	private JButton exit = null;

	/**
	 * Creates a new {@link ContactEditView} object.
	 * 
	 * @param model {@link ContactModel}
	 */
	public ContactEditView(final ContactModel model)
	{
		this.controller = new ContactEditController(model, this);
		createGui();
	}

	/**
	 * Creates a new {@link ContactEditView} object.
	 * 
	 * @param model {@link ContactModel}
	 * @param newController {@link ContactEditController}
	 */
	public ContactEditView(final ContactModel model, final ContactEditController newController)
	{
		this.controller = newController;
		createGui();
	}

	/**
     * 
     */
	public void createGui()
	{
		this.update = new JButton(UPDATE_BUTTON);
		this.exit = new JButton(EXIT_BUTTON);

		this.firstNameLabel = new JLabel(CONTACT_FIRST_NAME);
		this.lastNameLabel = new JLabel(CONTACT_LAST_NAME);
		this.titleLabel = new JLabel(CONTACT_TITLE);
		this.organizationLabel = new JLabel(CONTACT_ORG);

		this.firstName = new JTextField(FNAME_COL_WIDTH);
		this.lastName = new JTextField(LNAME_COL_WIDTH);
		this.title = new JTextField(TITLE_COL_WIDTH);
		this.organization = new JTextField(ORG_COL_WIDTH);

		JPanel editPanel = new JPanel();

		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));

		JPanel labelPanel = new JPanel();

		labelPanel.setLayout(new GridLayout(0, 1));

		labelPanel.add(this.firstNameLabel);
		labelPanel.add(this.lastNameLabel);
		labelPanel.add(this.titleLabel);
		labelPanel.add(this.organizationLabel);

		editPanel.add(labelPanel);

		JPanel fieldPanel = new JPanel();

		fieldPanel.setLayout(new GridLayout(0, 1));

		fieldPanel.add(this.firstName);
		fieldPanel.add(this.lastName);
		fieldPanel.add(this.title);
		fieldPanel.add(this.organization);

		editPanel.add(fieldPanel);

		JPanel controlPanel = new JPanel();

		controlPanel.add(this.update);
		controlPanel.add(this.exit);
		this.update.addActionListener(this.controller);
		this.exit.addActionListener(new ExitHandler());

		setLayout(new BorderLayout());
		add(editPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
	}

	/**
	 * @return String
	 */
	public String getFirstName()
	{
		return this.firstName.getText();
	}

	/**
	 * @return String
	 */
	public String getLastName()
	{
		return this.lastName.getText();
	}

	/**
	 * @return String
	 */
	public String getOrganization()
	{
		return this.organization.getText();
	}

	/**
	 * @return String
	 */
	public String getTitle()
	{
		return this.title.getText();
	}

	/**
	 * @return Object
	 */
	public Object getUpdateRef()
	{
		return this.update;
	}

	/**
	 * @see mvc.ContactView#refreshContactView(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void refreshContactView(final String newFirstName, final String newLastName,
									final String newTitle, final String newOrganization)
	{
		this.firstName.setText(newFirstName);
		this.lastName.setText(newLastName);
		this.title.setText(newTitle);
		this.organization.setText(newOrganization);
	}
}
