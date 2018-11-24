/**
 * Created: 12.03.2015
 */

package de.freese.openstreetmap.io;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import de.freese.openstreetmap.model.OSMModel;

/**
 * Parser zum Auslesen der XML Kartendaten von http://www.openstreetmap.org.<br>
 * Beste Variante, da nur das aktuelle Element im Speicher gehalten wird.
 *
 * @author Thomas Freese
 */
public class SaxOSMParser implements IOSMParser
{
    /**
     * Erstellt ein neues {@link SaxOSMParser} Object.
     */
    public SaxOSMParser()
    {
        super();
    }

    /**
     * @see de.freese.openstreetmap.io.IOSMParser#parse(java.io.InputStream)
     */
    @Override
    public OSMModel parse(final InputStream inputStream) throws Exception
    {
        OSMModel model = new OSMModel();
        OSMContentHandler contentHandler = new OSMContentHandler(model);
        // XMLReader reader = XMLReaderFactory.createXMLReader();
        // reader.setContentHandler(contentHandler);
        // reader.parse(new InputSource(inputStream));

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        saxParser.parse(inputStream, contentHandler);

        return model;
    }

    /**
     * @see de.freese.openstreetmap.io.IOSMParser#parse(java.lang.String, java.lang.String)
     */
    @Override
    public OSMModel parse(final String zipFileName, final String zipEntryName) throws Exception
    {
        OSMModel model = null;

        try (ZipFile zipFile = new ZipFile(zipFileName))
        {
            ZipEntry entry = zipFile.getEntry(zipEntryName);

            try (InputStream is = zipFile.getInputStream(entry))
            {
                model = parse(is);
            }
        }

        return model;
    }
}
