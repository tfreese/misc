package de.freese.sonstiges.dnd.extended;

/*
 * ListTransferHandler.java is used by the 1.4 ExtendedDnDDemo.java example.
 */
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * @author Thomas Freese
 */
public class ListTransferHandler extends StringTransferHandler
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3208151404479849978L;

	/**
	 * 
	 */
	private int[] indices = null;

	/**
	 * 
	 */
	private int addCount = 0; // Number of items added.

	/**
	 *
	 */
	private int addIndex = -1; // Location where items were added

	/**
	 * @see de.freese.sonstiges.dnd.extended.StringTransferHandler#cleanup(javax.swing.JComponent,
	 *      boolean)
	 */
	@Override
	protected void cleanup(final JComponent c, final boolean remove)
	{
		if (remove && (this.indices != null))
		{
			JList source = (JList) c;
			DefaultListModel model = (DefaultListModel) source.getModel();

			// If we are moving items around in the same list, we
			// need to adjust the indices accordingly, since those
			// after the insertion point have moved.
			if (this.addCount > 0)
			{
				for (int i = 0; i < this.indices.length; i++)
				{
					if (this.indices[i] > this.addIndex)
					{
						this.indices[i] += this.addCount;
					}
				}
			}

			for (int i = this.indices.length - 1; i >= 0; i--)
			{
				model.remove(this.indices[i]);
			}
		}

		this.indices = null;
		this.addCount = 0;
		this.addIndex = -1;
	}

	/**
	 * @see de.freese.sonstiges.dnd.extended.StringTransferHandler#exportString(javax.swing.JComponent)
	 */
	@Override
	protected String exportString(final JComponent c)
	{
		JList list = (JList) c;
		this.indices = list.getSelectedIndices();

		List<?> values = list.getSelectedValuesList();

		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < values.size(); i++)
		{
			Object val = values.get(i);
			buff.append((val == null) ? "" : val.toString());

			if (i != (values.size() - 1))
			{
				buff.append("\n");
			}
		}

		return buff.toString();
	}

	/**
	 * @see de.freese.sonstiges.dnd.extended.StringTransferHandler#importString(javax.swing.JComponent,
	 *      java.lang.String)
	 */
	@Override
	protected void importString(final JComponent c, final String str)
	{
		JList target = (JList) c;
		DefaultListModel listModel = (DefaultListModel) target.getModel();
		int index = target.getSelectedIndex();

		// Prevent the user from dropping data back on itself.
		// For example, if the user is moving items #4,#5,#6 and #7 and
		// attempts to insert the items after item #5, this would
		// be problematic when removing the original items.
		// So this is not allowed.
		if ((this.indices != null) && (index >= (this.indices[0] - 1))
				&& (index <= this.indices[this.indices.length - 1]))
		{
			this.indices = null;

			return;
		}

		int max = listModel.getSize();

		if (index < 0)
		{
			index = max;
		}
		else
		{
			index++;

			if (index > max)
			{
				index = max;
			}
		}

		this.addIndex = index;

		String[] values = str.split("\n");
		this.addCount = values.length;

		for (String value : values)
		{
			listModel.add(index++, value);
		}
	}
}
