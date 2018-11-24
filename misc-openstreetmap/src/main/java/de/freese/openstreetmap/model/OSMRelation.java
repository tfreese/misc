/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap.model;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import de.freese.openstreetmap.Matrix;
import de.freese.openstreetmap.Mercator;

/**
 * Relationen gruppieren {@link OSMNode} und {@link OSMWay} zu größeren Entitäten.
 * 
 * @author Thomas Freese
 */
public class OSMRelation extends AbstractOSMEntity
{
	/**
	 *
	 */
	public List<OSMNode> nodes = null;

	/**
	 *
	 */
	public List<OSMWay> ways = null;

	/**
	 * 
	 */
	private Rectangle bounds = null;

	/**
	 * 
	 */
	private Polygon polygon = null;

	/**
	 * Erstellt ein neues {@link OSMRelation} Object.
	 */
	public OSMRelation()
	{
		super();
	}

	/**
	 * @return {@link Rectangle}
	 */
	public Rectangle getBounds()
	{
		if (this.bounds == null)
		{
			for (OSMNode osmNode : getNodes())
			{
				double x = Mercator.mercX(osmNode.getLongitude());
				double y = Mercator.mercY(osmNode.getLatitude());

				if (this.bounds == null)
				{
					this.bounds = new Rectangle();
					this.bounds.x = (int) x;
					this.bounds.y = (int) y;
				}

				this.bounds.add(x, y);
			}

			if (this.bounds == null)
			{
				this.bounds = new Rectangle(0, 0);
			}

			for (OSMWay osmWay : getWays())
			{
				this.bounds = this.bounds.union(osmWay.getBounds());
			}
		}

		return this.bounds;
	}

	/**
	 * @param myZTFMatrix {@link Matrix}
	 * @return {@link Polygon}
	 */
	public Polygon getDrawablePolygon(final Matrix myZTFMatrix)
	{
		if (this.polygon == null)
		{
			this.polygon = new Polygon();

			for (OSMNode osmNode : getNodes())
			{
				double x = Mercator.mercX(osmNode.getLongitude());
				double y = Mercator.mercY(osmNode.getLatitude());
				this.polygon.addPoint((int) x, (int) y);
			}

			for (OSMWay osmWay : getWays())
			{
				Polygon pWay = osmWay.getDrawablePolygon(myZTFMatrix);

				for (int i = 0; i < pWay.npoints; i++)
				{
					this.polygon.addPoint(pWay.xpoints[i], pWay.ypoints[i]);
				}
			}
		}

		return myZTFMatrix.multiply(this.polygon);
	}

	/**
	 * @return {@link List}<OSMNode>
	 */
	public List<OSMNode> getNodes()
	{
		if (this.nodes == null)
		{
			this.nodes = new ArrayList<>();
		}

		return this.nodes;
	}

	/**
	 * @return {@link List}<OSMWay>
	 */
	public List<OSMWay> getWays()
	{
		if (this.ways == null)
		{
			this.ways = new ArrayList<>();
		}

		return this.ways;
	}
}
