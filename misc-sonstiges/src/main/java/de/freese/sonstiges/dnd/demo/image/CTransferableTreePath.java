package de.freese.sonstiges.dnd.demo.image;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;
import javax.swing.tree.TreePath;

/**
 * This represents a TreePath (a node in a JTree) that can be transferred between a drag source and a drop target.
 */
class CTransferableTreePath implements Transferable
{
    // The type of DnD object being dragged...

    /**
     *
     */
    public static final DataFlavor FLAVOR_TREE_PATH = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");

    // public static final DataFlavor TREEPATH_FLAVOR = new DataFlavor(TreePath.class, "TreePath");

    /**
     *
     */
    private static final DataFlavor[] FLAVORS =
    {
            FLAVOR_TREE_PATH
    };

    /**
     *
     */
    private TreePath path;

    /**
     * Constructs a transferrable tree path object for the specified path.
     *
     * @param path {@link TreePath}
     */
    public CTransferableTreePath(final TreePath path)
    {
        super();

        this.path = path;
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public synchronized Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException
    {
        if (flavor.isMimeTypeEqual(FLAVOR_TREE_PATH.getMimeType())) // DataFlavor.javaJVMLocalObjectMimeType))
        {
            return this.path;
        }

        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return FLAVORS;
    }

    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor)
    {
        return List.of(FLAVORS).contains(flavor);
    }
}
