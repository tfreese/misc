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
 * Gruppiert {@link OSMNode} zu einem zusammenhängenden Weg.
 * 
 * @author Thomas Freese
 */
public class OSMWay extends AbstractOSMEntity
{
	/**
	 * 
	 */
	private Rectangle bounds = null;

	/**
	 * 
	 */
	public List<OSMNode> nodes = null;

	/**
	 * 
	 */
	private Polygon polygon = null;

	/**
	 * Erstellt ein neues {@link OSMWay} Object.
	 */
	public OSMWay()
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
					continue;
				}

				this.bounds.add(x, y);
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
}
