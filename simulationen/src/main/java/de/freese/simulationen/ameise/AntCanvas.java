// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.ameise;

import de.freese.simulationen.AbstractSimulationCanvas;
import de.freese.simulationen.model.AbstractWorld;

/**
 * Zeichenflaeche fuer die Ameisen-Simulation.
 * 
 * @author Thomas Freese
 */
public class AntCanvas extends AbstractSimulationCanvas<AntWorld>
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1308912886734563142L;

	/**
	 * Erstellt ein neues {@link AntCanvas} Object.
	 * 
	 * @param model {@link AntWorld}
	 */
	public AntCanvas(final AntWorld model)
	{
		super(model);
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationCanvas#worldChanged(de.freese.simulationen.model.AbstractWorld)
	 */
	@Override
	public void worldChanged(final AbstractWorld world)
	{
		// Aktuelle Position der Ameisen markieren
		for (AntCell cell : ((AntWorld) world).getAntCells())
		{
			int x = cell.getX();
			int y = cell.getY();

			cellColorChanged(x, y, cell.getColor());
		}

		// Neu malen
		super.worldChanged(world);
	}
}
