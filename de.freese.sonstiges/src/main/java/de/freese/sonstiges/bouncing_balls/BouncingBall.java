package de.freese.sonstiges.bouncing_balls;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

/**
 * Class BouncingBall - a graphical ball that observes the effect of gravity.
 * 
 * @author Thomas Freese
 */
public class BouncingBall
{
	/**
	 * effect of gravity
	 */
	static final int gravity = 3;

	/**
	 * y position of ground
	 */
	static final int ground = 400;

	/**
     * 
     */
	private int xPosition;

	/**
     * 
     */
	private int yPosition;

	/**
	 * downward speed
	 */
	private int ySpeed = 1;

	/**
     * 
     */
	private int ballDegradation = 2;

	/**
     * 
     */
	private Ellipse2D.Double circle;

	/**
     * 
     */
	private Color colour;

	/**
     * 
     */
	private int diameter;

	/**
	 * Erstellt ein neues {@link BouncingBall} Object.
	 * 
	 * @param xPos int
	 * @param yPos int
	 * @param ballDiameter int
	 * @param ballColour {@link Color}
	 */
	public BouncingBall(final int xPos, final int yPos, final int ballDiameter,
			final Color ballColour)
	{
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.circle = new Ellipse2D.Double(xPos, yPos, ballDiameter, ballDiameter);
		this.colour = ballColour;
		this.diameter = (int) this.circle.getHeight();
	}

	/**
	 * Draw this ball at its current position onto the canvas.
	 * 
	 * @param canvas {@link BallCanvas}
	 */
	public void draw(final BallCanvas canvas)
	{
		canvas.setForegroundColour(this.colour);
		canvas.fill(this.circle);
	}

	/**
	 * Erase this ball at its current position.
	 * 
	 * @param canvas {@link BallCanvas}
	 */
	public void erase(final BallCanvas canvas)
	{
		canvas.erase(this.circle);
	}

	/**
	 * return the horizontal position of this ball
	 * 
	 * @return int
	 */
	public int getXPosition()
	{
		return this.xPosition;
	}

	/**
	 * return the vertical position of this ball
	 * 
	 * @return int
	 */
	public int getYPosition()
	{
		return this.yPosition;
	}

	/**
	 * Move this ball according to its position and speed and redraw.
	 * 
	 * @param canvas {@link BallCanvas}
	 */
	public void move(final BallCanvas canvas)
	{
		// remove from canvas at the current position
		canvas.erase(this.circle);

		// compute new position
		this.ySpeed += gravity;
		this.yPosition += this.ySpeed;
		this.xPosition += 2;

		// check if it has hit the ground
		if ((this.yPosition >= (ground - this.diameter)) && (this.ySpeed > 0))
		{
			this.yPosition = (ground - this.diameter);
			this.ySpeed = -this.ySpeed + this.ballDegradation;
		}

		this.circle.setFrame(this.xPosition, this.yPosition, this.diameter, this.diameter);

		// draw again at new position
		canvas.setForegroundColour(this.colour);
		canvas.fill(this.circle);
	}
}
