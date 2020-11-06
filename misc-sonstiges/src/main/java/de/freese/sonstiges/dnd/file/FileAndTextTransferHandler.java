package de.freese.sonstiges.dnd.file;

/*
 * FileAndTextTransferHandler.java is used by the 1.4 DragFileDemo.java example.
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

/**
 * @author Thomas Freese
 */
class FileAndTextTransferHandler extends TransferHandler
{
    /**
     *
     */
    private static final long serialVersionUID = 6906658392318378092L;

    /**
     *
     */
    private final DataFlavor fileFlavor;

    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.

    /**
     *
     */
    private String newline = "\n";

    // Start and end position in the source text.
    // We need this information when performing a MOVE
    // in order to remove the dragged text from the source.

    /**
     *
     */
    private Position p0;

    /**
     *
     */
    private Position p1;

    /**
     *
     */
    private boolean shouldRemove;

    /**
     *
     */
    private JTextArea source;

    /**
     *
     */
    private final DataFlavor stringFlavor;

    /**
     *
     */
    private final TabbedPaneController tpc;

    /**
     * Creates a new FileAndTextTransferHandler object.
     *
     * @param t {@link TabbedPaneController}
     */
    public FileAndTextTransferHandler(final TabbedPaneController t)
    {
        super();

        this.tpc = t;
        this.fileFlavor = DataFlavor.javaFileListFlavor;
        this.stringFlavor = DataFlavor.stringFlavor;
    }

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors)
    {
        if (hasFileFlavor(flavors))
        {
            return true;
        }

        if (hasStringFlavor(flavors))
        {
            return true;
        }

        return false;
    }

    /**
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(final JComponent c)
    {
        this.source = (JTextArea) c;

        int start = this.source.getSelectionStart();
        int end = this.source.getSelectionEnd();
        Document doc = this.source.getDocument();

        if (start == end)
        {
            return null;
        }

        try
        {
            this.p0 = doc.createPosition(start);
            this.p1 = doc.createPosition(end);
        }
        catch (BadLocationException ex)
        {
            System.out.println("Can't create position - unable to remove text from source.");
        }

        this.shouldRemove = true;

        String data = this.source.getSelectedText();

        return new StringSelection(data);
    }

    /**
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action)
    {
        if (this.shouldRemove && (action == MOVE))
        {
            if ((this.p0 != null) && (this.p1 != null) && (this.p0.getOffset() != this.p1.getOffset()))
            {
                try
                {
                    JTextComponent tc = (JTextComponent) c;
                    tc.getDocument().remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
                }
                catch (BadLocationException ex)
                {
                    System.out.println("Can't remove text from source.");
                }
            }
        }

        this.source = null;
    }

    /**
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(final JComponent c)
    {
        return COPY_OR_MOVE;
    }

    /**
     * @param flavors {@link DataFlavor}{}
     * @return boolean
     */
    private boolean hasFileFlavor(final DataFlavor[] flavors)
    {
        for (DataFlavor flavor : flavors)
        {
            if (this.fileFlavor.equals(flavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param flavors {@link DataFlavor}{}
     * @return boolean
     */
    private boolean hasStringFlavor(final DataFlavor[] flavors)
    {
        for (DataFlavor flavor : flavors)
        {
            if (this.stringFlavor.equals(flavor))
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
        if (!canImport(c, t.getTransferDataFlavors()))
        {
            return false;
        }

        JTextArea tc = null;

        // A real application would load the file in another
        // thread in order to not block the UI. This step
        // was omitted here to simplify the code.
        try
        {
            if (hasFileFlavor(t.getTransferDataFlavors()))
            {
                String str = null;
                List<?> files = (List<?>) t.getTransferData(this.fileFlavor);

                for (int i = 0; i < files.size(); i++)
                {
                    File file = (File) files.get(i);

                    // Tell the tabbedpane controller to add
                    // a new tab with the name of this file
                    // on the tab. The text area that will
                    // display the contents of the file is returned.
                    tc = this.tpc.addTab(file.toString());

                    try (BufferedReader in = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))
                    {
                        while ((str = in.readLine()) != null)
                        {
                            tc.append(str + this.newline);
                        }
                    }
                    catch (IOException ioe)
                    {
                        System.out.println("importData: Unable to read from file " + file.toString());
                    }
                }

                return true;
            }
            else if (hasStringFlavor(t.getTransferDataFlavors()))
            {
                tc = (JTextArea) c;

                if (tc.equals(this.source) && (tc.getCaretPosition() >= this.p0.getOffset()) && (tc.getCaretPosition() <= this.p1.getOffset()))
                {
                    this.shouldRemove = false;

                    return true;
                }

                String str = (String) t.getTransferData(this.stringFlavor);
                tc.replaceSelection(str);

                return true;
            }
        }
        catch (UnsupportedFlavorException ufe)
        {
            System.out.println("importData: unsupported data flavor");
        }
        catch (IOException ieo)
        {
            System.out.println("importData: I/O exception");
        }

        return false;
    }
}
