package net.ledticker;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;

import net.led.elements.Element;

public class ImageProvider
{
	private Component component;

	private Element element;

	private Image image;

	private Matrix matrix;

	private Object object;

	public ImageProvider(final Element element, final Matrix matrix, final Component component)
	{
		this.object = new Object();
		this.element = element;
		this.matrix = matrix;
		this.component = component;
		createImage();
	}

	public void createImage()
	{
		int width = this.matrix.getWidthOf(this.element);
		int heigth = this.matrix.getHeigth();

		if ((this.image == null) || (this.image.getWidth(null) != width)
				|| (this.image.getHeight(null) != heigth) || (this.image instanceof BufferedImage))
		{
			this.image = this.component.createImage(width, heigth);

			if (this.image == null)
			{
				this.image = new BufferedImage(width, heigth, 2);
			}
		}

		if (this.image != null)
		{
			java.awt.Graphics g = this.image.getGraphics();
			this.matrix.b(g, this.image.getWidth(null), this.image.getHeight(null));
			this.matrix.b(g, this.element);
		}
	}

	public Element getElement()
	{
		return this.element;
	}

	public Image getImage()
	{
		return this.image;
	}

	public Object getObject()
	{
		return this.object;
	}
}
