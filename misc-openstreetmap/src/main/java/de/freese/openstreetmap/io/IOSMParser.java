/**
 * Created: 12.03.2015
 */

package de.freese.openstreetmap.io;

import java.io.InputStream;
import de.freese.openstreetmap.model.OSMModel;

/**
 * Interface für einen OSM-Parser.
 *
 * @author Thomas Freese
 */
public interface IOSMParser
{
    /**
     * Einlesen der Kartendaten.<br>
     * Der Stream wird NICHT geschlossen !
     * 
     * @param inputStream {@link InputStream}
     * @return {@link OSMModel}
     * @throws Exception Falls was schief geht.
     */
    public OSMModel parse(final InputStream inputStream) throws Exception;

    /**
     * Einlesen der Kartendaten.
     * 
     * @param zipFileName String
     * @param zipEntryName String
     * @return {@link OSMModel}
     * @throws Exception Falls was schief geht.
     */
    public OSMModel parse(final String zipFileName, final String zipEntryName) throws Exception;
}
