/*
 * Copyright (c) 2002-2007 JGoodies Karsten Lentzsch. All Rights Reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met: o Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. o Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. o Neither the name of
 * JGoodies Karsten Lentzsch nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.binding.tutorial.manager;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.tutorial.Album;

/**
 * Provides the models and Actions for managing and editing Albums. Works with an underlying
 * AlbumManager that provides a ListModel for the Albums and operations to add, remove, and change a
 * Album. In other words, this class turns the raw data and operations form the AlbumManager into a
 * form usable in a user interface.
 * <p>
 * This model keeps the Albums in a SelectionInList, refers to another PresentationModel for editing
 * the selected Album, and provides Actions for the Album operations: add, remove and edit the
 * selected Album.
 * 
 * @author Karsten Lentzsch
 * @version $Revision: 1.10 $
 * @see AlbumManager
 * @see com.jgoodies.binding.PresentationModel
 */

public final class AlbumManagerModel
{

	private final class DeleteAction extends AbstractAction
	{

		private DeleteAction()
		{
			super("Delete");
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			doDelete();
		}
	}

	/**
	 * A mouse listener that edits the selected item on double click.
	 */
	private final class DoubleClickHandler extends MouseAdapter
	{
		/**
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(final MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
			{
				editSelectedItem();
			}
		}
	}

	private final class EditAction extends AbstractAction
	{

		private EditAction()
		{
			super("Edit\u2026");
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			doEdit();
		}
	}

	private final class NewAction extends AbstractAction
	{

		private NewAction()
		{
			super("New\u2026");
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			doNew();
		}
	}

	/**
	 * Enables or disables this model's Actions when it is notified about a change in the
	 * <em>selectionEmpty</em> property of the SelectionInList.
	 */
	private final class SelectionEmptyHandler implements PropertyChangeListener
	{

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent evt)
		{
			updateActionEnablement();
		}
	}

	// Instance Creation ******************************************************

	/**
	 * Holds the List of Albums and provides operations to create, add, remove and change a Album.
	 */
	private final AlbumManager albumManager;

	/**
	 * Holds the list of managed albums plus a single selection.
	 */
	private SelectionInList<Album> albumSelection;

	private Action deleteAction;

	// Exposing Models and Actions ********************************************

	private Action editAction;

	private Action newAction;

	/**
	 * Constructs an AlbumManager for editing the given list of Albums.
	 * 
	 * @param albumManager the list of albums to edit
	 */
	public AlbumManagerModel(final AlbumManager albumManager)
	{
		this.albumManager = albumManager;
		initModels();
		initEventHandling();
	}

	/**
	 * Lets the AlbumManager add the given Album to the list of Albums. The AlbumManager fires the
	 * list data change event. If the AlbumManager won't fire this event, we could use
	 * {@link SelectionInList#fireIntervalAdded(int, int)}.
	 */
	private void addItem(final Album albumToAdd)
	{
		this.albumManager.addItem(albumToAdd);
	}

	private Album createAndAddItem()
	{
		Album newAlbum = this.albumManager.createItem();
		boolean canceled = openAlbumEditor(newAlbum);
		if (!canceled)
		{
			addItem(newAlbum);
			return newAlbum;
		}
		return null;
	}

	// Action Operations ******************************************************
	// For every Action we provide a method that is performed for this Action.
	// This makes it easier to overview this class.

	/**
	 * Lets the AlbumManager removes the selected Album from the list of Albums. The AlbumManager
	 * fires the list data change event. If the AlbumManager wouldn't fire this event, we could use
	 * {@link SelectionInList#fireIntervalRemoved(int, int)}.
	 */
	private void doDelete()
	{
		this.albumManager.removeItem(getSelectedItem());
	}

	/**
	 * Edits the selected item and marks it as changed, if the editor dialog has not been canceled.
	 */
	private void doEdit()
	{
		editSelectedItem();
	}

	private void doNew()
	{
		Album newAlbum = createAndAddItem();
		getAlbumSelection().setSelection(newAlbum);
	}

	// Managing Albums ********************************************************

	/**
	 * Edits the selected item. If the editor dialog has not been canceled, the presentations is
	 * notified that the contents has changed.
	 * <p>
	 * This implementation fires the contents change event using
	 * {@link SelectionInList#fireSelectedContentsChanged()}. Since the album SelectionInList
	 * contains a ListModel, the <code>albumSelection</code> managed by the AlbumManager, the
	 * AlbumManager could fire that event. However, I favored to fire the contents change in the
	 * SelectionInList because this approach works with underlying Lists, ListModels, and managers
	 * that don't fire contents changes.
	 */
	private void editSelectedItem()
	{
		boolean canceled = openAlbumEditor(getSelectedItem());
		if (!canceled)
		{
			getAlbumSelection().fireSelectedContentsChanged();
		}
	}

	/**
	 * Returns the List of Albums with the current selection. Useful to display the managed Albums
	 * in a JList or JTable.
	 * 
	 * @return the List of Albums with selection
	 */
	public SelectionInList<Album> getAlbumSelection()
	{
		return this.albumSelection;
	}

	/**
	 * Returns the Action that deletes the selected Album from this model's List of managed albums.
	 * 
	 * @return The Action that deletes the selected Album
	 */
	public Action getDeleteAction()
	{
		return this.deleteAction;
	}

	/**
	 * Returns a MouseListener that selects and edits a Album on double-click.
	 * 
	 * @return a MouseListener that selects and edits a Album on double-click.
	 */
	public MouseListener getDoubleClickHandler()
	{
		return new DoubleClickHandler();
	}

	/**
	 * Returns the Action that opens a AlbumEditorDialog on the selected Album.
	 * 
	 * @return the Action that opens a AlbumEditorDialog on the selected Album
	 */
	public Action getEditAction()
	{
		return this.editAction;
	}

	// Actions ****************************************************************

	/**
	 * Returns the Action that creates a new Album and adds it to this model's List of managed
	 * Albums. Opens a AlbumEditorDialog on the newly created Album.
	 * 
	 * @return the Action that creates and adds a new Album
	 */
	public Action getNewAction()
	{
		return this.newAction;
	}

	private Album getSelectedItem()
	{
		return getAlbumSelection().getSelection();
	}

	/**
	 * Initializes the event handling by just registering a handler that updates the Action
	 * enablement if the albumSelection's 'selectionEmpty' property changes.
	 */
	private void initEventHandling()
	{
		this.albumSelection.addPropertyChangeListener(SelectionInList.PROPERTY_SELECTION_EMPTY,
				new SelectionEmptyHandler());
	}

	// Event Handling *********************************************************

	/**
	 * Initializes the SelectionInList and Action. In this case we eagerly initialize the Actions.
	 * As an alternative you can create the Actions lazily in the Action getter methods. To
	 * synchronize the Action enablement with the selection state, we update the enablement now.
	 */
	private void initModels()
	{
		this.albumSelection = new SelectionInList<>(this.albumManager.getManagedAlbums());

		this.newAction = new NewAction();
		this.editAction = new EditAction();
		this.deleteAction = new DeleteAction();
		updateActionEnablement();
	}

	/**
	 * Opens a AlbumEditorDialog for the given Album.
	 * 
	 * @param album the Album to be edited
	 * @return true if the dialog has been canceled, false if accepted
	 */
	private boolean openAlbumEditor(final Album album)
	{
		AlbumEditorDialog dialog = new AlbumEditorDialog(null, album);
		dialog.open();
		return dialog.hasBeenCanceled();
	}

	private void updateActionEnablement()
	{
		boolean hasSelection = getAlbumSelection().hasSelection();
		getEditAction().setEnabled(hasSelection);
		getDeleteAction().setEnabled(hasSelection);
	}

}
