/**
 * Created: 12.03.2015
 */

package de.freese.openstreetmap.io;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import de.freese.openstreetmap.model.OSMModel;
import de.freese.openstreetmap.model.OSMNode;
import de.freese.openstreetmap.model.OSMRelation;
import de.freese.openstreetmap.model.OSMWay;

/**
 * Parser zum Auslesen der XML Kartendaten von http://www.openstreetmap.org.<br>
 * Beste Variante, da nur das aktuelle Element im Speicher gehalten wird.
 *
 * @author Thomas Freese
 */
public class XMLStreamOSMParser implements IOSMParser
{
    /**
     *
     */
    private static final String ATTR_NAME_ID = "id";

    /**
     *
     */
    private static final String ATTR_NAME_KEY = "k";

    /**
     *
     */
    private static final String ATTR_NAME_LAT = "lat";

    /**
     *
     */
    private static final String ATTR_NAME_LON = "lon";

    /**
     *
     */
    private static final String ATTR_NAME_REF = "ref";

    /**
     *
     */
    private static final String ATTR_NAME_TYPE = "type";

    /**
     *
     */
    private static final String ATTR_NAME_VALUE = "v";

    /**
     *
     */
    private static final String NODE_NAME_NODE = "node";

    /**
     *
     */
    private static final String NODE_NAME_RELATION = "relation";

    /**
     *
     */
    private static final String NODE_NAME_RELATIONMEMBER = "member";

    /**
     *
     */
    private static final String NODE_NAME_TAG = "tag";

    /**
     *
     */
    private static final String NODE_NAME_WAY = "way";

    /**
     *
     */
    private static final String NODE_NAME_WAYNODE = "nd";

    /**
     *
     */
    private OSMNode node;

    /**
     *
     */
    private OSMRelation relation;

    /**
     *
     */
    private OSMWay way;

    /**
     * Erstellt ein neues {@link XMLStreamOSMParser} Object.
     */
    public XMLStreamOSMParser()
    {
        super();
    }

    /**
     * @see de.freese.openstreetmap.io.IOSMParser#parse(java.io.InputStream)
     */
    @Override
    public OSMModel parse(final InputStream inputStream) throws Exception
    {
        // Validate gegen Schema.
        // SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // Schema schema = schemaFactory.newSchema(schemaFile);
        // Validator validator = schema.newValidator();
        // validator.validate(xmlFile);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

        return parseDocument(reader);
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

    /**
     * @param reader {@link XMLStreamReader}
     * @return {@link OSMModel}
     * @throws XMLStreamException Falls was schief geht.
     */
    private OSMModel parseDocument(final XMLStreamReader reader) throws XMLStreamException
    {
        OSMModel model = new OSMModel();

        while (reader.hasNext())
        {
            int event = reader.next();

            // for (int i = 0; i < reader.getAttributeCount(); i++)
            // {
            // System.out.printf("%d: AttributeLocalName=%s, AttributeValue=%s%n", Integer.valueOf(i), reader.getAttributeLocalName(i),
            // reader.getAttributeValue(i));
            // }

            switch (event)
            {
                case XMLStreamConstants.START_ELEMENT:

                    String localName = reader.getLocalName();

                    if (NODE_NAME_TAG.equals(localName))
                    {
                        String key = reader.getAttributeValue(null, ATTR_NAME_KEY);
                        String value = reader.getAttributeValue(null, ATTR_NAME_VALUE);

                        if (this.node != null)
                        {
                            this.node.getTags().put(key, value);
                        }
                        else if (this.way != null)
                        {
                            this.way.getTags().put(key, value);
                        }
                        else if (this.relation != null)
                        {
                            this.relation.getTags().put(key, value);
                        }
                    }
                    else if (NODE_NAME_NODE.equals(localName))
                    {
                        long id = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_ID));
                        float lat = Float.parseFloat(reader.getAttributeValue(null, ATTR_NAME_LAT));
                        float lon = Float.parseFloat(reader.getAttributeValue(null, ATTR_NAME_LON));

                        this.node = new OSMNode();
                        this.node.setID(id);
                        this.node.setLatitude(lat);
                        this.node.setLongitude(lon);
                        model.getNodeMap().put(id, this.node);
                    }
                    else if (NODE_NAME_WAY.equals(localName))
                    {
                        long id = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_ID));

                        this.way = new OSMWay();
                        this.way.setID(id);
                        model.getWayMap().put(id, this.way);
                    }
                    else if (NODE_NAME_WAYNODE.equals(localName))
                    {
                        long refID = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_REF));

                        OSMNode n = model.getNodeMap().get(refID);

                        if (n != null)
                        {
                            this.way.getNodes().add(n);
                        }
                    }
                    else if (NODE_NAME_RELATION.equals(localName))
                    {
                        long id = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_ID));

                        this.relation = new OSMRelation();
                        this.relation.setID(id);
                        model.getRelationMap().put(id, this.relation);
                    }
                    else if (NODE_NAME_RELATIONMEMBER.equals(localName))
                    {
                        String type = reader.getAttributeValue(null, ATTR_NAME_TYPE);
                        long refID = Long.parseLong(reader.getAttributeValue(null, ATTR_NAME_REF));

                        if (NODE_NAME_NODE.equals(type))
                        {
                            OSMNode refNode = model.getNodeMap().get(refID);

                            if (refNode != null)
                            {
                                this.relation.getNodes().add(refNode);
                            }
                        }
                        else if (NODE_NAME_WAY.equals(type))
                        {
                            OSMWay refWay = model.getWayMap().get(refID);

                            if (refWay != null)
                            {
                                this.relation.getWays().add(refWay);
                            }
                        }
                    }

                    break;

                case XMLStreamConstants.END_ELEMENT:
                    localName = reader.getLocalName();

                    if (NODE_NAME_NODE.equals(localName))
                    {
                        this.node = null;
                    }
                    else if (NODE_NAME_WAY.equals(localName))
                    {
                        this.way = null;
                    }
                    else if (NODE_NAME_RELATION.equals(localName))
                    {
                        this.relation = null;
                    }

                    break;
            }
        }

        return model;
    }
}
