package de.freese.sonstiges.dnd.color.textfield;

/*
 * ColorAndTextTransferHandler.java is used by the 1.4 DragColorDemo.java example.
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

/**
 * An implementation of TransferHandler that adds support for the import of dnd.color and the import
 * and export of text. Dropping a dnd.color on a component having this TransferHandler changes the
 * foreground of the component to the imported dnd.color.
 */
class ColorAndTextTransferHandler extends ColorTransferHandler
{
	// Start and end position in the source text.
	// We need this information when performing a MOVE
	// in order to remove the dragged text from the source.

	/**
	 *
	 */
	private static final long serialVersionUID = -2099117900708234471L;

	/**
	 * 
	 */
	Position p0 = null;

	// Start and end position in the source text.
	// We need this information when performing a MOVE
	// in order to remove the dragged text from the source.

	/**
	 *
	 */
	Position p1 = null;

	/**
	 * 
	 */
	private DataFlavor stringFlavor = DataFlavor.stringFlavor;

	/**
	 * 
	 */
	private JTextComponent source;

	/**
	 * 
	 */
	private boolean shouldRemove;

	/**
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(final JComponent c)
	{
		return COPY_OR_MOVE;
	}

	/**
	 * @see de.freese.sonstiges.dnd.color.textfield.ColorTransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean canImport(final JComponent c, final DataFlavor[] flavors)
	{
		if (hasStringFlavor(flavors))
		{
			return true;
		}

		return super.canImport(c, flavors);
	}

	// Get the flavors from the Transferable.
	// Is there a dnd.color flavor? If so, set the foreground dnd.color.
	// Is there a string flavor? If so, set the text property.
	/**
	 * @see de.freese.sonstiges.dnd.color.textfield.ColorTransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean importData(final JComponent c, final Transferable t)
	{
		JTextComponent tc = (JTextComponent) c;

		if (!canImport(c, t.getTransferDataFlavors()))
		{
			return false;
		}

		if (tc.equals(this.source) && (tc.getCaretPosition() >= this.p0.getOffset())
				&& (tc.getCaretPosition() <= this.p1.getOffset()))
		{
			this.shouldRemove = false;

			return true;
		}

		if (hasStringFlavor(t.getTransferDataFlavors()))
		{
			try
			{
				String str = (String) t.getTransferData(this.stringFlavor);
				tc.replaceSelection(str);

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

		// The ColorTransferHandler superclass handles dnd.color.
		return super.importData(c, t);
	}

	// Create a Transferable implementation that contains the
	// selected text.
	/**
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(final JComponent c)
	{
		this.source = (JTextComponent) c;

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
		catch (BadLocationException e)
		{
			System.out.println("Can't create position - unable to remove text from source.");
		}

		this.shouldRemove = true;

		String data = this.source.getSelectedText();

		return new StringSelection(data);
	}

	// Remove the old text if the action is a MOVE.
	// However, we do not allow dropping on top of the selected text,
	// so in that case do nothing.
	/**
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected void exportDone(final JComponent c, final Transferable data, final int action)
	{
		if (this.shouldRemove && (action == MOVE))
		{
			if ((this.p0 != null) && (this.p1 != null)
					&& (this.p0.getOffset() != this.p1.getOffset()))
			{
				try
				{
					JTextComponent tc = (JTextComponent) c;
					tc.getDocument().remove(this.p0.getOffset(),
							this.p1.getOffset() - this.p0.getOffset());
				}
				catch (BadLocationException e)
				{
					System.out.println("Can't remove text from source.");
				}
			}
		}

		this.source = null;
	}

	/**
	 * Does the flavor list have a string flavor?
	 * 
	 * @param flavors {@link DataFlavor}[]
	 * @return boolean
	 */
	protected boolean hasStringFlavor(final DataFlavor[] flavors)
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
}
