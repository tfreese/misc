/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap.io;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.freese.openstreetmap.model.OSMModel;
import de.freese.openstreetmap.model.OSMNode;
import de.freese.openstreetmap.model.OSMRelation;
import de.freese.openstreetmap.model.OSMWay;

/**
 * Parser zum Auslesen der XML Kartendaten von http://www.openstreetmap.org.<br>
 * Schlechteste Variante, da das gesamte Dokument im Speicher gehalten wird.
 *
 * @author Thomas Freese
 */
public class JdomOSMParser implements IOSMParser
{
    /**
     * @see de.freese.openstreetmap.io.IOSMParser#parse(java.io.InputStream)
     */
    @Override
    public OSMModel parse(final InputStream inputStream) throws Exception
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        Document document = docBuilder.parse(inputStream);
        document.getDocumentElement().normalize();

        OSMModel model = new OSMModel();

        parseNodes(document, model);
        parseWays(document, model);
        parseRelations(document, model);

        inputStream.close();

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

    /**
     * Auslesen der Nodes.
     *
     * @param document {@link Document}
     * @param model {@link OSMModel}
     */
    private void parseNodes(final Document document, final OSMModel model)
    {
        NodeList nodeList = document.getElementsByTagName("node");

        // Teure Operation.
        int nodeListLength = nodeList.getLength();

        for (int i = 0; i < nodeListLength; i++)
        {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();

            long id = Long.parseLong(nodeAttributes.getNamedItem("id").getNodeValue());
            float latitude = Float.parseFloat(nodeAttributes.getNamedItem("lat").getNodeValue());
            float longitude = Float.parseFloat(nodeAttributes.getNamedItem("lon").getNodeValue());

            OSMNode osmNode = new OSMNode();
            osmNode.setID(id);
            osmNode.setLatitude(latitude);
            osmNode.setLongitude(longitude);
            model.getNodeMap().put(id, osmNode);

            // double mX = Mercator.mercX(longitude);
            // double mY = Mercator.mercY(latitude);
            // System.out.println(mX + "- " + mY);

            // Tags auslesen.
            NodeList nodeChildList = node.getChildNodes();

            // Teure Operation.
            int nodeChildListLength = nodeChildList.getLength();

            for (int j = 0; j < nodeChildListLength; j++)
            {
                Node childNode = nodeChildList.item(j);
                nodeAttributes = childNode.getAttributes();
                String nodeName = childNode.getNodeName();

                if ("tag".equals(nodeName))
                {
                    String key = nodeAttributes.getNamedItem("k").getNodeValue();
                    String value = nodeAttributes.getNamedItem("v").getNodeValue();

                    osmNode.getTags().put(key, value);
                }
            }
        }
    }

    /**
     * Auslesen der Relations.
     *
     * @param document {@link Document}
     * @param model {@link OSMModel}
     */
    private void parseRelations(final Document document, final OSMModel model)
    {
        NodeList nodeList = document.getElementsByTagName("relation");

        // Teure Operation.
        int nodeListLength = nodeList.getLength();

        for (int i = 0; i < nodeListLength; i++)
        {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();

            long id = Long.parseLong(nodeAttributes.getNamedItem("id").getNodeValue());

            OSMRelation osmRelation = new OSMRelation();
            osmRelation.setID(id);
            model.getRelationMap().put(id, osmRelation);

            // Tags und Refs auslesen.
            NodeList nodeChildList = node.getChildNodes();

            // Teure Operation.
            int nodeChildListLength = nodeChildList.getLength();

            for (int j = 0; j < nodeChildListLength; j++)
            {
                Node childNode = nodeChildList.item(j);
                nodeAttributes = childNode.getAttributes();
                String nodeName = childNode.getNodeName();

                if ("tag".equals(nodeName))
                {
                    String key = nodeAttributes.getNamedItem("k").getNodeValue();
                    String value = nodeAttributes.getNamedItem("v").getNodeValue();

                    osmRelation.getTags().put(key, value);
                }
                else if ("member".equals(nodeName))
                {
                    String type = nodeAttributes.getNamedItem("type").getNodeValue();
                    long refID = Long.parseLong(nodeAttributes.getNamedItem("ref").getNodeValue());

                    if ("node".equals(type))
                    {
                        OSMNode refNode = model.getNodeMap().get(refID);

                        if (refNode != null)
                        {
                            osmRelation.getNodes().add(refNode);
                        }
                    }
                    else if ("way".equals(type))
                    {
                        OSMWay refWay = model.getWayMap().get(refID);

                        if (refWay != null)
                        {
                            osmRelation.getWays().add(refWay);
                        }
                    }
                }
            }
        }
    }

    /**
     * Auslesen der Ways.
     *
     * @param document {@link Document}
     * @param model {@link OSMModel}
     */
    private void parseWays(final Document document, final OSMModel model)
    {
        NodeList nodeList = document.getElementsByTagName("way");

        // Teure Operation.
        int nodeListLength = nodeList.getLength();

        for (int i = 0; i < nodeListLength; i++)
        {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();

            long id = Long.parseLong(nodeAttributes.getNamedItem("id").getNodeValue());

            OSMWay osmWay = new OSMWay();
            osmWay.setID(id);
            model.getWayMap().put(id, osmWay);

            // Tags und Refs auslesen.
            NodeList nodeChildList = node.getChildNodes();

            // Teure Operation.
            int nodeChildListLength = nodeChildList.getLength();

            for (int j = 0; j < nodeChildListLength; j++)
            {
                Node childNode = nodeChildList.item(j);
                nodeAttributes = childNode.getAttributes();
                String nodeName = childNode.getNodeName();

                if ("tag".equals(nodeName))
                {
                    String key = nodeAttributes.getNamedItem("k").getNodeValue();
                    String value = nodeAttributes.getNamedItem("v").getNodeValue();

                    osmWay.getTags().put(key, value);
                }
                else if ("nd".equals(nodeName))
                {
                    long refID = Long.parseLong(nodeAttributes.getNamedItem("ref").getNodeValue());
                    OSMNode refNode = model.getNodeMap().get(refID);

                    if (refNode != null)
                    {
                        osmWay.getNodes().add(refNode);
                    }
                }
            }
        }
    }
}
