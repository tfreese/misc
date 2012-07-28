// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.gameoflife;

import java.awt.BorderLayout;

import de.freese.simulationen.AbstractSimulationView;

/**
 * View fuer die "Game of Life"-Simulation.
 * 
 * @author Thomas Freese
 */
public class GofView extends AbstractSimulationView<GoFWorld>
{
	/**
	 * Erstellt ein neues {@link GofView} Object.
	 */
	public GofView()
	{
		super();
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationView#createModel(int, int)
	 */
	@Override
	protected GoFWorld createModel(final int fieldWidth, final int fieldHeight)
	{
		return new GoFWorld(fieldWidth, fieldHeight);
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationView#initialize(int, int)
	 */
	@Override
	public void initialize(final int fieldWidth, final int fieldHeight)
	{
		super.initialize(fieldWidth, fieldHeight);

		GoFCanvas canvas = new GoFCanvas(getModel());
		getModel().initialize();

		getMainPanel().add(canvas, BorderLayout.CENTER);
	}
}
