/**
 * 
 */
package com.jgoodies.binding.tutorial;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import com.jgoodies.binding.adapter.AbstractTableAdapter;

/**
 * Consists only of static methods that return instances reused in multiple examples of the JGoodies
 * Binding tutorial.
 * 
 * @author Karsten Lentzsch
 * @version $Revision: 1.21 $
 */
public final class TutorialUtils
{

	/**
	 * Used to renders Albums in JLists and JComboBoxes. If the combo box selection is null, an
	 * empty text <code>""</code> is rendered.
	 */
	private static final class AlbumListCellRenderer extends DefaultListCellRenderer
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -1890663672747195101L;

		/**
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(final JList list, final Object value,
														final int index, final boolean isSelected,
														final boolean cellHasFocus)
		{
			Component component =
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			Album album = (Album) value;
			setText(album == null ? "" : (" " + album.getTitle()));
			return component;
		}
	}

	/**
	 * Describes how to present an Album in a JTable.
	 */
	private static final class AlbumTableModel extends AbstractTableAdapter<Album>
	{

		/**
		 * 
		 */
		private static final String[] COLUMNS =
		{
				"Artist", "Title", "Classical", "Composer"
		};

		/**
		 * 
		 */
		private static final long serialVersionUID = -6037463426520977616L;

		/**
		 * Erstellt ein neues {@link AlbumTableModel} Objekt.
		 * 
		 * @param listModel ListModel
		 */
		private AlbumTableModel(final ListModel listModel)
		{
			super(listModel, COLUMNS);
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			Album album = getRow(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return album.getArtist();
				case 1:
					return album.getTitle();
				case 2:
					return Boolean.valueOf(album.isClassical());
				case 3:
					return album.isClassical() ? album.getComposer() : "";
				default:
					throw new IllegalStateException("Unknown column");
			}
		}

	}

	/**
	 * An Action that exists the System.
	 */
	private static final class CloseAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4903279978991362048L;

		/**
		 * Erstellt ein neues {@link CloseAction} Objekt.
		 */
		private CloseAction()
		{
			super("Close");
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(final ActionEvent e)
		{
			System.exit(0);
		}
	}

	/**
	 * Writes the source, property name, old/new value to the system console.
	 */
	private static final class DebugPropertyChangeListener implements PropertyChangeListener
	{

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(final PropertyChangeEvent evt)
		{
			System.out.println();
			System.out.println("The source: " + evt.getSource());
			System.out.println("changed '" + evt.getPropertyName() + "' from '" + evt.getOldValue()
					+ "' to '" + evt.getNewValue() + "'.");
		}
	}

	/**
	 * Used to hold debug listeners, so they won't be removed by the garbage collector, even if
	 * registered by a listener list that is based on weak references.
	 * 
	 * @see #createDebugPropertyChangeListener()
	 * @see WeakReference
	 */
	private static List<PropertyChangeListener> debugListeners =
			new LinkedList<PropertyChangeListener>();

	/**
	 * Creates and returns a renderer for Albums in a list.
	 * 
	 * @return a renderer for Albums in lists.
	 */
	public static ListCellRenderer createAlbumListCellRenderer()
	{
		return new AlbumListCellRenderer();
	}

	// Renderer ***************************************************************

	/**
	 * Creates and returns a TableModel for Albums with columns for the title, artist, classical and
	 * composer.
	 * 
	 * @param listModel the ListModel of Albums to display in the table
	 * @return a TableModel on the list of Albums
	 */
	public static TableModel createAlbumTableModel(final ListModel listModel)
	{
		return new AlbumTableModel(listModel);
	}

	// TableModel *************************************************************

	/**
	 * Returns a listener that writes bean property changes to the console. The log entry includes
	 * the PropertyChangeEvent's source, property name, old value, and new value.
	 * 
	 * @return a debug listener that logs bean changes to the console
	 */
	public static PropertyChangeListener createDebugPropertyChangeListener()
	{
		PropertyChangeListener listener = new DebugPropertyChangeListener();
		debugListeners.add(listener);
		return listener;
	}

	// Debug Listener *********************************************************

	/**
	 * Creates and returns an Action that exists the system if performed.
	 * 
	 * @return an Action that exists the system if performed
	 * @see System#exit(int)
	 */
	public static Action getCloseAction()
	{
		return new CloseAction();
	}

	/**
	 * Locates the given component on the screen's center.
	 * 
	 * @param component the component to be centered
	 */
	public static void locateOnOpticalScreenCenter(final Component component)
	{
		Dimension paneSize = component.getSize();
		Dimension screenSize = component.getToolkit().getScreenSize();
		component.setLocation((screenSize.width - paneSize.width) / 2,
				(int) ((screenSize.height - paneSize.height) * 0.45));
	}

	// Actions ****************************************************************

	/**
	 * Erstellt ein neues {@link TutorialUtils} Objekt.
	 */
	private TutorialUtils()
	{
		super();
	}

}
