package net.ledticker;

import java.awt.Image;

public class A
{

	public A()
	{
	}

	public void b(final Image image, final boolean flag)
	{
		if (image == null)
		{
			this.image = image;
			this.lastImage = image;
			this.width = 0;
			this.b = 0;
			return;
		}
		if (flag)
		{
			this.image = image;
			this.width = image.getWidth(null);
		}
		else
		{
			this.lastImage = image;
		}
	}

	public int c()
	{
		return this.b;
	}

	public int b()
	{
		return this.width;
	}

	public Image getImage()
	{
		return this.image;
	}

	public void d()
	{
		if (this.lastImage != null)
		{
			this.image = this.lastImage;
			this.width = this.lastImage.getWidth(null);
			this.lastImage = null;
			this.b = 0;
		}
	}

	public void c(final int i)
	{
		this.b -= i;
	}

	public void b(final int i)
	{
		this.b = i;
	}

	private int width;

	private int b;

	private Image image;

	private Image lastImage;
}
