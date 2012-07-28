// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.coordinates;

import de.freese.openstreetmap.model.projection.Projection;

/**
 * This is a simple data class for "rectangular" areas of the world, given in lat/lon min/max
 * values. Do not confuse this with "Area", which is an OSM-primitive for a vector of nodes,
 * describing some area (like a sea).
 * 
 * @author imi
 * @author Thomas Freese
 */
public class Bounds
{
	/**
	 * The maximum bounds possible.
	 */
	public static final Bounds WORLD = new Bounds();

	/**
	 * The minimum and maximum coordinates.
	 */
	private LatLon myMax = null;

	/**
	 * The minimum and maximum coordinates.
	 */
	private LatLon myMin = null;

	/**
	 * Construct bounds that span the whole world.
	 */
	public Bounds()
	{
		super();

		this.myMin = new LatLon(-Projection.MAX_LAT, -Projection.MAX_LON);
		this.myMax = new LatLon(Projection.MAX_LAT, Projection.MAX_LON);
	}

	/**
	 * Construct bounds out of two points.
	 * 
	 * @param lat0 the first edge
	 * @param lon0 the first edge
	 * @param lat1 the second edge
	 * @param lon1 the second edge
	 */
	public Bounds(final double lat0, final double lon0, final double lat1, final double lon1)
	{
		super();

		this.myMin = new LatLon(Math.min(lat0, lat1), Math.min(lon0, lon1));
		this.myMax = new LatLon(Math.max(lat0, lat1), Math.max(lon0, lon1));
	}

	/**
	 * @param pCenter the center-point
	 * @param pRadius the radius
	 */
	public Bounds(final LatLon pCenter, final double pRadius)
	{
		this(pCenter.lat() - pRadius, pCenter.lon() - pRadius, pCenter.lat() + pRadius, pCenter
				.lon() + pRadius);
	}

	/**
	 * Construct bounds out of two points.
	 * 
	 * @param min the first edge
	 * @param max the second edge
	 */
	public Bounds(final LatLon min, final LatLon max)
	{
		this(min.lat(), min.lon(), max.lat(), max.lon());
	}

	/**
	 * @return Center of the bounding box.
	 */
	public LatLon center()
	{
		// not sure, whether this calculation is right.. maybe there is some
		// more complex calculation needed to get a center of a spherical
		// dimension?
		return new LatLon((this.myMin.lat() + this.myMax.lat()) / 2,
				(this.myMin.lon() + this.myMax.lon()) / 2);
	}

	/**
	 * @param aLatitude the latitude to test.
	 * @param aLongitude the longitude to test.
	 * @return true if the given coordinates are within this bounding-box
	 */
	public boolean contains(final double aLatitude, final double aLongitude)
	{
		if (aLatitude < this.myMin.lat())
		{
			return false;
		}

		if (aLatitude > this.myMax.lat())
		{
			return false;
		}

		if (aLongitude < this.myMin.lon())
		{
			return false;
		}

		if (aLongitude > this.myMax.lon())
		{
			return false;
		}

		return true;
	}

	// /**
	// * Extend the bounds if necessary to include the given point.
	// */
	// public void extend(double LatLon ll) {
	// if (ll.lat() < min.lat()
	// || ll.lon() < min.lon()) {
	// min = new LatLon(Math.min(ll.lat(), min.lat()), Math.min(ll.lon(), min.lon()));
	// }
	// if (ll.lat() > max.lat()
	// || ll.lon() > max.lon()) {
	// max = new LatLon(Math.max(ll.lat(), max.lat()), Math.max(ll.lon(), max.lon()));
	// }
	// }

	/**
	 * @return the center of these bounds.
	 */
	public LatLon getCenter()
	{
		double centerLat = getMin().lat() + ((getMax().lat() - getMin().lat()) / 2.0);
		double centerLon = getMin().lon() + ((getMax().lon() - getMin().lon()) / 2.0);

		return new LatLon(centerLat, centerLon);
	}

	/**
	 * @return the max
	 */
	public LatLon getMax()
	{
		return this.myMax;
	}

	/**
	 * @return the min
	 */
	public LatLon getMin()
	{
		return this.myMin;
	}

	/**
	 * @return the maximum of the coordinate-distances of the max and min lat and lon-values.
	 */
	public double getSize()
	{
		return Math.max(Math.abs(getMax().lat() - getMin().lat()), getMax().lon() - getMin().lon());
	}

	/**
	 * @param aMax the max to set
	 */
	public void setMax(final LatLon aMax)
	{
		this.myMax = aMax;
	}

	/**
	 * @param aMin the min to set
	 */
	public void setMin(final LatLon aMin)
	{
		this.myMin = aMin;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Bounds[" + this.myMin.lat() + "," + this.myMin.lon() + "," + this.myMax.lat() + ","
				+ this.myMax.lon() + "]";
	}
}
