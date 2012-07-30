/*
 * Created on 31.08.2004
 */
package de.freese.sonstiges.dnd.tree;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.TreePath;

/**
 * @author Thomas Freese
 */
public class TreeDropTargetListener implements DropTargetListener
{
	/**
     * 
     */
	private JTree tree = null;

	/**
     *
     */
	private Timer expandTimer = null;

	/**
     * 
     */
	private TreePath lastPath = null;

	/**
     *
     */
	public TreeDropTargetListener()
	{
		super();

		this.expandTimer = new Timer(1000, new ActionListener()
		{
			/**
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				if ((TreeDropTargetListener.this.tree == null)
						|| (TreeDropTargetListener.this.lastPath == null))
				{
					return;
				}

				// Nicht den RootKnoten
				if (TreeDropTargetListener.this.tree.isRootVisible()
						&& (TreeDropTargetListener.this.tree
								.getRowForPath(TreeDropTargetListener.this.lastPath) == 0))
				{
					return;
				}

				// if (_tree.isExpanded(_lastPath))
				// {
				// _tree.collapsePath(_lastPath);
				// }
				// else
				// {
				TreeDropTargetListener.this.tree.expandPath(TreeDropTargetListener.this.lastPath);

				// }
			}
		});

		this.expandTimer.setRepeats(true);
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dragEnter(final DropTargetDragEvent dtde)
	{
		// Empty
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragExit(final DropTargetEvent dte)
	{
		this.expandTimer.stop();
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dragOver(final DropTargetDragEvent dtde)
	{
		// Ist Target ein JTree ?
		if (!(dtde.getDropTargetContext().getComponent() instanceof JTree))
		{
			return;
		}

		this.tree = (JTree) dtde.getDropTargetContext().getComponent();

		TreePath path =
				this.tree.getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);

		if (!(path == this.lastPath))
		{
			this.lastPath = path;
			this.expandTimer.restart();
		}
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	@Override
	public void drop(final DropTargetDropEvent dtde)
	{
		this.tree = null;
		this.expandTimer.stop();
	}

	/**
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
	 */
	@Override
	public void dropActionChanged(final DropTargetDragEvent dtde)
	{
		// Empty
	}
}
