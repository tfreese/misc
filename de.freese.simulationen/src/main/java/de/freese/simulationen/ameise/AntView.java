// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.ameise;

import java.awt.BorderLayout;

import de.freese.simulationen.AbstractSimulationView;

/**
 * View fuer die Ameisen-Simulation.
 * 
 * @author Thomas Freese
 */
public class AntView extends AbstractSimulationView<AntWorld>
{
	/**
	 * Erstellt ein neues {@link AntView} Object.
	 */
	public AntView()
	{
		super();
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationView#createModel(int, int)
	 */
	@Override
	protected AntWorld createModel(final int fieldWidth, final int fieldHeight)
	{
		return new AntWorld(fieldWidth, fieldHeight);
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationView#initialize(int, int)
	 */
	@Override
	public void initialize(final int fieldWidth, final int fieldHeight)
	{
		super.initialize(fieldWidth, fieldHeight);

		AntCanvas canvas = new AntCanvas(getModel());
		getModel().initialize();

		getMainPanel().add(canvas, BorderLayout.CENTER);
	}
}
