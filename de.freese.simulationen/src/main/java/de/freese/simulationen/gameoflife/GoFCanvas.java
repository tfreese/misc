// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.gameoflife;

import de.freese.simulationen.AbstractSimulationCanvas;

/**
 * Zeichenflaeche faer die "Game of Life"-Simulation.
 * 
 * @author Thomas Freese
 */
public class GoFCanvas extends AbstractSimulationCanvas<GoFWorld>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4708875490004658905L;

	/**
	 * Erstellt ein neues {@link GoFCanvas} Object.
	 * 
	 * @param model {@link GoFWorld}
	 */
	public GoFCanvas(final GoFWorld model)
	{
		super(model);
	}
}
