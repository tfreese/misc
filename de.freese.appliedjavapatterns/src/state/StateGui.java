package state;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * @author Thomas Freese
 */
public class StateGui implements ActionListener
{
	/**
	 * @author Thomas Freese
	 */
	private class StateTableModel extends AbstractTableModel
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -2237135452297420103L;

		/**
         * 
         */
		private final String[] columnNames =
		{
				"Appointment", "Contacts", "Location", "Start Date", "End Date"
		};

		/**
         * 
         */
		private final Appointment[] data;

		/**
		 * Creates a new {@link StateTableModel} object.
		 * 
		 * @param appointments {@link Appointment}[]
		 */
		public StateTableModel(final Appointment[] appointments)
		{
			super();

			this.data = appointments;
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return this.columnNames.length;
		}

		/**
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(final int column)
		{
			return this.columnNames[column];
		}

		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			return this.data.length;
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(final int row, final int column)
		{
			Object value = null;

			switch (column)
			{
				case 0:
				{
					value = this.data[row].getReason();

					break;
				}

				case 1:
				{
					value = this.data[row].getContacts();

					break;
				}

				case 2:
				{
					value = this.data[row].getLocation();

					break;
				}

				case 3:
				{
					value = this.data[row].getStartDate();

					break;
				}

				case 4:
				{
					value = this.data[row].getEndDate();

					break;
				}
			}

			return value;
		}

		/**
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(final int row, final int column)
		{
			return ((column == 0) || (column == 2)) ? true : false;
		}

		/**
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(final Object value, final int row, final int column)
		{
			switch (column)
			{
				case 0:
				{
					this.data[row].setReason((String) value);
					StateGui.this.editor.edit();

					break;
				}

				case 1:
				{
					break;
				}

				case 2:
				{
					this.data[row].setLocation(new LocationImpl((String) value));
					StateGui.this.editor.edit();

					break;
				}

				case 3:
				{
					break;
				}

				case 4:
				{
					break;
				}
			}
		}
	}

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
	private JPanel editPanel = null;

	/**
     * 
     */
	private CalendarEditor editor;

	/**
     * 
     */
	private JFrame mainFrame;

	/**
     * 
     */
	private JButton save = null;

	/**
     * 
     */
	private JButton exit = null;

	/**
	 * Creates a new {@link StateGui} object.
	 * 
	 * @param edit {@link CalendarEditor}
	 */
	public StateGui(final CalendarEditor edit)
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

		if (originator == this.save)
		{
			saveAppointments();
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
		this.mainFrame = new JFrame("State Pattern Example");
		Container content = this.mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		this.editPanel = new JPanel();
		this.editPanel.setLayout(new BorderLayout());
		JTable appointmentTable =
				new JTable(new StateTableModel(this.editor.getAppointments().toArray(
						new Appointment[1])));

		this.editPanel.add(new JScrollPane(appointmentTable));
		content.add(this.editPanel);

		this.controlPanel = new JPanel();
		this.save = new JButton("Save Appointments");
		this.exit = new JButton("Exit");
		this.controlPanel.add(this.save);
		this.controlPanel.add(this.exit);
		content.add(this.controlPanel);

		this.save.addActionListener(this);
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
	private void saveAppointments()
	{
		this.editor.save();
	}
}
