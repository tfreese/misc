package net.ledticker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import net.led.elements.ArrowToken;
import net.led.elements.Element;
import net.led.elements.Token;

public class Matrix
{
	protected static final Map<Object, byte[]> map;

	static
	{
		map = new HashMap<Object, byte[]>();
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

	private int bottomInset;

	private int topInset;

	private Color backgroundColor;

	private int dotWidth;

	private int vGap;

	private int tokenGap;

	private int elementGap;

	private Color dotOffColor;

	private int dotHeight;

	private int hGap;

	public Matrix()
	{
		this.tokenGap = 2;
		this.elementGap = 4;
		this.hGap = 1;
		this.vGap = 1;
		this.dotHeight = 1;
		this.dotWidth = 1;
		this.topInset = 1;
		this.bottomInset = 1;
		this.backgroundColor = new Color(1118481);
		this.dotOffColor = new Color(6710886);
	}

	public void setDotOffColor(final Color newValue)
	{
		this.dotOffColor = newValue;
	}

	private int b(final Graphics graphics, final byte[] bytes, int x)
	{
		Color color = graphics.getColor();

		for (byte b : bytes)
		{
			for (int j = 0; j < 7; j++)
			{
				if ((b & (1 << j)) != 0)
				{
					graphics.setColor(color);
					int y = (j + this.topInset) * (this.dotHeight + this.vGap);
					graphics.fillRect(x, y, this.dotWidth, this.dotHeight);
				}
			}

			x += (this.dotWidth + this.hGap);
		}

		x += (this.hGap + this.dotWidth);
		graphics.setColor(color);

		return x;
	}

	public void b(final Graphics g1, final int i1, final int j1)
	{
		g1.setColor(this.backgroundColor);
		g1.fillRect(0, 0, i1, j1);
		g1.setColor(this.dotOffColor);

		for (int k1 = 0; k1 < j1; k1 += this.dotHeight)
		{
			g1.fillRect(0, k1, i1, this.dotHeight);
			k1 += this.vGap;
		}

		g1.setColor(this.backgroundColor);

		for (int l1 = this.dotWidth; l1 < i1; l1 += this.dotWidth)
		{
			g1.fillRect(l1, 0, this.hGap, j1);
			l1 += this.hGap;
		}
	}

	public void b(final Graphics g1, final Element tickerelement)
	{
		Token[] atoken = tickerelement.getTokens();
		int i1 = 0;

		for (Token element : atoken)
		{
			i1 = b(g1, element, i1);
		}
	}

	private int b(final Graphics g1, final Token token, int i1)
	{
		Color color = token.getColorModel().getColor();
		g1.setColor(color);

		if (token instanceof ArrowToken)
		{
			byte[] abyte0 = map.get(((ArrowToken) token).getArrowType());
			i1 = b(g1, abyte0, i1);
		}
		else
		{
			String s = token.getDisplayValue();

			for (int j1 = 0; j1 < s.length(); j1++)
			{
				byte[] abyte1 = map.get(String.valueOf(s.charAt(j1)));

				if (abyte1 == null)
				{
					abyte1 = map.get("?");
				}

				i1 = b(g1, abyte1, i1);
			}
		}

		i1 += (this.tokenGap * (this.hGap + this.dotWidth));

		return i1;
	}

	public void setElementGap(final int newValue)
	{
		this.elementGap = newValue;
	}

	public void setDotSize(final int i1, final int j1)
	{
		this.dotWidth = i1;
		this.dotHeight = j1;
	}

	public int getWidthOf(final Element tickerelement)
	{
		int i1 = 0;
		Token[] atoken = tickerelement.getTokens();

		for (int j1 = 0; j1 < atoken.length; j1++)
		{
			i1 += b(atoken[j1]);
			i1 += ((j1 == (atoken.length - 1)) ? 0 : (this.tokenGap * (this.hGap + this.dotWidth)));
		}

		i1 += (this.elementGap * (this.hGap + this.dotWidth));

		if ((i1 % (this.hGap + this.dotWidth)) != 0)
		{
			i1 += ((this.hGap + this.dotWidth) - (i1 % (this.hGap + this.dotWidth)));
		}

		return i1;
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

	protected Image getDefaultImage()
	{
		int i1 = getHeigth();
		int j1 = 10 * (this.hGap + this.dotWidth);
		BufferedImage bufferedimage = new BufferedImage(j1, i1, 2);
		b(bufferedimage.getGraphics(), j1, i1);

		String s = "WWW.LEDTICKER.NET::";
		int k1 = 0;
		Graphics g1 = bufferedimage.getGraphics();

		for (int l1 = 0; l1 < s.length(); l1++)
		{
			byte[] abyte0 = (byte[]) map.get(String.valueOf(s.charAt(l1)));

			if (abyte0 == null)
			{
				abyte0 = (byte[]) map.get("?");
			}

			k1 = b(g1, abyte0, k1);
		}

		return bufferedimage;
	}

	public void setBackgroundColor(final Color newValue)
	{
		this.backgroundColor = newValue;
	}

	public void setTokenGap(final int newValue)
	{
		this.tokenGap = newValue;
	}

	public void setDotGaps(final int hGap, final int vGap)
	{
		this.hGap = hGap;
		this.vGap = vGap;
	}

	public Image getImage()
	{
		int heigth = getHeigth();
		int width = 10 * (this.hGap + this.dotWidth);
		BufferedImage bufferedimage = new BufferedImage(width, heigth, 2);
		b(bufferedimage.getGraphics(), width, heigth);

		return bufferedimage;
	}

	public int getHeigth()
	{
		return ((this.topInset + this.bottomInset + 7) * (this.dotHeight + this.vGap)) - this.vGap;
	}
}
