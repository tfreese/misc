package net.leddisplay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import net.led.elements.ArrowToken;
import net.led.elements.Element;
import net.led.elements.Token;

public class Matrix
{
	protected static final Map map;

	static
	{
		map = new HashMap();
		map.put(" ", new byte[]
		{
				0, 0, 0, 0, 0
		});
		map.put("A", new byte[]
		{
				126, 9, 9, 9, 126
		});
		map.put("a", new byte[]
		{
				32, 84, 84, 84, 120
		});
		map.put("B", new byte[]
		{
				127, 73, 73, 73, 62
		});
		map.put("b", new byte[]
		{
				127, 68, 68, 68, 56
		});
		map.put("C", new byte[]
		{
				62, 65, 65, 65, 34
		});
		map.put("c", new byte[]
		{
				56, 68, 68, 68, 0
		});
		map.put("D", new byte[]
		{
				65, 127, 65, 65, 62
		});
		map.put("d", new byte[]
		{
				56, 68, 68, 72, 127
		});
		map.put("E", new byte[]
		{
				127, 73, 73, 65, 65
		});
		map.put("e", new byte[]
		{
				56, 84, 84, 84, 24
		});
		map.put("F", new byte[]
		{
				127, 9, 9, 1, 1
		});
		map.put("f", new byte[]
		{
				8, 126, 9, 1, 2
		});
		map.put("G", new byte[]
		{
				62, 65, 65, 73, 58
		});
		map.put("g", new byte[]
		{
				72, 84, 84, 84, 60
		});
		map.put("H", new byte[]
		{
				127, 8, 8, 8, 127
		});
		map.put("h", new byte[]
		{
				127, 8, 4, 4, 120
		});
		map.put("I", new byte[]
		{
				0, 65, 127, 65, 0
		});
		map.put("i", new byte[]
		{
				0, 68, 125, 64, 0
		});
		map.put("J", new byte[]
		{
				32, 64, 65, 63, 1
		});
		map.put("j", new byte[]
		{
				32, 64, 68, 61, 0
		});
		map.put("K", new byte[]
		{
				127, 8, 20, 34, 65
		});
		map.put("k", new byte[]
		{
				127, 16, 40, 68, 0
		});
		map.put("L", new byte[]
		{
				127, 64, 64, 64, 64
		});
		map.put("l", new byte[]
		{
				0, 65, 127, 64, 0
		});
		map.put("M", new byte[]
		{
				127, 2, 12, 2, 127
		});
		map.put("m", new byte[]
		{
				124, 4, 24, 4, 120
		});
		map.put("N", new byte[]
		{
				127, 4, 8, 16, 127
		});
		map.put("n", new byte[]
		{
				124, 8, 4, 4, 120
		});
		map.put("O", new byte[]
		{
				62, 65, 65, 65, 62
		});
		map.put("o", new byte[]
		{
				56, 68, 68, 68, 56
		});
		map.put("P", new byte[]
		{
				127, 9, 9, 9, 6
		});
		map.put("p", new byte[]
		{
				124, 20, 20, 20, 8
		});
		map.put("Q", new byte[]
		{
				62, 65, 81, 33, 94
		});
		map.put("q", new byte[]
		{
				8, 20, 20, 20, 124
		});
		map.put("R", new byte[]
		{
				127, 9, 25, 41, 70
		});
		map.put("r", new byte[]
		{
				124, 8, 4, 4, 8
		});
		map.put("S", new byte[]
		{
				38, 73, 73, 73, 50
		});
		map.put("s", new byte[]
		{
				72, 84, 84, 84, 32
		});
		map.put("T", new byte[]
		{
				1, 1, 127, 1, 1
		});
		map.put("t", new byte[]
		{
				4, 63, 68, 64, 64
		});
		map.put("U", new byte[]
		{
				63, 64, 64, 64, 63
		});
		map.put("u", new byte[]
		{
				60, 64, 64, 32, 124
		});
		map.put("V", new byte[]
		{
				7, 24, 96, 24, 7
		});
		map.put("v", new byte[]
		{
				28, 32, 64, 32, 28
		});
		map.put("W", new byte[]
		{
				127, 32, 24, 32, 127
		});
		map.put("w", new byte[]
		{
				60, 64, 48, 64, 60
		});
		map.put("X", new byte[]
		{
				99, 20, 8, 20, 99
		});
		map.put("x", new byte[]
		{
				68, 40, 16, 40, 68
		});
		map.put("Y", new byte[]
		{
				7, 8, 120, 8, 7
		});
		map.put("y", new byte[]
		{
				12, 80, 80, 80, 60
		});
		map.put("Z", new byte[]
		{
				97, 81, 73, 69, 67
		});
		map.put("z", new byte[]
		{
				68, 100, 84, 76, 68
		});
		map.put("0", new byte[]
		{
				62, 81, 73, 69, 62
		});
		map.put("1", new byte[]
		{
				0, 66, 127, 64, 0
		});
		map.put("2", new byte[]
		{
				98, 81, 81, 73, 70
		});
		map.put("3", new byte[]
		{
				34, 65, 73, 73, 54
		});
		map.put("4", new byte[]
		{
				24, 20, 18, 127, 16
		});
		map.put("5", new byte[]
		{
				39, 69, 69, 69, 57
		});
		map.put("6", new byte[]
		{
				60, 74, 73, 73, 49
		});
		map.put("7", new byte[]
		{
				1, 113, 9, 5, 3
		});
		map.put("8", new byte[]
		{
				54, 73, 73, 73, 54
		});
		map.put("9", new byte[]
		{
				70, 73, 73, 41, 30
		});
		map.put("~", new byte[]
		{
				2, 1, 2, 4, 2
		});
		map.put("`", new byte[]
		{
				1, 2, 4, 0, 0
		});
		map.put("!", new byte[]
		{
				0, 0, 111, 0, 0
		});
		map.put("@", new byte[]
		{
				62, 65, 93, 85, 14
		});
		map.put("#", new byte[]
		{
				20, 127, 20, 127, 20
		});
		map.put("$", new byte[]
		{
				44, 42, 127, 42, 26
		});
		map.put("%", new byte[]
		{
				38, 22, 8, 52, 50
		});
		map.put("^", new byte[]
		{
				4, 2, 1, 2, 4
		});
		map.put("&", new byte[]
		{
				54, 73, 86, 32, 80
		});
		map.put("*", new byte[]
		{
				42, 28, 127, 28, 42
		});
		map.put("(", new byte[]
		{
				0, 0, 62, 65, 0
		});
		map.put(")", new byte[]
		{
				0, 65, 62, 0, 0
		});
		map.put("-", new byte[]
		{
				8, 8, 8, 8, 8
		});
		map.put("_", new byte[]
		{
				64, 64, 64, 64, 64
		});
		map.put("+", new byte[]
		{
				8, 8, 127, 8, 8
		});
		map.put("=", new byte[]
		{
				36, 36, 36, 36, 36
		});
		map.put("\\", new byte[]
		{
				3, 4, 8, 16, 96
		});
		map.put("|", new byte[]
		{
				0, 0, 127, 0, 0
		});
		map.put("{", new byte[]
		{
				0, 8, 54, 65, 65
		});
		map.put("}", new byte[]
		{
				65, 65, 54, 8, 0
		});
		map.put("[", new byte[]
		{
				0, 127, 65, 65, 0
		});
		map.put("]", new byte[]
		{
				0, 65, 65, 127, 0
		});
		map.put(":", new byte[]
		{
				0, 0, 54, 54, 0
		});
		map.put(";", new byte[]
		{
				0, 91, 59, 0, 0
		});
		map.put(",", new byte[]
		{
				0, 0, 88, 56, 0
		});
		map.put(".", new byte[]
		{
				0, 96, 96, 0, 0
		});
		map.put("<", new byte[]
		{
				8, 20, 34, 65, 0
		});
		map.put(">", new byte[]
		{
				65, 34, 20, 8, 0
		});
		map.put("?", new byte[]
		{
				2, 1, 89, 5, 2
		});
		map.put("/", new byte[]
		{
				96, 16, 8, 4, 3
		});
		map.put("'", new byte[]
		{
				0, 0, 7, 0, 0
		});
		map.put("\"", new byte[]
		{
				0, 7, 0, 7, 0
		});
		map.put(ArrowToken.INCREASING, new byte[]
		{
				16, 24, 28, 24, 16
		});
		map.put(ArrowToken.UNCHANGED, new byte[]
		{
				8, 28, 28, 28, 8
		});
		map.put(ArrowToken.DECREASING, new byte[]
		{
				4, 12, 28, 12, 4
		});
	}

	private int anchor;

	private Color backgroundColor;

	private int bottom;

	private int dotHeight;

	private Color dotOffColor;

	private int dotWidth;

	private int gap;

	private int hGap;

	private int i;

	private int left;

	private int right;

	private int top;

	private int vGap;

	public Matrix()
	{
		this.gap = 2;
		this.i = 0;
		this.top = 0;
		this.left = 0;
		this.bottom = 0;
		this.right = 0;
		this.anchor = 0;
		this.hGap = 1;
		this.vGap = 1;
		this.dotHeight = 1;
		this.dotWidth = 1;
		this.backgroundColor = new Color(0x111111);
		this.dotOffColor = new Color(0x666666);
	}

	/**
	 * @param Anzeige-Element.
	 * @return Weite des gegebenen Anzeige-Elements.
	 */
	public int getWidthOf(final Element displayelement)
	{
		int width = 0;
		Token[] atoken = displayelement.getTokens();

		for (int j1 = 0; j1 < atoken.length; j1++)
		{
			width += b(atoken[j1]);
			width += ((j1 == (atoken.length - 1)) ? 0 : (this.gap * (this.hGap + this.dotWidth)));
		}

		width += (this.i * (this.hGap + this.dotWidth));

		if ((width % (this.hGap + this.dotWidth)) != 0)
		{
			width += ((this.hGap + this.dotWidth) - (width % (this.hGap + this.dotWidth)));
		}

		return width;
	}

	private int b(final Graphics graphics, final byte[] bytes, int x, final int offset)
	{
		Color color = graphics.getColor();

		for (int i = 0; i < bytes.length; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				if ((bytes[i] & (1 << j)) != 0)
				{
					graphics.setColor(color);
					int y = (j * (this.dotHeight + this.vGap));
					graphics.fillRect(x, offset + y, this.dotWidth, this.dotHeight);
				}
			}

			x += (this.dotWidth + this.hGap);
		}

		x += (this.hGap + this.dotWidth);
		graphics.setColor(color);

		return x;
	}

	public void b(final Graphics g1, final Element displayelement, int i1, int j1)
	{
		Token[] atoken = displayelement.getTokens();
		int k1 = (int) Math.ceil(getWidthOf(displayelement) / (this.dotWidth + this.hGap));
		j1 /= (this.dotHeight + this.vGap);
		i1 /= (this.dotWidth + this.hGap);

		Point point = b(i1, j1, k1);
		int l1 = point.x;
		int i2 = point.y;

		for (int j2 = 0; j2 < atoken.length; j2++)
		{
			l1 = b(g1, atoken[j2], l1, i2);
		}
	}

	public void b(final Graphics g1, final int width, final int height)
	{
		g1.setColor(this.backgroundColor);
		g1.fillRect(0, 0, width, height);
		g1.setColor(this.dotOffColor);

		for (int k1 = 0; k1 < height; k1 += this.dotHeight)
		{
			g1.fillRect(0, k1, width, this.dotHeight);
			k1 += this.vGap;
		}

		g1.setColor(this.backgroundColor);

		for (int l1 = this.dotWidth; l1 < width; l1 += this.dotWidth)
		{
			g1.fillRect(l1, 0, this.hGap, height);
			l1 += this.hGap;
		}
	}

	private int b(final Graphics g1, final Token token, int i1, final int j1)
	{
		Color color = token.getColorModel().getColor();
		g1.setColor(color);

		if (token instanceof ArrowToken)
		{
			byte[] abyte0 = (byte[]) map.get(((ArrowToken) token).getArrowType());
			i1 = b(g1, abyte0, i1, j1);
		}
		else
		{
			String s = token.getDisplayValue();

			for (int k1 = 0; k1 < s.length(); k1++)
			{
				byte[] abyte1 = (byte[]) map.get(String.valueOf(s.charAt(k1)));

				if (abyte1 == null)
				{
					abyte1 = (byte[]) map.get("?");
				}

				i1 = b(g1, abyte1, i1, j1);
			}
		}

		i1 += (this.gap * (this.hGap + this.dotWidth));

		return i1;
	}

	public void b(final int i1)
	{
		this.i = i1;
	}

	private Point b(final int i1, final int j1, final int k1)
	{
		Point point = new Point();
		byte byte0 = 7;

		switch (this.anchor)
		{
			case 0: // '\0'
				point.x =
						(this.left + ((i1 - k1 - this.right - this.left) / 2))
								* (this.dotWidth + this.hGap);
				point.y =
						(this.top + ((j1 - byte0 - this.top - this.bottom) / 2))
								* (this.dotHeight + this.vGap);

				break;

			case 1: // '\001'
				point.x =
						(this.left + ((i1 - k1 - this.right - this.left) / 2))
								* (this.dotWidth + this.hGap);
				point.y = this.top * (this.dotHeight + this.vGap);

				break;

			case 4: // '\004'
				point.x =
						(this.left + ((i1 - k1 - this.right - this.left) / 2))
								* (this.dotWidth + this.hGap);
				point.y = (j1 - byte0 - this.bottom) * (this.dotHeight + this.vGap);

				break;

			case 3: // '\003'
				point.x = ((i1 - k1 - this.right) + 1) * (this.dotWidth + this.hGap);
				point.y =
						(this.top + ((j1 - byte0 - this.top - this.bottom) / 2))
								* (this.dotHeight + this.vGap);

				break;

			case 2: // '\002'
				point.x = this.left * (this.dotWidth + this.hGap);
				point.y =
						(this.top + ((j1 - byte0 - this.top - this.bottom) / 2))
								* (this.dotHeight + this.vGap);

				break;

			case 6: // '\006'
				point.x = ((i1 - k1 - this.right) + 1) * (this.dotWidth + this.hGap);
				point.y = this.top * (this.dotHeight + this.vGap);

				break;

			case 5: // '\005'
				point.x = this.left * (this.dotWidth + this.hGap);
				point.y = this.top * (this.dotHeight + this.vGap);

				break;

			case 8: // '\b'
				point.x = ((i1 - k1 - this.right) + 1) * (this.dotWidth + this.hGap);
				point.y = (j1 - byte0 - this.bottom) * (this.dotHeight + this.vGap);

				break;

			case 7: // '\007'
				point.x = this.left * (this.dotWidth + this.hGap);
				point.y = (j1 - byte0 - this.bottom) * (this.dotHeight + this.vGap);

				break;
		}

		return point;
	}

	private int b(final Token token)
	{
		int i1 = 6 * (this.dotWidth + this.hGap);

		if (token instanceof ArrowToken)
		{
			return i1;
		}
		else
		{
			return token.getDisplayValue().length() * i1;
		}
	}

	public int getHeigth()
	{
		return (7 * (this.dotHeight + this.vGap)) - this.vGap;
	}

	/**
	 * Sets the location of the element on the display when the display area is larger than the
	 * element.
	 * 
	 * @param anchor
	 */
	public void setAnchor(final int newValue)
	{
		this.anchor = newValue;
	}

	/**
	 * Sets the background color of the display.
	 * 
	 * @param color - the background color of the display
	 */
	public void setBackgroundColor(final Color color)
	{
		this.backgroundColor = color;
	}

	public void setDotGaps(final int hGap, final int vGap)
	{
		this.hGap = hGap;
		this.vGap = vGap;
	}

	/**
	 * Sets the color of a turned-off led.
	 * 
	 * @param color - the color of a turned-off led
	 */
	public void setDotOffColor(final Color color)
	{
		this.dotOffColor = color;
	}

	/**
	 * Sets the dimensions, in pixels, of the display's led Default values are (1, 1).
	 * 
	 * @param width - the width of the led
	 * @param height - the height of the led
	 */
	public void setDotSize(final int width, final int height)
	{
		this.dotWidth = width;
		this.dotHeight = height;
	}

	/**
	 * Sets the number of dots separating the element from the edges of the component.
	 * 
	 * @param top - leading dots from top
	 * @param left - leading dots from left
	 * @param bottom - leading dots from bottom
	 * @param right - leading dots from right
	 */
	public void setPadding(final int top, final int left, final int bottom, final int right)
	{
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public void setTokenGap(final int newValue)
	{
		this.gap = newValue;
	}
}
