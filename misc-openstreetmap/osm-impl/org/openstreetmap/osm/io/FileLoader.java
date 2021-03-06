/**
 * This file is part of LibOSM by Marcus Wolschon <a href="mailto:Marcus@Wolscon.biz">Marcus@Wolscon.biz</a>.
 * You can purchase support for a sensible hourly rate or
 * a commercial license of this file (unless modified by others) by contacting him directly.
 *
 *  LibOSM is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  LibOSM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with LibOSM.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************
 * Editing this file:
 *  -For consistent code-quality this file should be checked with the
 *   checkstyle-ruleset enclosed in this project.
 *  -After the design of this file has settled it should get it's own
 *   JUnit-Test that shall be executed regularly. It is best to write
 *   the test-case BEFORE writing this class and to run it on every build
 *   as a regression-test.
 */
package org.openstreetmap.osm.io;


import java.io.File;
import java.net.URL;

import org.openstreetmap.osm.data.MemoryDataSet;

import org.openstreetmap.osmosis.core.migrate.MigrateV05ToV06;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.core.xml.v0_6.XmlReader;

/**
 * This class exists to make loading an <a hef="http://wiki.openstreetmap.org/index.php/Develop">OpenStreetMap</a>-file
 * via program-code easier. It wraps the existing functionality
 * in <a href="http://wiki.openstreetmap.org/index.php/Osmosis">Osmosis</a>.
 * @author <a href="mailto:Marcus@Wolschon.biz">Marcus Wolschon</a>
 */
public class FileLoader {

    /**
     * The filename we are reading from.
     */
    private File myFileName;

    /**
     * The filename we are reading from.
     */
    //private URL myFileURL;


    /**
     * @param aFileName The filename we are reading from.
     */
    public FileLoader(final File aFileName) {
        super();
        this.myFileName = aFileName;
//        this.myFileURL = null;
    }

    /**
     * @param aFileName The filename we are reading from.
     */
    public FileLoader(final URL aFileName) {
        super();
        this.myFileName = null;
  //      this.myFileURL = aFileName;
    }


    /**
     * @return the file-content or null
     */
    public MemoryDataSet parseOsm() {
        DataSetSink sink = new DataSetSink();

        parseOsm(sink);

        return (MemoryDataSet) sink.getDataSet();
    }


    /**
     * @param sink where to give the file-content for processing
     */
    public void parseOsm(final Sink sink) {
        CompressionMethod compr = CompressionMethod.None;
        if (myFileName.getName().toLowerCase().endsWith(".gz")) {
            compr = CompressionMethod.GZip;
        } else {
            if (myFileName.getName().toLowerCase().endsWith(".bz2")) {
                compr = CompressionMethod.BZip2;
            }
        }

        XmlReader task = new XmlReader(this.myFileName, true, compr);


        task.setSink(sink);
        try {
            task.run();
        } catch (java.lang.NumberFormatException e) {
            if (e.getMessage().equals("null")) {
                // this seems to be an api0.5-file.
                org.openstreetmap.osmosis.core.xml.v0_5.XmlReader oldTask = new org.openstreetmap.osmosis.core.xml.v0_5.XmlReader(this.myFileName, true, compr);
                MigrateV05ToV06 migrate = new MigrateV05ToV06();
                oldTask.setSink(migrate);
                migrate.setSink(sink);
                oldTask.run();
            } else {
                throw e;
            }
        }

    }

}
