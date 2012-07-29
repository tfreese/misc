package mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Thomas Freese
 */
public class ContactEditController implements ActionListener
{
	/**
     * 
     */
	private ContactModel model;

	/**
     * 
     */
	private ContactEditView view;

	/**
	 * Creates a new {@link ContactEditController} object.
	 * 
	 * @param m {@link ContactModel}
	 * @param v {@link ContactEditView}
	 */
	public ContactEditController(final ContactModel m, final ContactEditView v)
	{
		this.model = m;
		this.view = v;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		Object source = evt.getSource();

		if (source == this.view.getUpdateRef())
		{
			updateModel();
		}
	}

	/**
	 * @param input String
	 * @return boolean
	 */
	private boolean isAlphabetic(final String input)
	{
		char[] testChars =
		{
				'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
		};

		for (char testChar : testChars)
		{
			if (input.indexOf(testChar) != -1)
			{
				return false;
			}
		}

		return true;
	}

	/**
     * 
     */
	private void updateModel()
	{
		String firstName = null;
		String lastName = null;

		if (isAlphabetic(this.view.getFirstName()))
		{
			firstName = this.view.getFirstName();
		}

		if (isAlphabetic(this.view.getLastName()))
		{
			lastName = this.view.getLastName();
		}

		this.model.updateModel(firstName, lastName, this.view.getTitle(),
				this.view.getOrganization());
	}
}
