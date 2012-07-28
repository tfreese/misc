package de.freese.sonstiges.bouncing_balls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class BallCanvas - a class to allow for simple graphical drawing on a canvas.
 * 
 * @author Thomas Freese
 */
public class BallCanvas
{
	/**
     * 
     */
	private JPanel canvas;

	/**
     *
     */
	private Color backgroundColour;

	/**
	 * instance variables - replace the example below with your own
	 */
	private JFrame display;

	/**
     * 
     */
	private Graphics2D graphic;

	/**
	 * Erstellt ein neues {@link BallCanvas} Object.
	 * 
	 * @param title String
	 */
	public BallCanvas(final String title)
	{
		this(title, 600, 400, Color.white);
	}

	/**
	 * Erstellt ein neues {@link BallCanvas} Object.
	 * 
	 * @param title String
	 * @param width int
	 * @param height int
	 */
	public BallCanvas(final String title, final int width, final int height)
	{
		this(title, width, height, Color.white);
	}

	/**
	 * Erstellt ein neues {@link BallCanvas} Object.
	 * 
	 * @param title String
	 * @param width int
	 * @param height int
	 * @param bgColour {@link Color}
	 */
	public BallCanvas(final String title, final int width, final int height, final Color bgColour)
	{
		super();

		this.display = new JFrame();
		this.canvas = (JPanel) this.display.getContentPane();
		this.display.setTitle(title);
		this.display.setSize(width, height);
		this.canvas.setBackground(bgColour);
		this.backgroundColour = bgColour;
	}

	/**
	 * @param shape {@link Shape}
	 */
	public void draw(final Shape shape)
	{
		this.graphic.draw(shape);
	}

	/**
	 * draws an image onto the canvas
	 * 
	 * @param image the Image object to be displayed
	 * @param x x co-ordinate for Image placement
	 * @param y y co-ordinate for Image placement
	 * @return returns boolean value representing whether the image was completely loaded
	 */
	public boolean drawImage(final Image image, final int x, final int y)
	{
		return this.graphic.drawImage(image, x, y, null);
	}

	/**
	 * draws a straight line on the BallCanvas
	 * 
	 * @param x1 x co-ordinate of start of line
	 * @param y1 y co-ordinate of start of line
	 * @param x2 x co-ordinate of start of line
	 * @param y2 y co-ordinate of start of line
	 */
	public void drawLine(final int x1, final int y1, final int x2, final int y2)
	{
		this.graphic.drawLine(x1, y1, x2, y2);
	}

	/**
	 * draws a String on the BallCanvas
	 * 
	 * @param text the String to be displayed
	 * @param x x co-ordinate for text placement
	 * @param y y co-ordinate for text placement
	 */
	public void drawString(final String text, final int x, final int y)
	{
		this.graphic.drawString(text, x, y);
	}

	/**
	 * erases a given shape on the screen
	 * 
	 * @param shape the shape object to be erased
	 */
	public void erase(final Shape shape)
	{
		// keep track of the current colour
		Color original = this.graphic.getColor();

		// set colour to that of the canvas background
		this.graphic.setColor(this.backgroundColour);

		// draw and fill shape again using background colour
		// graphic.draw(shape);
		this.graphic.fill(shape);

		// revert graphic object back to original foreground colour
		this.graphic.setColor(original);
	}

	/**
	 * erases a String on the BallCanvas by rewriting the same String using the background colour as
	 * the colour of the String.
	 * 
	 * @param text the String to be displayed
	 * @param x x co-ordinate for text placement
	 * @param y y co-ordinate for text placement
	 */
	public void eraseString(final String text, final int x, final int y)
	{
		Color original = this.graphic.getColor();

		this.graphic.setColor(this.backgroundColour);
		this.graphic.drawString(text, x, y);
		this.graphic.setColor(original);
	}

	/**
	 * fills the internal dimensions of a given shape with the current foreground colour of the
	 * canvas.
	 * 
	 * @param shape the shape object to be filled
	 */
	public void fill(final Shape shape)
	{
		this.graphic.fill(shape);
	}

	/**
	 * returns the current colour of the background
	 * 
	 * @return the colour of the background of the BallCanvas
	 */
	public Color getBackgroundColour()
	{
		return this.backgroundColour;
	}

	/**
	 * An example of a method - replace this comment with your own
	 * 
	 * @return the sum of x and y
	 */
	public Font getFont()
	{
		return this.graphic.getFont();
	}

	/**
	 * returns the current colour of the foreground
	 * 
	 * @return the colour of the foreground of the BallCanvas
	 */
	public Color getForegroundColour()
	{
		return this.graphic.getColor();
	}

	/**
	 * provides information on visibility of the BallCanvas
	 * 
	 * @return boolean value representing the visibility of the canvas (true or false)
	 */
	public boolean isVisible()
	{
		return this.display.isVisible();
	}

	/**
	 * sets the background colour of the BallCanvas
	 * 
	 * @param newColour the new colour for the background of the BallCanvas
	 */
	public void setBackgroundColour(final Color newColour)
	{
		this.backgroundColour = newColour;
		this.graphic.setBackground(newColour);
	}

	/**
	 * changes the current Font used on the BallCanvas
	 * 
	 * @param newFont a sample parameter for a method
	 */
	public void setFont(final Font newFont)
	{
		this.graphic.setFont(newFont);
	}

	/**
	 * sets the foreground colour of the BallCanvas
	 * 
	 * @param newColour the new colour for the foreground of the BallCanvas
	 */
	public void setForegroundColour(final Color newColour)
	{
		this.graphic.setColor(newColour);
	}

	/**
	 * sets the size of the canvas display
	 * 
	 * @param width new width
	 * @param height new height
	 */
	public void setSize(final int width, final int height)
	{
		this.display.setSize(width, height);
	}

	/**
	 * Sets the canvas visibility and brings canvas to the front of screen when made visible. This
	 * method can also be used to bring an already visible canvas to the front of other windows.
	 * 
	 * @param visible boolean value representing the desired visibility of the canvas (true or
	 *            false)
	 */
	public void setVisible(final boolean visible)
	{
		this.display.setVisible(visible);

		if (this.graphic == null)
		{
			this.graphic = (Graphics2D) this.canvas.getGraphics();
		}
	}

	/**
	 * waits for a specified number of milliseconds before finishing. This provides an easy way to
	 * specify a small delay which can be used when producing animations.
	 * 
	 * @param milliseconds the number
	 */
	public void wait(final int milliseconds)
	{
		try
		{
			Thread.sleep(milliseconds);
		}
		catch (Exception e)
		{
			// ignoring exception at the moment
		}
	}
}
