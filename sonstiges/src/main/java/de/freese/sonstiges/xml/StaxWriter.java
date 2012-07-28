/**
 * Created: 15.06.2012
 */

package de.freese.sonstiges.xml;

import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamWriter;

import de.freese.base.utils.xml.PrettyPrintXMLStreamWriter;

/**
 * @author Thomas Freese
 */
public class StaxWriter
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		String encoding = "ISO-8859-1";

		// System.setProperty("javax.xml.stream.XMLOutputFactory", value) ;

		OutputStream os1 = new FileOutputStream("stax-writer1.xml");
		// OutputStream os1 = new ByteArrayOutputStream();

		// XMLOutputFactory factory = XMLOutputFactory.newInstance();
		// factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
		// XMLStreamWriter writer = factory.createXMLStreamWriter(os1, encoding);
		XMLStreamWriter writer = new PrettyPrintXMLStreamWriter(os1, encoding);

		// PrettyPring per Proxy
		// StaxPrettyPrintHandler handler = new StaxPrettyPrintHandler(writer);
		// writer =
		// (XMLStreamWriter) Proxy.newProxyInstance(XMLStreamWriter.class.getClassLoader(),
		// new Class[]
		// {
		// XMLStreamWriter.class
		// }, handler);

		writer.writeStartDocument(encoding, "1.0");

		// Write the root element "person" with a single attribute "gender"
		writer.writeStartElement("person");
		writer.writeNamespace("one", "http://namespaceOne");
		writer.writeAttribute("gender", "f");
		// writer.writeCharacters("\n");

		// Write the "name" element with some content and two attributes
		// writer.writeCharacters("    ");
		writer.writeStartElement("one", "name", "http://namespaceOne");
		writer.writeAttribute("hair", "pigtails");
		writer.writeAttribute("freckles", "yes");

		writer.writeStartElement("firstname");
		writer.writeCharacters("Pippi Longstocking");
		writer.writeEndElement();

		writer.writeEmptyElement("empty");

		// End the "name" element
		writer.writeEndElement();
		// writer.writeCharacters("\n");

		// End the "person" element
		writer.writeEndElement();

		writer.flush();
		writer.close();
		os1.close();

		// Transformation, PrettyPrint
		// StreamSource xmlInput = new StreamSource(new FileInputStream("stax-writer1.xml"));
		//
		// OutputStream fos = new FileOutputStream("stax-writer2.xml");
		// StreamResult xmlOutput = new StreamResult(fos);
		//
		// Transformer transformer = TransformerFactory.newInstance().newTransformer();
		// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		// transformer.transform(xmlInput, xmlOutput);
		//
		// xmlInput.getInputStream().close();
		// xmlOutput.getOutputStream().close();

		System.out.println("StaxWriter.main()");
		System.out.printf("%3s", "");
	}
}
