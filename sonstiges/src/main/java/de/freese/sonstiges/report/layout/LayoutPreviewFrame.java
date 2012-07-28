/**
 * 15.04.2008
 */
package de.freese.sonstiges.report.layout;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * Uebersicht des Layouts.
 * 
 * @author Thomas Freese
 */
public class LayoutPreviewFrame extends JFrame
{
	/**
     * 
     */
	private static final long serialVersionUID = -2245301418603208848L;

	/**
	 * Creates a new {@link LayoutPreviewFrame} object.
	 * 
	 * @param layoutElement {@link AbstractLayoutElement}
	 * @throws HeadlessException Falls was schief geht.
	 */
	public LayoutPreviewFrame(final AbstractLayoutElement layoutElement) throws HeadlessException
	{
		super();

		Image designImage = layoutElement.createImage();

		// Darstellung
		ImageIcon imageIcon = new ImageIcon(designImage);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setResizable(false);

		JLabel label = new JLabel();
		label.setIcon(imageIcon);

		frame.getContentPane().add(label);
		frame.pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		int w = frame.getSize().width;
		int h = frame.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		frame.setLocation(x, y);

		frame.setVisible(true);
	}
}
