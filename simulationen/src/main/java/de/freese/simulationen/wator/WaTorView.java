// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.wator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.freese.simulationen.AbstractSimulationView;

/**
 * View fuer die WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public class WaTorView extends AbstractSimulationView<WaTorWorld>
{
	/**
	 * Erstellt ein neues {@link WaTorView} Object.
	 */
	public WaTorView()
	{
		super();
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationView#createModel(int, int)
	 */
	@Override
	protected WaTorWorld createModel(final int fieldWidth, final int fieldHeight)
	{
		return new WaTorWorld(fieldWidth, fieldHeight);
	}

	/**
	 * @param title String
	 * @param value int
	 * @param titleColor {@link Color}
	 * @return {@link JSlider}
	 */
	private JSlider createSlider(final String title, final int value, final Color titleColor)
	{
		JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 20, value);
		TitledBorder border = new TitledBorder(title);
		border.setTitleColor(titleColor);
		slider.setBorder(border);
		slider.setPaintLabels(true);
		slider.setPaintTrack(true);
		slider.setPaintTicks(true);
		// slider.setSnapToTicks(true);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);

		return slider;
	}

	/**
	 * @see de.freese.simulationen.AbstractSimulationView#initialize(int, int)
	 */
	@Override
	public void initialize(final int fieldWidth, final int fieldHeight)
	{
		super.initialize(fieldWidth, fieldHeight);

		WaTorCanvas canvas = new WaTorCanvas(getModel());
		getModel().initialize();

		getMainPanel().add(canvas, BorderLayout.CENTER);

		// Slider fuer Settings
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(3, 1));

		// Startenergie
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.setBorder(new TitledBorder("Startenergie -> Reset"));

		JSlider slider = createSlider("Fische", getModel().getFishStartEnergy(), Color.GREEN);
		slider.addChangeListener(new ChangeListener()
		{
			/**
			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			 */
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();

				if (!source.getValueIsAdjusting())
				{
					int value = source.getValue();

					getModel().setFishStartEnergy(value);
				}
			}
		});
		panel.add(slider);

		slider = createSlider("Haie", getModel().getSharkStartEnergy(), Color.BLUE);
		slider.addChangeListener(new ChangeListener()
		{
			/**
			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			 */
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();

				if (!source.getValueIsAdjusting())
				{
					int value = source.getValue();

					getModel().setSharkStartEnergy(value);
				}
			}
		});
		panel.add(slider);

		sliderPanel.add(panel);

		// Brutenergie
		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.setBorder(new TitledBorder("Brutenergie"));

		slider = createSlider("Fische", getModel().getFishBreedEnergy(), Color.GREEN);
		slider.addChangeListener(new ChangeListener()
		{
			/**
			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			 */
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();

				if (!source.getValueIsAdjusting())
				{
					int value = source.getValue();

					getModel().setFishBreedEnergy(value);
				}
			}
		});
		panel.add(slider);

		slider = createSlider("Haie", getModel().getSharkBreedEnergy(), Color.BLUE);
		slider.addChangeListener(new ChangeListener()
		{
			/**
			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			 */
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();

				if (!source.getValueIsAdjusting())
				{
					int value = source.getValue();

					getModel().setSharkBreedEnergy(value);
				}
			}
		});
		panel.add(slider);

		sliderPanel.add(panel);

		// Sterbenergie
		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.setBorder(new TitledBorder("Sterbenergie"));

		panel.add(Box.createGlue());

		slider = createSlider("Haie", getModel().getSharkStarveEnergy(), Color.BLUE);
		slider.addChangeListener(new ChangeListener()
		{
			/**
			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
			 */
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();

				if (!source.getValueIsAdjusting())
				{
					int value = source.getValue();

					getModel().setSharkStarveEnergy(value);
				}
			}
		});
		panel.add(slider);

		sliderPanel.add(panel);

		getControlPanel().add(sliderPanel, BorderLayout.CENTER);
	}
}
