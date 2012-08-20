package segment;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class Segment7 extends Canvas
{
	/**
	 *
	 */
	private static final long serialVersionUID = -127396317020967058L;

	private int digit;

	private int[][] digits =
	{
			{
					1, 1, 1, 1, 1, 1, 0
			}, // Ziffer 0
			{
					0, 1, 1, 0, 0, 0, 0
			}, // Ziffer 1
			{
					1, 1, 0, 1, 1, 0, 1
			}, // Ziffer 2
			{
					1, 1, 1, 1, 0, 0, 1
			}, // Ziffer 3
			{
					0, 1, 1, 0, 0, 1, 1
			}, // Ziffer 4
			{
					1, 0, 1, 1, 0, 1, 1
			}, // Ziffer 5
			{
					1, 0, 1, 1, 1, 1, 1
			}, // Ziffer 6
			{
					1, 1, 1, 0, 0, 0, 0
			}, // Ziffer 7
			{
					1, 1, 1, 1, 1, 1, 1
			}, // Ziffer 8
			{
					1, 1, 1, 1, 0, 1, 1
			}
	// Ziffer 9
			};

	private boolean hasfocus;

	private int[][] polysx =
	{
			{
					1, 2, 8, 9, 8, 2
			}, // Segment 0
			{
					9, 10, 10, 9, 8, 8
			}, // Segment 1
			{
					9, 10, 10, 9, 8, 8
			}, // Segment 2
			{
					1, 2, 8, 9, 8, 2
			}, // Segment 3
			{
					1, 2, 2, 1, 0, 0
			}, // Segment 4
			{
					1, 2, 2, 1, 0, 0
			}, // Segment 5
			{
					1, 2, 8, 9, 8, 2
			}, // Segment 6
	};

	private int[][] polysy =
	{
			{
					1, 0, 0, 1, 2, 2
			}, // Segment 0
			{
					1, 2, 8, 9, 8, 2
			}, // Segment 1
			{
					9, 10, 16, 17, 16, 10
			}, // Segment 2
			{
					17, 16, 16, 17, 18, 18
			}, // Segment 3
			{
					9, 10, 16, 17, 16, 10
			}, // Segment 4
			{
					1, 2, 8, 9, 8, 2
			}, // Segment 5
			{
					9, 8, 8, 9, 10, 10
			}, // Segment 6
	};

	public static void main(final String[] args)
	{
		Segment7 seg = new Segment7();

		JFrame frame = new JFrame();
		frame.getContentPane().add(seg);

		frame.pack();
		frame.setVisible(true);
	}

	public Segment7()
	{
		this(0);
	}

	public Segment7(final int digit)
	{
		super();
		this.digit = digit;
		this.hasfocus = false;
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
		enableEvents(AWTEvent.FOCUS_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		enableEvents(AWTEvent.KEY_EVENT_MASK);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(1 * 10, 1 * 18);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(5 * 10, 5 * 18);
	}

	public int getValue()
	{
		return this.digit;
	}

	@Override
	public boolean isFocusTraversable()
	{
		return true;
	}

	@Override
	public void paint(final Graphics g)
	{
		Color darkred = new Color(127, 0, 0);
		Color lightred = new Color(255, 0, 0);
		Color yellow = new Color(255, 255, 0);

		// dx und dy berechnen
		int dx = getSize().width / 10;
		int dy = getSize().height / 18;
		// Hintergrund
		g.setColor(darkred);
		g.fillRect(0, 0, getSize().width, getSize().height);

		// Segmente
		if (this.hasfocus)
		{
			g.setColor(yellow);
		}
		else
		{
			g.setColor(lightred);
		}

		for (int i = 0; i < 7; ++i)
		{ // alle Segmente

			if (this.digits[this.digit][i] == 1)
			{
				Polygon poly = new Polygon();

				for (int j = 0; j < 6; ++j)
				{ // alle Eckpunkte
					poly.addPoint(dx * this.polysx[i][j], dy * this.polysy[i][j]);
				}

				g.fillPolygon(poly);
			}
		}

		// Trennlinien
		g.setColor(darkred);
		g.drawLine(0, 0, dx * 10, dy * 10);
		g.drawLine(0, 8 * dy, 10 * dx, 18 * dy);
		g.drawLine(0, 10 * dy, 10 * dx, 0);
		g.drawLine(0, 18 * dy, 10 * dx, 8 * dy);
	}

	@Override
	protected void processComponentEvent(final ComponentEvent event)
	{
		if (event.getID() == ComponentEvent.COMPONENT_SHOWN)
		{
			requestFocus();
		}

		super.processComponentEvent(event);
	}

	@Override
	protected void processFocusEvent(final FocusEvent event)
	{
		if (event.getID() == FocusEvent.FOCUS_GAINED)
		{
			this.hasfocus = true;
			repaint();
		}
		else if (event.getID() == FocusEvent.FOCUS_LOST)
		{
			this.hasfocus = false;
			repaint();
		}

		super.processFocusEvent(event);
	}

	@Override
	protected void processKeyEvent(final KeyEvent event)
	{
		if (event.getID() == KeyEvent.KEY_PRESSED)
		{
			char key = event.getKeyChar();

			if ((key >= '0') && (key <= '9'))
			{
				setValue(key - '0');
				repaint();
			}
			else if (key == '+')
			{
				setValue(getValue() + 1); // increment by 1
				repaint();
			}
			else if (key == '-')
			{
				setValue(getValue() + 9); // decrement by 1
				repaint();
			}
		}

		super.processKeyEvent(event);
	}

	@Override
	protected void processMouseEvent(final MouseEvent event)
	{
		if (event.getID() == MouseEvent.MOUSE_PRESSED)
		{
			requestFocus();

			if (!event.isShiftDown())
			{
				if (event.isMetaDown())
				{
					setValue(getValue() + 1); // increment by 1
				}
				else
				{
					setValue(getValue() + 9); // decrement by 1
				}
			}

			repaint();
		}

		super.processMouseEvent(event);
	}

	public void setValue(final int value)
	{
		this.digit = value % 10;
	}
}