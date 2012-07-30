// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.wator;

import de.freese.simulationen.AbstractSimulationCanvas;

/**
 * Zeichenflaeche fuer die WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public class WaTorCanvas extends AbstractSimulationCanvas<WaTorWorld>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4708875490004658905L;

	// /**
	// *
	// */
	// private final int[] pixels;
	//
	// /**
	// *
	// */
	// private final Image img;
	//
	// /**
	// *
	// */
	// private final MemoryImageSource imageSource;

	/**
	 * Erstellt ein neues {@link WaTorCanvas} Object.
	 * 
	 * @param model {@link WaTorWorld}
	 */
	public WaTorCanvas(final WaTorWorld model)
	{
		super(model);

		// this.pixels = new int[model.getWidth() * getCellSize() * model.getHeight() *
		// getCellSize()];
		//
		// this.imageSource =
		// new MemoryImageSource(model.getWidth() * getCellSize(), model.getHeight()
		// * getCellSize(), this.pixels, 0, model.getWidth() * getCellSize());
		// this.imageSource.setAnimated(true);
		//
		// this.img = createImage(this.imageSource);
	}

	// /**
	// * @see
	// de.freese.simulationen.AbstractSimulationCanvas#worldChanged(de.freese.simulationen.model.AbstractWorld)
	// */
	// @Override
	// public void worldChanged(AbstractWorld world)
	// {
	// this.imageSource
	// }

	// /**
	// * @see de.freese.simulationen.model.WorldListener#cellColorChanged(int, int, java.awt.Color)
	// */
	// @Override
	// public void cellColorChanged(final int x, final int y, final Color color)
	// {
	// int index = y * 900 + x;
	// int rgb = color == null ? Color.BLACK.getRGB() : color.getRGB();
	//
	// this.pixels[index] = rgb;
	//
	// // Arrays.fill(this.pixels, index, index + getCellSize() * getCellSize(), rgb);
	//
	// // for (int i = 0; i < getCellSize() * getCellSize(); i++)
	// // {
	// // this.pixels[index + i] = rgb;
	// // }
	// }
}
