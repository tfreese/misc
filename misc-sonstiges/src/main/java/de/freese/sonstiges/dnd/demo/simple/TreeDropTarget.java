/*
 * Created on 30.08.2004 TODO To change the template for this generated file go to Window - Preferences - Java - Code Style - Code Templates
 */
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * @author Thomas Freese
 */
public class TreeDropTarget implements DropTargetListener
{
    /**
     *
     */
    @SuppressWarnings("unused")
    private final DropTarget target;

    /**
     *
     */
    private final JTree targetTree;

    /**
     * Creates a new TreeDropTarget object.
     *
     * @param tree {@link JTree}
     */
    public TreeDropTarget(final JTree tree)
    {
        this.targetTree = tree;
        this.target = new DropTarget(this.targetTree, this);
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragEnter(final DropTargetDragEvent dtde)
    {
        TreeNode node = getNodeForEvent(dtde);

        if (node.isLeaf())
        {
            dtde.rejectDrag();
        }
        else
        {
            // start by supporting move operations
            // dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            dtde.acceptDrag(dtde.getDropAction());
        }
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    @Override
    public void dragExit(final DropTargetEvent dte)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragOver(final DropTargetDragEvent dtde)
    {
        TreeNode node = getNodeForEvent(dtde);

        if (node.isLeaf())
        {
            dtde.rejectDrag();
        }
        else
        {
            // start by supporting move operations
            // dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            dtde.acceptDrag(dtde.getDropAction());
        }
    }

    /**
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    @Override
    public void drop(final DropTargetDropEvent dtde)
    {
        Point pt = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();

        if (parent.isLeaf())
        {
            dtde.rejectDrop();

            return;
        }

        try
        {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();

            for (DataFlavor flavor : flavors)
            {
                if (tr.isDataFlavorSupported(flavor))
                {
                    dtde.acceptDrop(dtde.getDropAction());

                    TreePath p = (TreePath) tr.getTransferData(flavor);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    model.insertNodeInto(node, parent, 0);
                    dtde.dropComplete(true);

                    return;
                }
            }

            dtde.rejectDrop();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            dtde.rejectDrop();
        }
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde)
    {
        // Empty
    }

    /**
     * @param dtde {@link DropTargetDragEvent}
     * @return {@link TreeNode}
     */
    private TreeNode getNodeForEvent(final DropTargetDragEvent dtde)
    {
        Point p = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree) dtc.getComponent();
        TreePath path = tree.getClosestPathForLocation(p.x, p.y);

        return (TreeNode) path.getLastPathComponent();
    }
}
