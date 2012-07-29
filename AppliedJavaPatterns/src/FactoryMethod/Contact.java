package FactoryMethod;

import java.awt.GridLayout;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class Contact implements Editable, Serializable
{
	/**
	 * @author Thomas Freese
	 */
	private class ContactEditor implements ItemEditor, Serializable
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -7809642709387671932L;

		/**
         * 
         */
		private transient JTextField nameField;

		/**
         * 
         */
		private transient JPanel panel;

		/**
         * 
         */
		private transient JTextField relationField;

		/**
		 * @see FactoryMethod.ItemEditor#commitChanges()
		 */
		@Override
		public void commitChanges()
		{
			if (this.panel != null)
			{
				Contact.this.name = this.nameField.getText();
				Contact.this.relationship = this.relationField.getText();
			}
		}

		/**
		 * @see FactoryMethod.ItemEditor#getGUI()
		 */
		@Override
		public JComponent getGUI()
		{
			if (this.panel == null)
			{
				this.panel = new JPanel();
				this.nameField = new JTextField(Contact.this.name);
				this.relationField = new JTextField(Contact.this.relationship);
				this.panel.setLayout(new GridLayout(2, 2));
				this.panel.add(new JLabel("Name:"));
				this.panel.add(this.nameField);
				this.panel.add(new JLabel("Relationship:"));
				this.panel.add(this.relationField);
			}
			else
			{
				this.nameField.setText(Contact.this.name);
				this.relationField.setText(Contact.this.relationship);
			}

			return this.panel;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "\nContact:\n" + "    Name: " + Contact.this.name + "\n" + "    Relationship: "
					+ Contact.this.relationship;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 8590754646025330423L;

	/**
     * 
     */
	private String name;

	/**
     * 
     */
	private String relationship;

	/**
	 * @see FactoryMethod.Editable#getEditor()
	 */
	@Override
	public ItemEditor getEditor()
	{
		return new ContactEditor();
	}
}
