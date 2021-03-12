/**
 * This file is part of LibOSM. LibOSM is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. LibOSM is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with LibOSM. If not, see <http://www.gnu.org/licenses/>.
 * Editing this file: -For consistent code-quality this file should be checked with the checkstyle-ruleset enclosed in this project. -After the design of this
 * file has settled it should get it's own JUnit-Test that shall be executed regularly. It is best to write the test-case BEFORE writing this class and to run
 * it on every build as a regression-test.
 */
package de.freese.openstreetmap.model.coordinates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import de.freese.openstreetmap.model.projection.Projection;

/**
 * This class represents a single gps-reading. GPS-Readings are not part of the map but are raw-data.
 *
 * @author <a href="mailto:Marcus@Wolschon.biz">Marcus Wolschon</a>
 * @author Thomas Freese
 */
public class GpsPoint
{
    /**
     * 
     */
    private static final SimpleDateFormat SDF_TO_DATE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * The actual coordinates.
     */
    private final LatLon latlon;

    /**
     * The timestamp of the last modification/creation.
     */
    private final Date time;

    /**
     * @param ll latitude and longtitude (The actual coordinates)
     * @param timestamp The timestamp of the last modification/creation.
     * @throws ParseException if the timestamp has a wrong format.
     */
    public GpsPoint(final LatLon ll, final Date timestamp) throws ParseException
    {
        super();

        this.latlon = ll;
        this.time = timestamp;
    }

    /**
     * @param ll latitude and longtitude (The actual coordinates)
     * @param timestamp The timestamp of the last modification/creation.
     * @throws ParseException if the timestamp has a wrong format.
     */
    public GpsPoint(final LatLon ll, final String timestamp) throws ParseException
    {
        super();

        this.latlon = ll;
        this.time = SDF_TO_DATE.parse(timestamp);
    }

    /**
     * The coordinates eastined by the current projection.
     * 
     * @param projection {@link Projection}
     * @return {@link EastNorth}
     */
    public final EastNorth getEastNorth(final Projection projection)
    {
        return projection.latlon2eastNorth(getLatlon().lat(), getLatlon().lon());
    }

    /**
     * @return The actual coordinates
     */
    public LatLon getLatlon()
    {
        return this.latlon;
    }

    /**
     * @return {@link Date}
     */
    public Date getTime()
    {
        return this.time;
    }
}
