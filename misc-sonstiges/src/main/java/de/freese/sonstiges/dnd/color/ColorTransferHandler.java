package de.freese.sonstiges.dnd.color;

/*
 * ColorTransferHandler.java is used by the 1.4 DragColorDemo.java and DragColorTextFieldDemo examples.
 */
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * An implementation of TransferHandler that adds support for dropping colors. Dropping a dnd.color on a component having this TransferHandler changes the
 * foreground or the background of the component to the dropped dnd.color, according to the value of the changesForegroundColor property.
 */
class ColorTransferHandler extends TransferHandler
{
    /**
     *
     */
    private static final long serialVersionUID = -77275171383046920L;

    /**
     * 
     */
    private boolean changesForegroundColor = true;

    // The data type exported from JColorChooser.

    /**
     * 
     */
    DataFlavor colorFlavor;

    /**
     *
     */
    String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.awt.Color";

    /**
     * Creates a new ColorTransferHandler object.
     */
    ColorTransferHandler()
    {
        super();

        // Try to create a DataFlavor for dnd.color.
        try
        {
            this.colorFlavor = new DataFlavor(this.mimeType);
        }
        catch (ClassNotFoundException ex)
        {
            // Empty
        }
    }

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors)
    {
        return hasColorFlavor(flavors);
    }

    /**
     * @return boolean
     */
    protected boolean getChangesForegroundColor()
    {
        return this.changesForegroundColor;
    }

    /**
     * Does the flavor list have a Color flavor?
     * 
     * @param flavors {@link DataFlavor}{}
     * @return boolean
     */
    protected boolean hasColorFlavor(final DataFlavor[] flavors)
    {
        if (this.colorFlavor == null)
        {
            return false;
        }

        for (DataFlavor flavor : flavors)
        {
            if (this.colorFlavor.equals(flavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(final JComponent c, final Transferable t)
    {
        if (hasColorFlavor(t.getTransferDataFlavors()))
        {
            try
            {
                Color col = (Color) t.getTransferData(this.colorFlavor);

                if (getChangesForegroundColor())
                {
                    c.setForeground(col);
                }
                else
                {
                    c.setBackground(col);
                }

                return true;
            }
            catch (UnsupportedFlavorException ufe)
            {
                System.out.println("importData: unsupported data flavor");
            }
            catch (IOException ioe)
            {
                System.out.println("importData: I/O exception");
            }
        }

        return false;
    }

    /**
     * @param flag boolean
     */
    protected void setChangesForegroundColor(final boolean flag)
    {
        this.changesForegroundColor = flag;
    }
}
