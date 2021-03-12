// License: GPL. Copyright 2007 by Immanuel Scholz and others
package de.freese.openstreetmap.model.coordinates;

import de.freese.openstreetmap.model.projection.Projection;

/**
 * LatLon are unprojected latitude / longitude coordinates. This class is immutable.
 *
 * @author Imi
 * @author Thomas Freese
 */
public class LatLon extends Coordinate
{
    /**
     * From Aviaton Formulary v1.3. http://williams.best.vwh.net/avform.htm
     *
     * @param lat1 postition1
     * @param lon1 postition1
     * @param lat2 postition2
     * @param lon2 postition2
     * @return the course from position1 to position2
     */
    public static double course(final double lat1, final double lon1, final double lat2, final double lon2)
    {
        return Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), (Math.cos(lat1) * Math.sin(lat2)) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2)))
                % (2 * Math.PI);
    }

    /**
     * Calculate course from dLat and dLon. dLat = lat2 - lat1; dLon = lon2 - lon1.
     *
     * @param lat1 latitude of first point
     * @param lon1 longitude of first point
     * @param lat2 latitude of second point
     * @param lon2 longitude of second point
     * @return the course (direction) in degrees
     */
    public static double deriveCourse(final double lat1, final double lon1, final double lat2, final double lon2)
    {
        double dLat = lat1 - lat2;
        double dLon = lon1 - lon2;
        double alpha = (Math.atan2(dLat, dLon) * 180D) / Math.PI;

        if (alpha <= 90)
        {
            return 90 - alpha;
        }

        return 450 - alpha;
    }

    /**
     * From Aviaton Formulary v1.3 http://williams.best.vwh.net/avform.htm Calculate the great-circle-distance. (Distance between 2 points of a sphrere.)
     *
     * @param lat1 postition1
     * @param lon1 postition1
     * @param lat2 postition2
     * @param lon2 postition2
     * @return the great-circle-distance
     */
    public static double dist(final double lat1, final double lon1, final double lat2, final double lon2)
    {
        return 2 * Math
                .asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2), 2) + (Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon1 - lon2) / 2), 2))));
    }

    /**
     * Computes the distance between this lat/lon and another point on the earth. Uses spherical law of cosines formula, not Haversine. Earth is ellipsoid. see
     * http://www.movable-type.co.uk/scripts/gis-faq-5.1.html
     *
     * @param lat1 the first point.
     * @param lon1 the first point.
     * @param lat2 the second point.
     * @param lon2 the second point.
     * @return distance in metres.
     */
    public static int distanceInMeters(final double lat1, final double lon1, final double lat2, final double lon2)
    {
        final int factor = (int) ((6378 - (21 * Math.sin(Math.toRadians((lat1 + lat2) / 2)))) * 1000);
        // final int factor = 6371000;
        final double p1lat = Math.toRadians(lat1);
        final double p2lat = Math.toRadians(lat2);

        return (int) (Math.acos((Math.sin(p1lat) * Math.sin(p2lat)) + (Math.cos(p1lat) * Math.cos(p2lat) * Math.cos(Math.toRadians(lon2 - lon1)))) * factor);
    }

    /**
     * Computes the distance between this lat/lon and another point on the earth. Uses spherical law of cosines formula, not Haversine. Earth is ellipsoid. see
     * http://www.movable-type.co.uk/scripts/gis-faq-5.1.html
     *
     * @param point1 the first point.
     * @param point2 the second point.
     * @return distance in metres.
     */
    public static int distanceInMeters(final LatLon point1, final LatLon point2)
    {
        return distanceInMeters(point1.lat(), point1.lon(), point2.lat(), point2.lon());
        // final int factor = (int) ((6378 - 21 * Math.sin(Math.toRadians((point1.lat() +
        // point2.lat()) / 2))) * 1000);
        // //final int factor = 6371000;
        // final double p1lat = Math.toRadians(point1.lat());
        // final double p2lat = Math.toRadians(point2.lat());
        // return (int) (
        // Math.acos(
        // Math.sin(p1lat) * Math.sin(p2lat) + Math.cos(p1lat) * Math.cos(p2lat)
        // * Math.cos(Math.toRadians(point2.lon() - point1.lon()))
        // ) * factor);
    }

    /**
     * Given a squared distance as returned by {@link #distance(Coordinate)}, calculate the aproximate distance in kilometers.
     *
     * @param aDist the squared distance in northing/easting-space
     * @param projection {@link Projection}
     * @return the aproximate distance in Km.
     */
    public static double distanceToKilomters(final double aDist, final Projection projection)
    {
        final int kilo = 1000;

        return (Math.sqrt(aDist) * projection.scaleFactor() * Projection.EARTH_CIRCMFENCE_IN_METERS) / kilo;
    }

    /**
     * From Aviaton Formulary v1.3. http://williams.best.vwh.net/avform.htm
     *
     * @return the cource-track-error.
     * @param lat1 postition1
     * @param lon1 postition1
     * @param lat2 postition2
     * @param lon2 postition2
     * @param lat3 postition3 (where we are)
     * @param lon3 postition3 (where we are)
     */
    public static double xtd(final double lat1, final double lon1, final double lat2, final double lon2, final double lat3, final double lon3)
    {
        double distAD = dist(lat1, lon1, lat3, lon3);
        double crsAD = course(lat1, lon1, lat3, lon3);
        double crsAB = course(lat1, lon1, lat2, lon2);

        return Math.asin(Math.sin(distAD) * Math.sin(crsAD - crsAB));
    }

    /**
     * @param lat the unprojected latitude
     * @param lon unprojected longitude
     */
    public LatLon(final double lat, final double lon)
    {
        super(lat, lon);
    }

    /**
     * @return <code>true</code>, if the other point has almost the same lat/lon values, only differ by no more than 1/Projection.MAX_SERVER_PRECISION.
     * @param other the LatLon to compare against
     */
    public boolean equalsEpsilon(final LatLon other)
    {
        final double p = 1 / Projection.MAX_SERVER_PRECISION;

        return (Math.abs(lat() - other.lat()) <= p) && (Math.abs(lon() - other.lon()) <= p);
    }

    /**
     * @return <code>true</code>, if the coordinate is outside the world, compared by using lat/lon.
     */
    public boolean isOutSideWorld()
    {
        return (lat() < -Projection.MAX_LAT) || (lat() > Projection.MAX_LAT) || (lon() < -Projection.MAX_LON) || (lon() > Projection.MAX_LON);
    }

    /**
     * @return <code>true</code> if this is within the given bounding box.
     * @param b the bounds to check against
     */
    public boolean isWithin(final Bounds b)
    {
        return (lat() >= b.getMin().lat()) && (lat() <= b.getMax().lat()) && (lon() > b.getMin().lon()) && (lon() < b.getMax().lon());
    }

    /**
     * @return the unprojected latitude
     */
    public double lat()
    {
        return super.getXCoord();
    }

    /**
     * @return the unprojected longitude
     */
    public double lon()
    {
        return super.getYCoord();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "LatLon[lat=" + lat() + ",lon=" + lon() + "]";
    }
}
