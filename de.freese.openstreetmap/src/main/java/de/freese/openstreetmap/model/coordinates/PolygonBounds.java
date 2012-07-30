/**
 * PolygonBounds.java created: 18.11.2007 21:26:33 (c) 2007 by <a
 * href="http://Wolschon.biz">Wolschon Softwaredesign und Beratung</a> This file is part of libosm
 * by Marcus Wolschon <a href="mailto:Marcus@Wolscon.biz">Marcus@Wolscon.biz</a>. You can purchase
 * support for a sensible hourly rate or a commercial license of this file (unless modified by
 * others) by contacting him directly. libosm is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. libosm is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with libosm. If not, see <http://www.gnu.org/licenses/>. Editing this file: -For
 * consistent code-quality this file should be checked with the checkstyle-ruleset enclosed in this
 * project. -After the design of this file has settled it should get it's own JUnit-Test that shall
 * be executed regularly. It is best to write the test-case BEFORE writing this class and to run it
 * on every build as a regression-test.
 */
package de.freese.openstreetmap.model.coordinates;

// automatically created logger for debug and error -output
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * (c) 2007 by <a href="http://Wolschon.biz>Wolschon Softwaredesign und Beratung</a>.<br/>
 * Project: libosm<br/>
 * PolygonBounds.java<br/>
 * created: 18.11.2007 21:26:33 <br/>
 * <br/>
 * <br/>
 * These special bounds are denoted by a polygon instead of a simple bounding-box.
 * 
 * @author <a href="mailto:Marcus@Wolschon.biz">Marcus Wolschon</a>
 */
public class PolygonBounds extends Bounds
{
	/**
	 * We use this path for inclusion-tests.
	 */
	private GeneralPath myPolygonPath = new GeneralPath(Path2D.WIND_EVEN_ODD);

	/**
	 * Erstellt ein neues {@link PolygonBounds} Object.
	 */
	public PolygonBounds()
	{
		super();
	}

	/**
	 * Add a point to this polygon.
	 * 
	 * @param lat the latitude
	 * @param lon the longitude
	 */
	public void addPoint(final double lat, final double lon)
	{
		if (this.myPolygonPath.getCurrentPoint() == null)
		{
			this.myPolygonPath.moveTo(lat, lon);
		}
		else
		{
			this.myPolygonPath.lineTo(lat, lon);
		}
	}

	/**
	 * Add a point to this polygon.
	 * 
	 * @param point the point to add.
	 */
	public void addPoint(final LatLon point)
	{
		addPoint(point.lat(), point.lon());
	}

	/**
	 * @return the center of these bounds.
	 */
	@Override
	public LatLon center()
	{
		return getCenter();
	}

	/**
	 * @see de.freese.openstreetmap.model.coordinates.Bounds#contains(double, double)
	 */
	@Override
	public boolean contains(final double aLatitude, final double aLongitude)
	{
		this.myPolygonPath.closePath();

		return this.myPolygonPath.contains(aLatitude, aLongitude);
	}

	/**
	 * @see de.freese.openstreetmap.model.coordinates.Bounds#getCenter()
	 */
	@Override
	public LatLon getCenter()
	{
		this.myPolygonPath.closePath();
		Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

		return new LatLon(bounds2D.getCenterX(), bounds2D.getCenterY());
	}

	/**
	 * @see de.freese.openstreetmap.model.coordinates.Bounds#getMax()
	 */
	@Override
	public LatLon getMax()
	{
		this.myPolygonPath.closePath();
		Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

		return new LatLon(bounds2D.getMaxX(), bounds2D.getMaxY());
	}

	/**
	 * @see de.freese.openstreetmap.model.coordinates.Bounds#getMin()
	 */
	@Override
	public LatLon getMin()
	{
		this.myPolygonPath.closePath();
		Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

		return new LatLon(bounds2D.getMinX(), bounds2D.getMinY());
	}

	/**
	 * @see de.freese.openstreetmap.model.coordinates.Bounds#getSize()
	 */
	@Override
	public double getSize()
	{
		this.myPolygonPath.closePath();
		Rectangle2D bounds2D = this.myPolygonPath.getBounds2D();

		return Math.max(bounds2D.getWidth(), bounds2D.getHeight());
	}

	/**
	 * @see de.freese.openstreetmap.model.coordinates.Bounds#toString()
	 */
	@Override
	public String toString()
	{
		return "PolygonBounds@" + hashCode();
	}
}
