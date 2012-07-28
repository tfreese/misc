// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.WorldListener;

/**
 * Zeichenflaeche fuer die Simulationen.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ des SimulationsModells.
 */
public abstract class AbstractSimulationCanvas<T extends AbstractWorld> extends Canvas implements
		WorldListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4896850562260701814L;

	/**
	 * Pixelgroesse einer Zelle.
	 */
	private final int cellSize;

	/**
	 *
	 */
	private final Map<Color, int[]> colorArrayMap = new HashMap<>();

	/**
	 *
	 */
	private final Dimension dimension;

	/**
	 *
	 */
	private final BufferedImage image;

	/**
	 *
	 */
	private final boolean useVolatileImage;

	/**
	 *
	 */
	private VolatileImage volatileImage = null;

	/**
	 * Erstellt ein neues {@link AbstractSimulationCanvas} Object.
	 * 
	 * @param model {@link AbstractWorld}
	 */
	public AbstractSimulationCanvas(final T model)
	{
		super();

		this.cellSize =
				Integer.parseInt(SimulationDemo.properties.getProperty("simulation.cell.size", "1"));
		this.useVolatileImage =
				Boolean.parseBoolean(SimulationDemo.properties.getProperty(
						"simulation.use.volatileImage", "false"));

		int width = model.getWidth() * this.cellSize;
		int height = model.getHeight() * this.cellSize;

		this.image =
				new BufferedImage(width * this.cellSize, height * this.cellSize,
						BufferedImage.TYPE_INT_RGB);
		this.dimension =
				new Dimension(model.getWidth() * this.cellSize, model.getHeight() * this.cellSize);

		model.addWorldListener(this);
	}

	/**
	 * @see de.freese.simulationen.model.WorldListener#cellColorChanged(int, int, java.awt.Color)
	 */
	@Override
	public void cellColorChanged(final int x, final int y, final Color color)
	{
		int[] rgbArray = getRGBArray(color);

		getImage().setRGB(x * getCellSize(), y * getCellSize(), getCellSize(), getCellSize(),
				rgbArray, 0, 0);
	}

	/**
	 * Pixelgroesse einer Zelle.
	 * 
	 * @return int
	 */
	protected int getCellSize()
	{
		return this.cellSize;
	}

	/**
	 * @see java.awt.Component#getHeight()
	 */
	@Override
	public final int getHeight()
	{
		return (int) this.dimension.getHeight();
	}

	/**
	 * @return {@link BufferedImage}
	 */
	protected final BufferedImage getImage()
	{
		return this.image;
	}

	/**
	 * @see java.awt.Component#getMaximumSize()
	 */
	@Override
	public final Dimension getMaximumSize()
	{
		return this.dimension;
	}

	/**
	 * @see java.awt.Component#getMinimumSize()
	 */
	@Override
	public final Dimension getMinimumSize()
	{
		return this.dimension;
	}

	/**
	 * @see java.awt.Component#getPreferredSize()
	 */
	@Override
	public final Dimension getPreferredSize()
	{
		return this.dimension;
	}

	/**
	 * @param color {@link Color}
	 * @return int[]
	 */
	protected final int[] getRGBArray(final Color color)
	{
		int[] rgbArray = this.colorArrayMap.get(color);

		if (rgbArray == null)
		{
			rgbArray = new int[getCellSize()];
			Arrays.fill(rgbArray, color.getRGB());
			this.colorArrayMap.put(color, rgbArray);
		}

		return rgbArray;
	}

	/**
	 * @see java.awt.Component#getSize()
	 */
	@Override
	public final Dimension getSize()
	{
		return this.dimension;
	}

	/**
	 * @see java.awt.Component#getSize(java.awt.Dimension)
	 */
	@Override
	public final Dimension getSize(final Dimension rv)
	{
		return this.dimension;
	}

	/**
	 * BackBuffer, erzeugt lazy das {@link VolatileImage} wenn noetig.
	 * 
	 * @return {@link VolatileImage}
	 */
	private VolatileImage getVolatileImage()
	{
		if (this.volatileImage == null)
		{
			GraphicsConfiguration gc = getGraphicsConfiguration();

			this.volatileImage = gc.createCompatibleVolatileImage(getWidth(), getHeight());
		}

		return this.volatileImage;
	}

	/**
	 * @see java.awt.Component#getWidth()
	 */
	@Override
	public final int getWidth()
	{
		return (int) this.dimension.getWidth();
	}

	/**
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(final Graphics g)
	{
		// // super.paint(g);
		int x = 0;
		int y = 0;

		if (!this.useVolatileImage)
		{
			g.drawImage(this.image, x, y, null);

			return;
		}

		// Main rendering loop. Volatile images may lose their contents.
		// This loop will continually render to (and produce if neccessary) volatile images
		// until the rendering was completed successfully.
		do
		{

			// Validate the volatile image for the graphics configuration of this
			// component. If the volatile image doesn't apply for this graphics configuration
			// (in other words, the hardware acceleration doesn't apply for the new device)
			// then we need to re-create it.
			GraphicsConfiguration gc = getGraphicsConfiguration();
			int valCode = getVolatileImage().validate(gc);

			// This means the device doesn't match up to this hardware accelerated image.
			if (valCode == VolatileImage.IMAGE_INCOMPATIBLE)
			{
				this.volatileImage = null;
				// createBackBuffer(); // recreate the hardware accelerated image.
			}

			Graphics offscreenGraphics = getVolatileImage().getGraphics();
			offscreenGraphics.drawImage(this.image, x, y, null);

			// paint back buffer to main graphics
			g.drawImage(getVolatileImage(), 0, 0, this);
			// Test if content is lost
		}
		while (getVolatileImage().contentsLost());
	}

	/**
	 * @see de.freese.simulationen.model.WorldListener#worldChanged(de.freese.simulationen.model.AbstractWorld)
	 */
	@Override
	public void worldChanged(final AbstractWorld world)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			repaint();
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				/**
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run()
				{
					repaint();
				}
			});
		}

		// SimulationDemo.executorService.execute(runnable);
	}

	// /**
	// * Aktualisieren des Bildes.
	// *
	// * @param model {@link AbstractWorld}
	// */
	// protected void updateImage(final T model)
	// {
	// int xOffset = 0;
	//
	// for (int x = 0; x < model.getWidth(); x++)
	// {
	// int yOffset = 0;
	//
	// for (int y = 0; y < model.getHeight(); y++)
	// {
	// Cell cell = model.getCell(x, y);
	// Color color = null;
	//
	// if (cell == null)
	// {
	// color = model.getNullCellColor();
	// }
	// else
	// {
	// color = cell.getColor();
	// }
	//
	// int[] rgbArray = getRGBArray(color);
	//
	// getImage().setRGB(xOffset, yOffset, getCellSize(), getCellSize(), rgbArray, 0, 0);
	//
	// yOffset += getCellSize();
	// }
	//
	// xOffset += getCellSize();
	// }
	// }
}
