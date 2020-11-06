/*
 * Created on 31.08.2004
 */
package de.freese.sonstiges.dnd.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * TransferHandler fuer DnD von JTree-JTree
 *
 * @author Thomas Freese
 */
public class TreeTransferHandler extends TransferHandler
{
    /**
     *
     */
    private static final long serialVersionUID = -3761501619688863055L;

    /**
     * Wird in der Methode exportDone benoetigt, damit ein Knoten, der auf sich selbst kopiert wird nicht geloescht wird.
     */
    private TreePath targetPath;

    /**
     *
     */
    public TreeTransferHandler()
    {
        super();
    }

    /**
     * Ist DnD erlaubt ?
     *
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors)
    {
        for (DataFlavor transferFlavor : transferFlavors)
        {
            if (TransferableTreeNode.TREE_PATH_FLAVOR.equals(transferFlavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Erzeugen der Transferdaten.
     *
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(final JComponent c)
    {
        if (!(c instanceof JTree))
        {
            return null;

            // oder ???
            // return new TransferableTreeNode(null);
        }

        JTree sourceTree = (JTree) c;

        return new TransferableTreeNode(sourceTree.getSelectionPath());
    }

    /**
     * Abschluss des Kopiervorgangs.
     *
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    @Override
    protected void exportDone(final JComponent source, final Transferable data, final int action)
    {
        TreePath sourcepath = getSourcePath(data);

        if ((action == MOVE) && (source instanceof JTree) && (sourcepath != null))
        {
            JTree sourceTree = (JTree) source;
            DefaultTreeModel sourceModell = (DefaultTreeModel) sourceTree.getModel();

            // Nur Loeschen wenn Source != Target ist.
            // Kopien auf sich selbst sind erlaubt
            if (this.targetPath != sourcepath)
            {
                sourceModell.removeNodeFromParent((MutableTreeNode) sourcepath.getLastPathComponent());
            }
        }
    }

    /**
     * Was darf DnD ?
     *
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(final JComponent c)
    {
        return COPY_OR_MOVE;
    }

    /**
     * ServiceMethode, liefert den SourcePath aus dem Transferable-Daten.
     *
     * @param t Transferable
     * @return SourcePath
     */
    private TreePath getSourcePath(final Transferable t)
    {
        TreePath sourcePath = null;

        try
        {
            if ((t == null) || !(t.getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR) instanceof TreePath))
            {
                return null;
            }

            sourcePath = (TreePath) t.getTransferData(TransferableTreeNode.TREE_PATH_FLAVOR);
        }
        catch (UnsupportedFlavorException ex)
        {
            // Ignore
        }
        catch (IOException ex)
        {
            // Ignore
        }

        return sourcePath;
    }

    /**
     * Kopieren der Transferdaten.
     *
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(final JComponent comp, final Transferable t)
    {
        if (canImport(comp, t.getTransferDataFlavors()) && (comp instanceof JTree))
        {
            // Target bestimmen
            JTree targetTree = (JTree) comp;

            // Wird in exportDone gegen den sourcepath geprueft
            this.targetPath = targetTree.getSelectionPath();

            TreePath sourcePath = getSourcePath(t);

            if (sourcePath == null)
            {
                return false;
            }

            DefaultTreeModel targetModell = (DefaultTreeModel) targetTree.getModel();
            DefaultMutableTreeNode targetParent = (DefaultMutableTreeNode) this.targetPath.getLastPathComponent();

            // sourcePath in das Target einfuegen, bei Move wird in der Methode exportDone geloescht
            // wenn sourcePath != _targetPath ist.
            targetModell.insertNodeInto((MutableTreeNode) sourcePath.getLastPathComponent(), targetParent, targetParent.getChildCount());

            return true;
        }

        return false;
    }
}
