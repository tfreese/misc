package net.leddisplay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPanel;

import net.led.elements.Element;

public class LedPanel extends JPanel
{
	private static final long serialVersionUID = 3000L;

	private Dimension preferredSize;

	private Matrix matrix;

	private Element displayElement;

	private int height;

	public LedPanel(final Matrix b1)
	{
		this.matrix = b1;
		setBackground(null);
		setLayout(null);
		setDoubleBuffered(true);
	}

	public Dimension getPreferredSize()
	{
		Dimension dimension = super.getPreferredSize();

		if (this.preferredSize == null)
		{
			Insets insets = getInsets();
			dimension =
					new Dimension(dimension.width + 399, this.height + insets.top + insets.bottom);
		}

		return dimension;
	}

	public void paintComponent(final Graphics g1)
	{
		super.paintComponent(g1);
		this.matrix.b(g1, getWidth(), getHeight());

		if (this.displayElement == null)
		{
			return;
		}
		else
		{
			this.matrix.b(g1, this.displayElement, getWidth(), getHeight());

			return;
		}
	}

	public void setDisplayElement(final Element newValue)
	{
		this.displayElement = newValue;
		repaint();
	}

	public void setHeight(final int newValue)
	{
		this.height = newValue;
	}

	public void setPreferredSize(final Dimension dimension)
	{
		this.preferredSize = dimension;
		super.setPreferredSize(dimension);
	}
}
