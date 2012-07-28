/**
 * Created: 06.11.2011
 */

package de.freese.openstreetmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.freese.openstreetmap.model.OSMModel;
import de.freese.openstreetmap.model.OSMNode;
import de.freese.openstreetmap.model.OSMWay;

/**
 * Parser zum Auslesen der XML Kartendaten von http://www.openstreetmap.org.
 * 
 * @author Thomas Freese
 */
public class OSMParser
{
	/**
	 * Erstellt ein neues {@link OSMParser} Object.
	 */
	public OSMParser()
	{
		super();
	}

	/**
	 * Einlesen der Kartendaten.
	 * 
	 * @param inputStream {@link InputStream}
	 * @return {@link OSMModel}
	 * @throws ParserConfigurationException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 * @throws SAXException Falls was schief geht.
	 */
	public OSMModel parse(final InputStream inputStream)
		throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

		Document document = docBuilder.parse(inputStream);
		document.getDocumentElement().normalize();

		OSMModel model = new OSMModel();

		parseNodes(document, model);
		parseWays(document, model);

		return model;
	}

	/**
	 * Einlesen der Kartendaten.
	 * 
	 * @param zipFileName String
	 * @param zipEntryName String
	 * @return {@link OSMModel}
	 * @throws IOException Falls was schief geht.
	 * @throws SAXException Falls was schief geht.
	 * @throws ParserConfigurationException Falls was schief geht.
	 */
	public OSMModel parse(final String zipFileName, final String zipEntryName)
		throws IOException, ParserConfigurationException, SAXException
	{
		ZipFile zipFile = new ZipFile(zipFileName);
		ZipEntry entry = zipFile.getEntry(zipEntryName);
		InputStream inputStream = zipFile.getInputStream(entry);

		OSMModel model = parse(inputStream);

		zipFile.close();

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
					osmWay.getNodes().add(refNode);
				}
			}
		}
	}
}
