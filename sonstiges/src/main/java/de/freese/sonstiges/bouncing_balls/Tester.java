package de.freese.sonstiges.bouncing_balls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

/**
 * Class Tester - provides some methods that demonstrate some of the capabilities of the BallCanvas
 * class.
 * 
 * @author Thomas Freese
 */
public class Tester
{
	/**
	 * canvas object
	 */
	private BallCanvas myCanvas;

	/**
	 * Erstellt ein neues {@link Tester} Object.
	 */
	public Tester()
	{
		super();

		// initialise instance variables
		this.myCanvas = new BallCanvas("Testing BallCanvas Class", 600, 500);
		this.myCanvas.setVisible(true);
	}

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		Tester t = new Tester();

		// t.testSquare(10);
		t.bounce();
	}

	/**
	 * simulates a bouncing ball
	 */
	public void bounce()
	{
		int ground = 400; // position of the ground line

		this.myCanvas.setVisible(true);

		// draw the ground
		this.myCanvas.drawLine(50, ground, 550, ground);

		// draw the balls
		BouncingBall ball1 = new BouncingBall(20, 40, 15, Color.blue);

		ball1.draw(this.myCanvas);

		BouncingBall ball2 = new BouncingBall(50, 80, 20, Color.red);

		ball2.draw(this.myCanvas);

		BouncingBall ball3 = new BouncingBall(80, 120, 25, Color.magenta);

		ball3.draw(this.myCanvas);

		boolean finished = false;

		while (!finished)
		{
			this.myCanvas.wait(50); // small delay
			ball1.move(this.myCanvas);
			ball2.move(this.myCanvas);
			ball3.move(this.myCanvas);

			// stop once ball has travelled a certain distance on x axis
			if ((ball1.getXPosition() == 550) || (ball2.getXPosition() == 550)
					|| (ball3.getXPosition() == 550))
			{
				finished = true;
			}
		}

		ball1.erase(this.myCanvas);
		ball2.erase(this.myCanvas);
		ball3.erase(this.myCanvas);

		System.exit(0);
	}

	/**
	 * test method for testing BallCanvas class Moves a Rectangle object around the screen in a
	 * rectangular direction. It changes colour each time.
	 * 
	 * @param iterations the number of cycles to perform
	 */
	public void testSquare(final int iterations)
	{
		// demo parameters
		int x = 30;
		int y = 20;
		int xTravel = 120;
		int yTravel = 100;

		int stringX = 20;
		int stringY = 30 + y + yTravel;

		// colours to rotate between
		Color[] rainbow =
		{
				Color.red, Color.gray, Color.blue, Color.green, Color.black
		};
		String[] colourNames =
		{
				"red", "gray", "blue", "green", "black"
		};

		// the shape to draw and move
		Rectangle rect = new Rectangle(x, y, 10, 10);
		int distance = (xTravel * 2) + (yTravel * 2);

		this.myCanvas.setFont(new Font("helvetica", Font.BOLD, 14));

		for (int count = 0; count < iterations; count++)
		{
			this.myCanvas.setForegroundColour(rainbow[count % rainbow.length]);
			this.myCanvas.drawString(colourNames[count % rainbow.length], stringX, stringY);

			for (int i = 0; i < distance; i++)
			{
				this.myCanvas.fill(rect);
				this.myCanvas.wait(10);

				// travel east
				if (i < xTravel)
				{
					this.myCanvas.erase(rect);
					rect.setLocation(x++, y);
				}

				// travel south
				else if (i < (xTravel + yTravel))
				{
					this.myCanvas.erase(rect);
					rect.setLocation(x, y++);
				}

				// travel west
				else if (i < ((xTravel * 2) + yTravel))
				{
					this.myCanvas.erase(rect);
					rect.setLocation(x--, y);
				}

				// travel north
				else if (i < distance)
				{
					this.myCanvas.erase(rect);
					rect.setLocation(x, y--);
				}
			}

			this.myCanvas.eraseString(colourNames[count % rainbow.length], stringX, stringY);
		}
	}
}
