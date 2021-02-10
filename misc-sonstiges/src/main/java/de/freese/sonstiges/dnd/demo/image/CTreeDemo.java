package de.freese.sonstiges.dnd.demo.image;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Demonstrates how to display a 'drag image' when using drag and drop on those platforms whose JVMs do not support it natively (eg Win32).
 */
public class CTreeDemo extends JTree implements DragSourceListener, DragGestureListener, Autoscroll, TreeModelListener
{
    // DropTargetListener interface object...
    /**
     * @author Thomas Freese
     */
    private class CDropTargetListener implements DropTargetListener
    {
        /**
         *
         */
        private final Color colorCueLine;

        /**
         * Cumulative left/right mouse movement
         */
        private int leftRight;

        /**
         *
         */
        private TreePath pathLast;

        /**
         *
         */
        private Point pointLast = new Point();

        /**
         *
         */
        private Rectangle2D rectangleCueLine = new Rectangle2D.Float();

        /**
         *
         */
        private Rectangle2D rectangleGhost = new Rectangle2D.Float();

        /**
         *
         */
        @SuppressWarnings("unused")
        private int shift;

        /**
         *
         */
        private Timer timerHover;

        /**
         * Erstellt ein neues {@link CDropTargetListener} Object.
         */
        public CDropTargetListener()
        {
            this.colorCueLine = new Color(SystemColor.controlShadow.getRed(), SystemColor.controlShadow.getGreen(), SystemColor.controlShadow.getBlue(), 64);

            // Set up a hover timer, so that a node will be automatically expanded or collapsed
            // if the user lingers on it for more than a short time
            this.timerHover = new Timer(1000, e -> {
                CDropTargetListener.this.leftRight = 0; // Reset left/right movement trend

                if (isRootPath(CDropTargetListener.this.pathLast))
                {
                    return; // Do nothing if we are hovering over the root node
                }

                if (isExpanded(CDropTargetListener.this.pathLast))
                {
                    collapsePath(CDropTargetListener.this.pathLast);
                }
                else
                {
                    expandPath(CDropTargetListener.this.pathLast);
                }
            });

            this.timerHover.setRepeats(true); // Set timer to one-shot mode
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragEnter(final DropTargetDragEvent dtde)
        {
            if (!isDragAcceptable(dtde))
            {
                dtde.rejectDrag();
            }
            else
            {
                dtde.acceptDrag(dtde.getDropAction());
            }
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
         */
        @Override
        public void dragExit(final DropTargetEvent dte)
        {
            if (!DragSource.isDragImageSupported())
            {
                repaint(this.rectangleGhost.getBounds());
            }
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragOver(final DropTargetDragEvent dtde)
        {
            // Even if the mouse is not moving, this method is still invoked 10 times per second
            Point pt = dtde.getLocation();

            if (pt.equals(this.pointLast))
            {
                return;
            }

            // Try to determine whether the user is flicking the cursor right or left
            int nDeltaLeftRight = pt.x - this.pointLast.x;

            if (((this.leftRight > 0) && (nDeltaLeftRight < 0)) || ((this.leftRight < 0) && (nDeltaLeftRight > 0)))
            {
                this.leftRight = 0;
            }

            this.leftRight += nDeltaLeftRight;

            this.pointLast = pt;

            Graphics2D g2 = (Graphics2D) getGraphics();

            // If a drag image is not supported by the platform, then draw my own drag image
            if (!DragSource.isDragImageSupported())
            {
                paintImmediately(this.rectangleGhost.getBounds()); // Rub out the last ghost image and cue
                // line

                // And remember where we are about to draw the new ghost image
                this.rectangleGhost.setRect(pt.x - CTreeDemo.this.pointOffset.x, pt.y - CTreeDemo.this.pointOffset.y, CTreeDemo.this.imageGhost.getWidth(),
                        CTreeDemo.this.imageGhost.getHeight());
                g2.drawImage(CTreeDemo.this.imageGhost, AffineTransform.getTranslateInstance(this.rectangleGhost.getX(), this.rectangleGhost.getY()), null);
            }
            else
            { // Just rub out the last cue line
                paintImmediately(this.rectangleCueLine.getBounds());
            }

            TreePath path = getClosestPathForLocation(pt.x, pt.y);

            if (!(path == this.pathLast))
            {
                this.leftRight = 0; // We've moved up or down, so reset left/right movement trend
                this.pathLast = path;
                this.timerHover.restart();
            }

            // In any case draw (over the ghost image if necessary) a cue line indicating where a
            // drop will occur
            Rectangle raPath = getPathBounds(path);
            this.rectangleCueLine.setRect(0, raPath.y + (int) raPath.getHeight(), getWidth(), 2);

            g2.setColor(this.colorCueLine);
            g2.fill(this.rectangleCueLine);

            // Now superimpose the left/right movement indicator if necessary
            if (this.leftRight > 20)
            {
                g2.drawImage(IMAGE_RIGHT, AffineTransform.getTranslateInstance(pt.x - CTreeDemo.this.pointOffset.x, pt.y - CTreeDemo.this.pointOffset.y), null);
                this.shift = +1;
            }
            else if (this.leftRight < -20)
            {
                g2.drawImage(IMAGE_LEFT, AffineTransform.getTranslateInstance(pt.x - CTreeDemo.this.pointOffset.x, pt.y - CTreeDemo.this.pointOffset.y), null);
                this.shift = -1;
            }
            else
            {
                this.shift = 0;
            }

            // And include the cue line in the area to be rubbed out next time
            this.rectangleGhost = this.rectangleGhost.createUnion(this.rectangleCueLine);

            /*
             * // Do this if you want to prohibit dropping onto the drag source if (path.equals(_pathSource)) e.rejectDrag(); else
             * e.acceptDrag(e.getDropAction());
             */
        }

        /**
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        @Override
        public void drop(final DropTargetDropEvent dtde)
        {
            this.timerHover.stop(); // Prevent hover timer from doing an unwanted expandPath or
            // collapsePath

            if (!isDropAcceptable(dtde))
            {
                dtde.rejectDrop();

                return;
            }

            dtde.acceptDrop(dtde.getDropAction());

            Transferable transferable = dtde.getTransferable();

            DataFlavor[] flavors = transferable.getTransferDataFlavors();

            for (DataFlavor flavor : flavors)
            {
                if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
                {
                    try
                    {
                        Point pt = dtde.getLocation();
                        // TreePath pathTarget = getClosestPathForLocation(pt.x, pt.y);
                        TreePath pathSource = (TreePath) transferable.getTransferData(flavor);

                        System.out.println("DROPPING: " + pathSource.getLastPathComponent());

                        DefaultTreeModel model = (DefaultTreeModel) getModel();
                        TreePath pathNewChild = null;

                        // .
                        // .. Add your code here to ask your TreeModel to copy the node and act on
                        // the mouse gestures...
                        // .
                        // For example:
                        // If pathTarget is an expanded BRANCH,
                        // then insert source UNDER it (before the first child if any)
                        // If pathTarget is a collapsed BRANCH (or a LEAF),
                        // then insert source AFTER it
                        // Note: a leaf node is always marked as collapsed
                        // You ask the model to do the copying...
                        // ...and you supply the copyNode method in the model as well of course.
                        // if (_nShift == 0)
                        // pathNewChild = model.copyNode(pathSource, pathTarget,
                        // isExpanded(pathTarget));
                        // else if (_nShift > 0) // The mouse is being flicked to the right (so move
                        // the node right)
                        // pathNewChild = model.copyNodeRight(pathSource, pathTarget);
                        // else // The mouse is being flicked to the left (so move the node left)
                        // pathNewChild = model.copyNodeLeft(pathSource);

                        TreePath parentpath = getClosestPathForLocation(pt.x, pt.y);
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
                        pathNewChild = (TreePath) transferable.getTransferData(flavor);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathNewChild.getLastPathComponent();
                        model.insertNodeInto(node, parent, parent.getChildCount());

                        // Loeschen des Sources in der Methode dragDropEnd

                        // if (pathNewChild != null)
                        {
                            setSelectionPath(pathNewChild); // Mark this as the selected path in the
                            // tree
                        }

                        break; // No need to check remaining flavors
                    }
                    catch (UnsupportedFlavorException ufe)
                    {
                        System.out.println(ufe);
                        dtde.dropComplete(false);

                        return;
                    }
                    catch (IOException ioe)
                    {
                        System.out.println(ioe);
                        dtde.dropComplete(false);

                        return;
                    }
                }
            }

            dtde.dropComplete(true);
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dropActionChanged(final DropTargetDragEvent dtde)
        {
            if (!isDragAcceptable(dtde))
            {
                dtde.rejectDrag();
            }
            else
            {
                dtde.acceptDrag(dtde.getDropAction());
            }
        }

        /**
         * @param dtde {@link DropTargetDragEvent}
         * @return boolean
         */
        public boolean isDragAcceptable(final DropTargetDragEvent dtde)
        {
            // Only accept COPY or MOVE gestures (ie LINK is not supported)
            if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
            {
                return false;
            }

            // Only accept this particular flavor
            if (!dtde.isDataFlavorSupported(CTransferableTreePath.FLAVOR_TREE_PATH))
            {
                return false;
            }

            /*
             * // Do this if you want to prohibit dropping onto the drag source... Point pt = e.getLocation(); TreePath path = getClosestPathForLocation(pt.x,
             * pt.y); if (path.equals(_pathSource)) return false;
             */
            /*
             * // Do this if you want to select the best flavor on offer... DataFlavor[] flavors = e.getCurrentDataFlavors(); for (int i = 0; i <
             * flavors.length; i++ ) { DataFlavor flavor = flavors[i]; if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) return true; }
             */
            return true;
        }

        /**
         * @param dtde {@link DropTargetDropEvent}
         * @return boolean
         */
        public boolean isDropAcceptable(final DropTargetDropEvent dtde)
        {
            // Only accept COPY or MOVE gestures (ie LINK is not supported)
            if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
            {
                return false;
            }

            // Only accept this particular flavor
            if (!dtde.isDataFlavorSupported(CTransferableTreePath.FLAVOR_TREE_PATH))
            {
                return false;
            }

            /*
             * // Do this if you want to prohibit dropping onto the drag source... Point pt = e.getLocation(); TreePath path = getClosestPathForLocation(pt.x,
             * pt.y); if (path.equals(_pathSource)) return false;
             */
            /*
             * // Do this if you want to select the best flavor on offer... DataFlavor[] flavors = e.getCurrentDataFlavors(); for (int i = 0; i <
             * flavors.length; i++ ) { DataFlavor flavor = flavors[i]; if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) return true; }
             */
            return true;
        }
    }

    /**
    *
    */
    private static final int AUTOSCROLL_MARGIN = 12;

    /**
    *
    */
    private static final BufferedImage IMAGE_LEFT = new CArrowImage(15, 15, CArrowImage.ARROW_LEFT);

    /**
        *
        */
    private static final BufferedImage IMAGE_RIGHT = new CArrowImage(15, 15, CArrowImage.ARROW_RIGHT);

    // Autoscroll Interface...
    // The following code was borrowed from the book:
    // Java Swing
    // By Robert Eckstein, Marc Loy & Dave Wood
    // Paperback - 1221 pages 1 Ed edition (September 1998)
    // O'Reilly & Associates; ISBN: 156592455X
    //
    // The relevant chapter of which can be found at:
    // http://www.oreilly.com/catalog/jswing/chapter/dnd.beta.pdf

    /**
     *
     */
    private static final long serialVersionUID = 10821500746764517L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            // Empty
        }

        CTreeDemo tree = new CTreeDemo();
        tree.setPreferredSize(new Dimension(300, 300));

        JScrollPane scrollPane = new JScrollPane(tree);

        JFrame frame = new JFrame("Drag Images");
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();

        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimFrame = frame.getSize();
        frame.setLocation((dimScreen.width - dimFrame.width) / 2, (dimScreen.height - dimFrame.height) / 2);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    /**
     * The 'drag image'
     */
    private BufferedImage imageGhost;

    /**
     * The path being dragged
     */
    private TreePath pathSource;

    /**
     * Where, in the drag image, the mouse was clicked
     */
    private Point pointOffset = new Point();

    /**
     * Erstellt ein neues {@link CTreeDemo} Object.<br>
     * Use the default JTree constructor so that we get a sample TreeModel built for us.
     */
    public CTreeDemo()
    {
        putClientProperty("JTree.lineStyle", "Angled");

        // Make this JTree a drag source
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        // Also, make this JTree a drag target
        DropTarget dropTarget = new DropTarget(this, new CDropTargetListener());
        dropTarget.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    /**
     * @see java.awt.dnd.Autoscroll#autoscroll(java.awt.Point)
     */
    @Override
    public void autoscroll(final Point pt)
    {
        // Figure out which row we're on.
        int nRow = getRowForLocation(pt.x, pt.y);

        // If we are not on a row then ignore this autoscroll request
        if (nRow < 0)
        {
            return;
        }

        Rectangle raOuter = getBounds();

        // Now decide if the row is at the top of the screen or at the
        // bottom. We do this to make the previous row (or the next
        // row) visible as appropriate. If we're at the absolute top or
        // bottom, just return the first or last row respectively.
        nRow = ((pt.y + raOuter.y) <= AUTOSCROLL_MARGIN) // Is row at top of screen?
                ? ((nRow <= 0) ? 0 : (nRow - 1)) // Yes, scroll up one row
                : ((nRow < (getRowCount() - 1)) ? (nRow + 1) : nRow); // No, scroll down one row

        scrollRowToVisible(nRow);
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
     */
    @Override
    public void dragDropEnd(final DragSourceDropEvent dsde)
    {
        if (dsde.getDropSuccess())
        {
            int nAction = dsde.getDropAction();

            if (nAction == DnDConstants.ACTION_MOVE)
            { // The dragged item (_pathSource) has been inserted at the target selected by the
              // user.
              // Now it is time to delete it from its original location.
                System.out.println("REMOVING: " + this.pathSource.getLastPathComponent());

                // .
                // .. ask your TreeModel to delete the node
                // .

                // Alten Knoten loeschen
                ((DefaultTreeModel) getModel()).removeNodeFromParent((MutableTreeNode) this.pathSource.getLastPathComponent());

                this.pathSource = null;
            }
        }
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
     */
    @Override
    public void dragEnter(final DragSourceDragEvent dsde)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
     */
    @Override
    public void dragExit(final DragSourceEvent dse)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
     */
    @Override
    public void dragGestureRecognized(final DragGestureEvent dge)
    {
        Point ptDragOrigin = dge.getDragOrigin();
        TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);

        if (path == null)
        {
            return;
        }

        if (isRootPath(path))
        {
            return; // Ignore user trying to drag the root node
        }

        // Work out the offset of the drag point from the TreePath bounding rectangle origin
        Rectangle raPath = getPathBounds(path);
        this.pointOffset.setLocation(ptDragOrigin.x - raPath.x, ptDragOrigin.y - raPath.y);

        // Get the cell renderer (which is a JLabel) for the path being dragged
        JLabel lbl = (JLabel) getCellRenderer().getTreeCellRendererComponent(this, // tree
                path.getLastPathComponent(), // value
                false, // isSelected (dont want a colored background)
                isExpanded(path), // isExpanded
                getModel().isLeaf(path.getLastPathComponent()), // isLeaf
                0, // row (not important for rendering)
                false // hasFocus (dont want a focus rectangle)
        );
        lbl.setSize((int) raPath.getWidth(), (int) raPath.getHeight()); // <-- The layout manager
        // would normally do this

        // Get a buffered image of the selection for dragging a ghost image
        this.imageGhost = new BufferedImage((int) raPath.getWidth(), (int) raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);

        Graphics2D g2 = this.imageGhost.createGraphics();

        // Ask the cell renderer to paint itself into the BufferedImage
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f)); // Make the image
        // ghostlike
        lbl.paint(g2);

        // Now paint a gradient UNDER the ghosted JLabel text (but not under the icon if any)
        // Note: this will need tweaking if your icon is not positioned to the left of the text
        Icon icon = lbl.getIcon();
        int nStartOfText = (icon == null) ? 0 : (icon.getIconWidth() + lbl.getIconTextGap());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f)); // Make the
        // gradient
        // ghostlike
        g2.setPaint(new GradientPaint(nStartOfText, 0, SystemColor.controlShadow, getWidth(), 0, new Color(255, 255, 255, 0)));
        g2.fillRect(nStartOfText, 0, getWidth(), this.imageGhost.getHeight());

        g2.dispose();

        setSelectionPath(path); // Select this path in the tree

        System.out.println("DRAGGING: " + path.getLastPathComponent());

        // Wrap the path being transferred into a Transferable object
        Transferable transferable = new CTransferableTreePath(path);

        // Remember the path being dragged (because if it is being moved, we will have to delete it
        // later)
        this.pathSource = path;

        // We pass our drag image just in case it IS supported by the platform
        dge.startDrag(null, this.imageGhost, new Point(5, 5), transferable, this);
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
     */
    @Override
    public void dragOver(final DragSourceDragEvent dsge)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
     */
    @Override
    public void dropActionChanged(final DragSourceDragEvent dsge)
    {
        // Empty
    }

    /**
     * @see java.awt.dnd.Autoscroll#getAutoscrollInsets()
     */
    @Override
    public Insets getAutoscrollInsets()
    {
        Rectangle raOuter = getBounds();
        Rectangle raInner = getParent().getBounds();

        return new Insets((raInner.y - raOuter.y) + AUTOSCROLL_MARGIN, (raInner.x - raOuter.x) + AUTOSCROLL_MARGIN,
                (raOuter.height - raInner.height - raInner.y) + raOuter.y + AUTOSCROLL_MARGIN,
                (raOuter.width - raInner.width - raInner.x) + raOuter.x + AUTOSCROLL_MARGIN);
    }

    /**
     * @param pathParent {@link TreePath}
     * @param childIndex int
     * @return {@link TreePath}
     */
    private TreePath getChildPath(final TreePath pathParent, final int childIndex)
    {
        TreeModel model = getModel();

        return pathParent.pathByAddingChild(model.getChild(pathParent.getLastPathComponent(), childIndex));
    }

    /**
     * @param path {@link TreePath}
     * @return boolean
     */
    private boolean isRootPath(final TreePath path)
    {
        return isRootVisible() && (getRowForPath(path) == 0);
    }

    /**
     * @param e {@link TreeModelEvent}
     */
    private void sayWhat(final TreeModelEvent e)
    {
        System.out.println(e.getTreePath().getLastPathComponent());

        int[] nIndex = e.getChildIndices();

        for (int i = 0; i < nIndex.length; i++)
        {
            System.out.println(i + ". " + nIndex[i]);
        }
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeNodesChanged(final TreeModelEvent e)
    {
        System.out.println("treeNodesChanged");
        sayWhat(e);

        // We dont need to reset the selection path, since it has not moved
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeNodesInserted(final TreeModelEvent e)
    {
        System.out.println("treeNodesInserted ");
        sayWhat(e);

        // We need to reset the selection path to the node just inserted
        int nChildIndex = e.getChildIndices()[0];
        TreePath pathParent = e.getTreePath();
        setSelectionPath(getChildPath(pathParent, nChildIndex));
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeNodesRemoved(final TreeModelEvent e)
    {
        System.out.println("treeNodesRemoved ");
        sayWhat(e);
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeStructureChanged(final TreeModelEvent e)
    {
        System.out.println("treeStructureChanged ");
        sayWhat(e);
    }
}
