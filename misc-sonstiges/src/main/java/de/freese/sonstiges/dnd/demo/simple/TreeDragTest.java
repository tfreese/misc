/*
 * Created on 30.08.2004 TODO To change the template for this generated file go to Window - Preferences - Java - Code Style - Code Templates
 */
package de.freese.sonstiges.dnd.demo.simple;

import java.awt.BorderLayout;
import java.awt.dnd.DnDConstants;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * @author Thomas Freese
 */
public class TreeDragTest extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = -1040887315433480625L;

    /**
     * @param args String[]
     */
    @SuppressWarnings("unused")
    public static void main(final String[] args)
    {
        new TreeDragTest();
    }

    /**
     *
     */
    TreeDragSource ds;

    /**
     *
     */
    TreeDropTarget dt;

    /**
     *
     */
    JTree tree;

    /**
     * Creates a new {@link TreeDragTest} object.
     */
    public TreeDragTest()
    {
        super("Rearrangeable Tree");

        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // If you want autoscrolling, use this line:
        // this.tree = new de.freese.base.swing.components.tree.ExtTree();
        this.tree = new JTree();

        // Otherwise, use this line:
        // tree = new JTree();
        getContentPane().add(new JScrollPane(this.tree), BorderLayout.CENTER);

        // If we only support move operations...
        // ds = new TreeDragSource(tree, DnDConstants.ACTION_MOVE);
        this.ds = new TreeDragSource(this.tree, DnDConstants.ACTION_COPY_OR_MOVE);
        this.dt = new TreeDropTarget(this.tree);
        setVisible(true);
    }
}
