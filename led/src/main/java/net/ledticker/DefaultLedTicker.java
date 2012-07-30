package net.ledticker;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.led.demo.tokens.TextToken;
import net.led.elements.Element;
import net.led.elements.Token;

public class DefaultLedTicker implements LedTicker
{
	private LedPanel ledPanel;

	private Matrix matrix;

	private List<ImageProvider> elements;

	private DefaultElement h;

	public DefaultLedTicker()
	{
		this.h = new DefaultElement();
		this.elements = new ArrayList<ImageProvider>();
		this.matrix = new Matrix();
		this.ledPanel = new LedPanel();
		this.ledPanel.setHeight(this.matrix.getHeigth());
		addElement(this.h);
	}

	@Override
	public void addElement(final Element tickerelement)
	{
		ImageProvider c1 = new ImageProvider(tickerelement, this.matrix, this.ledPanel);
		this.elements.add(c1);
		this.ledPanel.b(c1.getImage(), c1.getObject());
	}

	@Override
	public JComponent getTickerComponent()
	{
		return this.ledPanel;
	}

	@Override
	public void pauseAnimation()
	{
		this.ledPanel.pauseAnimation();
	}

	@Override
	public void removeAll()
	{
		this.elements.clear();
		this.ledPanel.g();
		addElement(this.h);
	}

	@Override
	public void removeElement(final Element tickerelement)
	{
		for (int i = 0; i < this.elements.size(); i++)
		{
			ImageProvider c1 = this.elements.get(i);

			if (c1.getElement() != tickerelement)
			{
				continue;
			}

			this.ledPanel.b(c1.getObject());
			this.elements.remove(c1);

			break;
		}
	}

	@Override
	public void setBackgroundColor(final java.awt.Color color)
	{
		this.matrix.setBackgroundColor(color);
		updateAll();
	}

	@Override
	public void setDotGaps(final int i, final int j)
	{
		this.matrix.setDotGaps(i, j);
		this.ledPanel.setHeight(this.matrix.getHeigth());
		updateAll();
	}

	@Override
	public void setDotOffColor(final java.awt.Color color)
	{
		this.h.setDotOffColor(color);
		this.matrix.setDotOffColor(color);
		updateAll();
	}

	@Override
	public void setDotSize(final int i, final int j)
	{
		this.matrix.setDotSize(i, j);
		this.ledPanel.setHeight(this.matrix.getHeigth());
		updateAll();
	}

	@Override
	public void setElementGap(final int i)
	{
		this.matrix.setElementGap(i);
		updateAll();
	}

	@Override
	public void setSpeed(final int i)
	{
		if ((i >= 1) && (i <= 10))
		{
			this.ledPanel.b(13 - i);
		}
		else
		{
			throw new IllegalArgumentException("Unsupported speed (" + i
					+ "). Speed must be between 1 and 10.");
		}
	}

	@Override
	public void setTokenGap(final int i)
	{
		this.matrix.setTokenGap(i);
		updateAll();
	}

	@Override
	public void startAnimation()
	{
		this.ledPanel.startAnimation();
	}

	@Override
	public void stopAnimation()
	{
		this.ledPanel.stopAnimation();
	}

	@Override
	public void update(final Element tickerelement)
	{
		for (int i = 0; i < this.elements.size(); i++)
		{
			ImageProvider c1 = this.elements.get(i);

			if (c1.getElement() == tickerelement)
			{
				c1.createImage();
				this.ledPanel.b(c1.getImage(), c1.getObject());

				return;
			}
		}

		throw new IllegalArgumentException(
				"Updated element was not found in the Ticker's element list.");
	}

	@Override
	public void updateAll()
	{
		this.ledPanel.b(true);
		this.ledPanel.b(this.matrix.getImage());

		for (int i = 0; i < this.elements.size(); i++)
		{
			ImageProvider c1 = this.elements.get(i);
			c1.createImage();
			this.ledPanel.b(c1.getImage(), c1.getObject());
		}

		this.ledPanel.b(false);
	}

	private class DefaultElement implements Element
	{
		protected Token[] array;

		public DefaultElement()
		{
			TextToken texttoken = new TextToken("WWW.LEDTICKER.NET::");
			this.array = (new Token[]
			{
				texttoken
			});
		}

		public void setDotOffColor(final java.awt.Color newValue)
		{
			java.awt.Color color = new java.awt.Color(newValue.getRGB() ^ 16777215);
			this.array[0].getColorModel().setColor(color);
		}

		@Override
		public Token[] getTokens()
		{
			return this.array;
		}
	}
}
