/*
 * Created on 31.08.2004
 */
package de.freese.sonstiges.dnd.tree;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.TooManyListenersException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import de.freese.base.swing.components.tree.ExtTree;

/**
 * Testklasse fuer DnD von JTree - JTree
 * 
 * @author Thomas Freese
 */
public class TreeDragDemo extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1197758766045157541L;

	/**
	 * 
	 */
	private JTree treeLeft = null;

	/**
	 * 
	 */
	private JTree treeRight = null;

	/**
	 * Creates a new {@link TreeDragDemo} object.
	 */
	public TreeDragDemo()
	{
		super("TreeDragDemo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		TreeTransferHandler transferHandler = new TreeTransferHandler();

		this.treeLeft = new ExtTree();
		this.treeLeft.setDragEnabled(true);
		this.treeLeft.setTransferHandler(transferHandler);

		try
		{
			this.treeLeft.getDropTarget().addDropTargetListener(new TreeDropTargetListener());
		}
		catch (TooManyListenersException ex)
		{
			ex.printStackTrace();
		}

		JScrollPane spLeft = new JScrollPane(this.treeLeft);
		spLeft.setPreferredSize(new Dimension(200, 400));

		this.treeRight = new ExtTree();
		this.treeRight.setDragEnabled(true);
		this.treeRight.setTransferHandler(transferHandler);

		JScrollPane spRight = new JScrollPane(this.treeRight);
		spRight.setPreferredSize(new Dimension(200, 400));

		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(spLeft);
		getContentPane().add(spRight);

		pack();
		setResizable(false);
		setVisible(true);

		setLocationRelativeTo(null);
	}

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		new TreeDragDemo();
	}
}
