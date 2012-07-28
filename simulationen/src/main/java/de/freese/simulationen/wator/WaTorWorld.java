// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.Cell;

/**
 * Model der WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public class WaTorWorld extends AbstractWorld
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1974828499776577516L;

	/**
	 * Richtung der Berechnung
	 */
	private int direction = 0;

	/**
	 * Brut-Energie der Fische.
	 */
	private int fishBreedEnergy = 5;

	/**
	 * Start-Energie der Fische.
	 */
	private int fishStartEnergy = 1;

	/**
	 *
	 */
	private final ObjectPool<AbstractWatorCell> objectPoolFish;

	/**
	 *
	 */
	private final ObjectPool<AbstractWatorCell> objectPoolShark;

	/**
	 * Brut-Energie der Haie.
	 */
	private int sharkBreedEnergy = 15;

	/**
	 * Start-Energie der Haie.
	 */
	private int sharkStartEnergy = 10;

	/**
	 * Start-Energie der Haie.
	 */
	private int sharkStarveEnergy = 0;

	/**
	 * Erstellt ein neues {@link WaTorWorld} Object.
	 * 
	 * @param width int
	 * @param height int
	 */
	public WaTorWorld(final int width, final int height)
	{
		super(width, height);

		Config config = new Config();
		config.lifo = true;
		config.maxActive = (width * height) + 1;
		config.maxIdle = (width * height) + 1;
		config.maxWait = -1;
		config.minEvictableIdleTimeMillis = -1;
		config.minIdle = 0;
		config.numTestsPerEvictionRun = 0;
		config.softMinEvictableIdleTimeMillis = -1;
		config.testOnBorrow = false;
		config.testOnReturn = false;
		config.testWhileIdle = false;
		config.timeBetweenEvictionRunsMillis = -1;
		config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_FAIL;

		this.objectPoolFish = new GenericObjectPool<>(new FishObjectFactory(), config);
		this.objectPoolShark = new GenericObjectPool<>(new SharkObjectFactory(), config);
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#getCell(int, int)
	 */
	@Override
	public AbstractWatorCell getCell(final int x, final int y)
	{
		return (AbstractWatorCell) super.getCell(x, y);
	}

	/**
	 * Brut-Energie der Fische.
	 * 
	 * @return int
	 */
	public int getFishBreedEnergy()
	{
		return this.fishBreedEnergy;
	}

	/**
	 * @return int
	 */
	public int getFishCounter()
	{
		return getObjectPoolFish().getNumActive();
	}

	/**
	 * Start-Energie der Fische.
	 * 
	 * @return int
	 */
	public int getFishStartEnergy()
	{
		return this.fishStartEnergy;
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#getNullCellColor()
	 */
	@Override
	public Color getNullCellColor()
	{
		return Color.BLACK;
	}

	/**
	 * @return {@link ObjectPool}
	 */
	ObjectPool<AbstractWatorCell> getObjectPoolFish()
	{
		return this.objectPoolFish;
	}

	/**
	 * @return {@link ObjectPool}
	 */
	ObjectPool<AbstractWatorCell> getObjectPoolShark()
	{
		return this.objectPoolShark;
	}

	/**
	 * Brut-Energie der Haie.
	 * 
	 * @return int
	 */
	public int getSharkBreedEnergy()
	{
		return this.sharkBreedEnergy;
	}

	/**
	 * @return int
	 */
	public int getSharkCounter()
	{
		return getObjectPoolShark().getNumActive();
	}

	/**
	 * Start-Energie der Haie.
	 * 
	 * @return int
	 */
	public int getSharkStartEnergy()
	{
		return this.sharkStartEnergy;
	}

	/**
	 * Start-Energie der Haie.
	 * 
	 * @return int
	 */
	public int getSharkStarveEnergy()
	{
		return this.sharkStarveEnergy;
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#initialize()
	 */
	@Override
	public void initialize()
	{
		try
		{
			for (int x = 0; x < getWidth(); x++)
			{
				for (int y = 0; y < getHeight(); y++)
				{
					AbstractWatorCell cell = getCell(x, y);

					if (cell instanceof FishCell)
					{
						getObjectPoolFish().returnObject(cell);
					}
					else if (cell instanceof SharkCell)
					{
						getObjectPoolShark().returnObject(cell);
					}

					setCell(null, x, y);
				}
			}

			// Zufaellige Platzierung
			for (int x = 0; x < getWidth(); x++)
			{
				for (int y = 0; y < getHeight(); y++)
				{
					int type = getRandom().nextInt(10);

					Cell cell = null;

					switch (type)
					{
						case 1:
							FishCell fishCell = (FishCell) getObjectPoolFish().borrowObject();
							fishCell.setWorld(this);
							fishCell.setEnergy(getFishStartEnergy());
							cell = fishCell;

							break;

						case 2:
							SharkCell sharkCell = (SharkCell) getObjectPoolShark().borrowObject();
							sharkCell.setWorld(this);
							sharkCell.setEnergy(getSharkStartEnergy());
							cell = sharkCell;

							break;

						default:
							break;
					}

					if (cell != null)
					{
						cell.moveTo(x, y);
					}
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		fireWorldChanged();
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#nextGeneration()
	 */
	@Override
	public synchronized void nextGeneration()
	{
		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				Cell cell = getCell(x, y);

				if (cell instanceof AbstractWatorCell)
				{
					((AbstractWatorCell) cell).setEdited(false);
				}
			}
		}

		// Richtung der Berechnung aendern, um auftretende Wellenfronten noch oben zu vermeiden.
		if (this.direction == 0)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				for (int y = 0; y < getHeight(); y++)
				{
					Cell cell = getCell(x, y);

					if (cell != null)
					{
						cell.nextGeneration();
					}
				}
			}
		}
		else if (this.direction == 1)
		{
			for (int x = getWidth() - 1; x >= 0; x--)
			{
				for (int y = 0; y < getHeight(); y++)
				{
					Cell cell = getCell(x, y);

					if (cell != null)
					{
						cell.nextGeneration();
					}
				}
			}
		}
		else if (this.direction == 2)
		{
			for (int x = getWidth() - 1; x >= 0; x--)
			{
				for (int y = getHeight() - 1; y >= 0; y--)
				{
					Cell cell = getCell(x, y);

					if (cell != null)
					{
						cell.nextGeneration();
					}
				}
			}
		}
		else if (this.direction == 3)
		{
			for (int x = 0; x < getWidth(); x++)
			{
				for (int y = getHeight() - 1; y >= 0; y--)
				{
					Cell cell = getCell(x, y);

					if (cell != null)
					{
						cell.nextGeneration();
					}
				}
			}
		}

		this.direction++;

		if (this.direction == 4)
		{
			this.direction = 0;
		}

		fireWorldChanged();
	}

	/**
	 * Brut-Energie der Fische.
	 * 
	 * @param fishBreedEnergy int
	 */
	public void setFishBreedEnergy(final int fishBreedEnergy)
	{
		this.fishBreedEnergy = fishBreedEnergy;
	}

	/**
	 * Start-Energie der Fische.
	 * 
	 * @param fishStartEnergy int
	 */
	public void setFishStartEnergy(final int fishStartEnergy)
	{
		this.fishStartEnergy = fishStartEnergy;
	}

	/**
	 * Brut-Energie der Haie.
	 * 
	 * @param sharkBreedEnergy int
	 */
	public void setSharkBreedEnergy(final int sharkBreedEnergy)
	{
		this.sharkBreedEnergy = sharkBreedEnergy;
	}

	/**
	 * Start-Energie der Haie.
	 * 
	 * @param sharkStartEnergy int
	 */
	public void setSharkStartEnergy(final int sharkStartEnergy)
	{
		this.sharkStartEnergy = sharkStartEnergy;
	}

	/**
	 * Start-Energie der Haie.
	 * 
	 * @param sharkStarveEnergy int
	 */
	public void setSharkStarveEnergy(final int sharkStarveEnergy)
	{
		this.sharkStarveEnergy = sharkStarveEnergy;
	}
}
