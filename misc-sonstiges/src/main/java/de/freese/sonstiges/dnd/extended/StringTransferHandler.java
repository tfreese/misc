package de.freese.sonstiges.dnd.extended;

/*
 * StringTransferHandler.java is used by the 1.4 ExtendedDnDDemo.java example.
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * @author Thomas Freese
 */
public abstract class StringTransferHandler extends TransferHandler
{
    /**
     *
     */
    private static final long serialVersionUID = -7174980141806424667L;

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors)
    {
        for (DataFlavor flavor : flavors)
        {
            if (DataFlavor.stringFlavor.equals(flavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param c {@link JComponent}
     * @param remove boolean
     */
    protected abstract void cleanup(JComponent c, boolean remove);

    /**
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(final JComponent c)
    {
        return new StringSelection(exportString(c));
    }

    /**
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action)
    {
        cleanup(c, action == MOVE);
    }

    /**
     * @param c {@link JComponent}
     * @return String
     */
    protected abstract String exportString(JComponent c);

    /**
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(final JComponent c)
    {
        return COPY_OR_MOVE;
    }

    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(final JComponent c, final Transferable t)
    {
        if (canImport(c, t.getTransferDataFlavors()))
        {
            try
            {
                String str = (String) t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);

                return true;
            }
            catch (UnsupportedFlavorException ex)
            {
                // Empty
            }
            catch (IOException ex)
            {
                // Empty
            }
        }

        return false;
    }

    /**
     * @param c {@link JComponent}
     * @param str String
     */
    protected abstract void importString(JComponent c, String str);
}
