package net.leddisplay;

import java.awt.Color;

import javax.swing.JComponent;

import net.led.elements.Element;

public interface LedDisplay
{
	public static final int CENTER = 0;

	public static final int EAST = 3;

	public static final int NORTH = 1;

	public static final int NORTHEAST = 6;

	public static final int NORTHWEST = 5;

	public static final int SOUTH = 4;

	public static final int SOUTHEAST = 8;

	public static final int SOUTHWEST = 7;

	public static final int WEST = 2;

	/**
	 * Gets the <code>JComponent</code> used to display the contents of the led display.
	 * 
	 * @return the component where the display is rendered
	 */
	public abstract JComponent getComponent();

	/**
	 * Sets the location of the element on the display when the display area is larger than the
	 * element.
	 * 
	 * @param anchor
	 */
	public abstract void setAnchor(int anchor);

	/**
	 * Sets the background color of the display.
	 * 
	 * @param color - the background color of the display
	 */
	public abstract void setBackgroundColor(Color color);

	/**
	 * Sets display's element.
	 * 
	 * @param element - the element to be displayed
	 */
	public abstract void setDisplayElement(Element element);

	/**
	 * Sets the gaps, in pixels, between two ticker leds. Default values are (1, 1).
	 * 
	 * @param hGap - the horizontal gap
	 * @param vGap - the vertical gap
	 */
	public abstract void setDotGaps(int hGap, int vGap);

	/**
	 * Sets the color of a turned-off led.
	 * 
	 * @param color - the color of a turned-off led
	 */
	public abstract void setDotOffColor(Color color);

	/**
	 * Sets the dimensions, in pixels, of the display's led Default values are (1, 1).
	 * 
	 * @param width - the width of the led
	 * @param height - the height of the led
	 */
	public abstract void setDotSize(int width, int height);

	/**
	 * Sets the number of dots separating the element from the edges of the component.
	 * 
	 * @param top - leading dots from top
	 * @param left - leading dots from left
	 * @param bottom - leading dots from bottom
	 * @param right - leading dots from right
	 */
	public abstract void setPadding(int top, int left, int button, int right);

	/**
	 * Sets the gap, in dots, between two tokens of an element. Default value is 2.
	 * 
	 * @param gap - the gap between the elements' tokens
	 */
	public abstract void setTokenGap(int gap);

	/**
	 * Signals the display that the element changed it's value and that it's led display should be
	 * updated.
	 */
	public abstract void update();
}
