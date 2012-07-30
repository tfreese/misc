// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.projection;

import de.freese.openstreetmap.model.coordinates.EastNorth;
import de.freese.openstreetmap.model.coordinates.LatLon;

/**
 * Classes subclass this are able to convert lat/lon values to planear screen coordinates.
 * 
 * @author imi
 * @author Thomas Freese
 */
public interface Projection
{
	/**
	 * Mercator squares the world.
	 */
	public static final double MAX_LAT = 85.05112877980659;

	/**
	 * The maximum possible longitude is 180� .
	 */
	public static final double MAX_LON = 180;

	/**
	 * The minimal distance that 2 coordinates in OpenStreetMap can have without being the same.
	 */
	public static final double MAX_SERVER_PRECISION = 1e12;

	/**
	 * circumference of the earth in meter.
	 */
	public static final long EARTH_CIRCMFENCE_IN_METERS = 40041455L;

	/**
	 * List of all available Projections.
	 */
	Projection[] ALLPRJECTIONS = new Projection[]
	{
			new Epsg4326(), new Mercator()
	};

	/**
	 * Convert from norting/easting to lat/lon.
	 * 
	 * @param p The geo point to convert. lat/lon members of the point are filled.
	 * @return the converted values
	 */
	public LatLon eastNorth2latlon(EastNorth p);

	/**
	 * Convert from lat/lon to northing/easting.
	 * 
	 * @param lat The geo point to convert. x/y members of the point are filled.
	 * @param lon The geo point to convert. x/y members of the point are filled.
	 * @return the converted values
	 */
	public EastNorth latlon2eastNorth(double lat, double lon);

	/**
	 * Convert from lat/lon to northing/easting.
	 * 
	 * @param latLon The geo point to convert. x/y members of the point are filled.
	 * @return the converted values
	 */
	public EastNorth latlon2eastNorth(LatLon latLon);

	/**
	 * The factor to multiply with an easting coordinate to get from "easting units per
	 * pixel" to "meters per pixel".
	 * 
	 * @return the factor
	 */
	public double scaleFactor();
}
