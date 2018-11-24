package de.freese.sonstiges.dnd.list;

/*
 * ArrayListTransferHandler.java is used by the 1.4 DragListDemo.java example.
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * @author Thomas Freese
 */
public class ArrayListTransferHandler extends TransferHandler
{
    /**
     * @author Thomas Freese
     */
    public class ArrayListTransferable implements Transferable
    {
        /**
         *
         */
        private List<?> data = null;

        /**
         * Creates a new ArrayListTransferable object.
         * 
         * @param alist {@link List}
         */
        public ArrayListTransferable(final List<?> alist)
        {
            super();

            this.data = alist;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException
        {
            if (!isDataFlavorSupported(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }

            return this.data;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[]
            {
                    ArrayListTransferHandler.this.localArrayListFlavor, ArrayListTransferHandler.this.serialArrayListFlavor
            };
        }

        /**
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor)
        {
            if (ArrayListTransferHandler.this.localArrayListFlavor.equals(flavor))
            {
                return true;
            }

            if (ArrayListTransferHandler.this.serialArrayListFlavor.equals(flavor))
            {
                return true;
            }

            return false;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -5057942587610930744L;

    /**
     *
     */
    private int addCount = 0; // Number of items added

    /**
     *
     */
    private int addIndex = -1; // Location where items were added

    /**
     *
     */
    private int[] indices = null;

    /**
     *
     */
    private DataFlavor localArrayListFlavor = null;

    /**
     *
     */
    private String localArrayListType = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.ArrayList";

    /**
     *
     */
    private DataFlavor serialArrayListFlavor = null;

    /**
     *
     */
    private JList<?> source = null;

    /**
     * Creates a new ArrayListTransferHandler object.
     * 
     * @throws ClassNotFoundException Falls was schief geht.
     */
    public ArrayListTransferHandler() throws ClassNotFoundException
    {
        super();

        this.localArrayListFlavor = new DataFlavor(this.localArrayListType);
        this.serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
    }

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(final JComponent c, final DataFlavor[] flavors)
    {
        if (hasLocalArrayListFlavor(flavors))
        {
            return true;
        }

        if (hasSerialArrayListFlavor(flavors))
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
        if (c instanceof JList)
        {
            this.source = (JList<?>) c;
            this.indices = this.source.getSelectedIndices();

            List<?> values = this.source.getSelectedValuesList();

            if ((values == null) || (values.size() == 0))
            {
                return null;
            }

            List<String> alist = new ArrayList<>(values.size());

            for (Object o : values)
            {
                String str = o.toString();

                if (str == null)
                {
                    str = "";
                }

                alist.add(str);
            }

            return new ArrayListTransferable(alist);
        }

        return null;
    }

    /**
     * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action)
    {
        if ((action == MOVE) && (this.indices != null))
        {
            DefaultListModel<?> model = (DefaultListModel<?>) this.source.getModel();

            // If we are moving items around in the same list, we
            // need to adjust the indices accordingly since those
            // after the insertion point have moved.
            if (this.addCount > 0)
            {
                for (int i = 0; i < this.indices.length; i++)
                {
                    if (this.indices[i] > this.addIndex)
                    {
                        this.indices[i] += this.addCount;
                    }
                }
            }

            for (int i = this.indices.length - 1; i >= 0; i--)
            {
                model.remove(this.indices[i]);
            }
        }

        this.indices = null;
        this.addIndex = -1;
        this.addCount = 0;
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
     * @param flavors {@link DataFlavor}[]
     * @return boolean
     */
    private boolean hasLocalArrayListFlavor(final DataFlavor[] flavors)
    {
        if (this.localArrayListFlavor == null)
        {
            return false;
        }

        for (DataFlavor flavor : flavors)
        {
            if (flavor.equals(this.localArrayListFlavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param flavors {@link DataFlavor}[]
     * @return boolean
     */
    private boolean hasSerialArrayListFlavor(final DataFlavor[] flavors)
    {
        if (this.serialArrayListFlavor == null)
        {
            return false;
        }

        for (DataFlavor flavor : flavors)
        {
            if (flavor.equals(this.serialArrayListFlavor))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @SuppressWarnings(
    {
            "rawtypes", "unchecked"
    })
    @Override
    public boolean importData(final JComponent c, final Transferable t)
    {
        if (!canImport(c, t.getTransferDataFlavors()))
        {
            return false;
        }

        JList<?> target = null;
        List alist = null;

        try
        {
            target = (JList<?>) c;

            if (hasLocalArrayListFlavor(t.getTransferDataFlavors()))
            {
                alist = (List<?>) t.getTransferData(this.localArrayListFlavor);
            }
            else if (hasSerialArrayListFlavor(t.getTransferDataFlavors()))
            {
                alist = (List<?>) t.getTransferData(this.serialArrayListFlavor);
            }
            else
            {
                return false;
            }
        }
        catch (UnsupportedFlavorException ufe)
        {
            System.out.println("importData: unsupported data flavor");

            return false;
        }
        catch (IOException ioe)
        {
            System.out.println("importData: I/O exception");

            return false;
        }

        // At this point we use the same code to retrieve the data
        // locally or serially.
        // We'll drop at the current selected index.
        int index = target.getSelectedIndex();

        // Prevent the user from dropping data back on itself.
        // For example, if the user is moving items #4,#5,#6 and #7 and
        // attempts to insert the items after item #5, this would
        // be problematic when removing the original items.
        // This is interpreted as dropping the same data on itself
        // and has no effect.
        if (this.source.equals(target))
        {
            if ((this.indices != null) && (index >= (this.indices[0] - 1)) && (index <= this.indices[this.indices.length - 1]))
            {
                this.indices = null;

                return true;
            }
        }

        DefaultListModel listModel = (DefaultListModel) target.getModel();
        int max = listModel.getSize();

        if (index < 0)
        {
            index = max;
        }
        else
        {
            index++;

            if (index > max)
            {
                index = max;
            }
        }

        this.addIndex = index;
        this.addCount = alist.size();

        for (int i = 0; i < alist.size(); i++)
        {
            listModel.add(index++, alist.get(i));
        }

        return true;
    }
}
