package net.ledticker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public class LedPanel extends JPanel implements Runnable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7648903683131199825L;

	private static final long d = 10000L;

	public static final byte j = -1;

	public static final byte l = 1;

	private volatile boolean b;

	protected volatile Thread c;

	protected byte e;

	private Dimension f;

	private Image g;

	protected int h;

	private boolean i;

	protected int k;

	private int height;

	private List<A> n;

	private Map<Object, A> o;

	public LedPanel()
	{
		this.k = 1;
		this.h = 10;
		this.b = false;
		this.n = new ArrayList<A>();
		this.o = new HashMap<Object, A>();
		this.i = false;
		this.e = 1;
		setBackground(null);
		setLayout(null);
		setDoubleBuffered(true);
	}

	public void b(final boolean flag)
	{
		this.i = flag;
	}

	public void b(final byte byte0)
	{
		this.e = byte0;
	}

	public void b(final Image image)
	{
		this.g = image;
	}

	public void b(final Image image, final Object obj)
	{
		A a = this.o.get(obj);

		synchronized (this.n)
		{
			if (a == null)
			{
				a = new A();
				this.o.put(obj, a);
				this.n.add(a);
				a.b(image, true);
			}
			else if (a.b() == image.getWidth(null))
			{
				a.b(image, true);
			}
			else
			{
				boolean flag = this.i;
				int i1 = (this.n.get(0)).c();
				int j1 = getWidth();
				int k1 = 0;

				for (int l1 = this.n.size(); (k1 < l1) && (i1 < j1) && !flag; k1++)
				{
					A a1 = this.n.get(k1);

					if (a1 == a)
					{
						flag = true;

						break;
					}

					i1 += a1.b();
				}

				a.b(image, flag);
			}
		}

		if ((this.c == null) || this.b)
		{
			repaint();
		}
	}

	public void b(final int i1)
	{
		this.h = i1;
	}

	public void b(final Object obj)
	{
		synchronized (this.n)
		{
			A a = this.o.remove(obj);

			if (a != null)
			{
				this.n.remove(a);
			}
		}
	}

	public void g()
	{
		synchronized (this.n)
		{
			this.n.clear();
			this.o.clear();
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension dimension = super.getPreferredSize();

		if (this.f == null)
		{
			Insets insets = getInsets();
			dimension =
					new Dimension(dimension.width + 399, this.height + insets.top + insets.bottom);
		}

		return dimension;
	}

	private void h()
	{
		A a;

		try
		{
			label0:
			{
				synchronized (this.n)
				{
					if (!this.n.isEmpty())
					{
						break label0;
					}
				}

				return;
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		a = this.n.get(0);

		if ((a.c() + a.b()) <= 0)
		{
			this.n.remove(0);

			int i1 = a.c() + a.b();
			a.d();
			this.n.add(a);
			a = this.n.get(0);
			a.b(i1);
		}

		a.c(this.k);
	}

	@Override
	public void paintComponent(final Graphics g1)
	{
		int i1;
		int j1;
		label0:
		{
			super.paintComponent(g1);
			i1 = getInsets().top;
			j1 = getWidth();

			synchronized (this.n)
			{
				if (!this.n.isEmpty())
				{
					break label0;
				}

				int k1 = this.g.getWidth(this);

				for (int i2 = 0; i2 < j1; i2 += k1)
				{
					g1.drawImage(this.g, i2, i1, this);
				}
			}

			return;
		}

		int l1 = 0;
		int j2 = (this.n.get(0)).c();
		int k2 = this.n.size();

		while (j2 < j1)
		{
			A a = this.n.get(l1);

			if (a.getImage() != null)
			{
				g1.drawImage(a.getImage(), j2, i1, this);
			}

			j2 += a.b();

			if (++l1 != k2)
			{
				continue;
			}

			if (j2 < 0)
			{
				break;
			}

			l1 = 0;
		}
	}

	public void pauseAnimation()
	{
		this.b = !this.b;
	}

	@Override
	public void run()
	{
		Thread thread = Thread.currentThread();

		while (this.c == thread)
		{
			long l1 = System.currentTimeMillis();

			if (!this.b)
			{
				h();
				repaint();
			}

			l1 = this.h - (System.currentTimeMillis() - l1);

			if (l1 < 1L)
			{
				l1 = 1L;
			}

			try
			{
				Thread.sleep(l1);
			}
			catch (InterruptedException interruptedexception)
			{
				// Ignore
			}
		}
	}

	public void setHeight(final int newValue)
	{
		this.height = newValue;
	}

	@Override
	public void setPreferredSize(final Dimension dimension)
	{
		this.f = dimension;
		super.setPreferredSize(dimension);
	}

	public void startAnimation()
	{
		if ((this.c != null) && this.b)
		{
			this.b = false;
		}
		else
		{
			this.b = false;
			this.c = new Thread(this, "Overload.TickerScroll");
			this.c.setPriority(10);
			this.c.start();
		}
	}

	public void stopAnimation()
	{
		this.c = null;
	}
}
