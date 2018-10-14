package sudoku5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application class. Shows a frame containing the Soduku Applet
 */
public class SudokuApplic extends Frame
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4999414040070593165L;

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		new SudokuApplic().setVisible(true);
	}

	/**
	 * Erstellt ein neues {@link SudokuApplic} Object.
	 */
	SudokuApplic()
	{
		int w = 400;
		int h = 440;

		this.setSize(w, h);
		this.setTitle("Sudoku");
		this.setBackground(Color.lightGray);

		this.add(new Sudoku(true, w, h), BorderLayout.CENTER);

		this.addWindowListener(new WindowClose());
	}
}

/**
 *
 */
class WindowClose extends WindowAdapter
{
	/**
	 * Erstellt ein neues {@link WindowClose} Object.
	 */
	public WindowClose()
	{
	}

	/**
	 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(final WindowEvent e)
	{
		System.exit(0);
	}
}