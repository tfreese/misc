package net.leddisplay;

import java.awt.Color;

import javax.swing.JComponent;

import net.led.elements.Element;

public class DefaultLedDisplay implements LedDisplay
{
	private LedPanel ledPanel;

	private Matrix matrix;

	public DefaultLedDisplay()
	{
		this.matrix = new Matrix();
		this.ledPanel = new LedPanel(this.matrix);
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#getComponent()
	 */
	public JComponent getComponent()
	{
		return this.ledPanel;
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setAnchor(int)
	 */
	public void setAnchor(final int i)
	{
		this.matrix.setAnchor((i >= 9) ? (i % 9) : i);
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setBackgroundColor(java.awt.Color)
	 */
	public void setBackgroundColor(final Color color)
	{
		this.matrix.setBackgroundColor(color);
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setDisplayElement(net.leddisplay.elements.DisplayElement)
	 */
	public void setDisplayElement(final Element element)
	{
		this.ledPanel.setDisplayElement(element);
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setDotGaps(int, int)
	 */
	public void setDotGaps(final int i, final int j)
	{
		this.matrix.setDotGaps(i, j);
		this.ledPanel.setHeight(this.matrix.getHeigth());
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setDotOffColor(java.awt.Color)
	 */
	public void setDotOffColor(final Color color)
	{
		this.matrix.setDotOffColor(color);
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setDotSize(int, int)
	 */
	public void setDotSize(final int width, final int height)
	{
		this.matrix.setDotSize(width, height);
		this.ledPanel.setHeight(this.matrix.getHeigth());
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setPadding(int, int, int, int)
	 */
	public void setPadding(final int top, final int left, final int bottom, final int right)
	{
		this.matrix.setPadding(top, left, bottom, right);
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#setTokenGap(int)
	 */
	public void setTokenGap(final int gap)
	{
		this.matrix.setTokenGap(gap);
		update();
	}

	/*
	 * (non-Javadoc)
	 * @see net.leddisplay.LedDisplay#update()
	 */
	public void update()
	{
		this.ledPanel.repaint();
	}
}
