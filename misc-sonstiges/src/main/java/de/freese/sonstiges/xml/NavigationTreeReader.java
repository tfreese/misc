// Created: 02.04.2009
package de.freese.sonstiges.xml;

import java.io.File;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXParseException;

/**
 * @author Thomas Freese
 */
public class NavigationTreeReader
{
    /**
     * @param args String[]
     * @throws Exception Fehler
     */
    public static void main(final String[] args) throws Exception
    {
        try
        {
            URL url = ClassLoader.getSystemResource("navigationTree.xsd");
            Source schemaFile = new StreamSource(new File(url.toURI()));

            url = ClassLoader.getSystemResource("navigationTree.xml");
            Source xmlFile = new StreamSource(new File(url.toURI()));

            // Validate gegen Schema.
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);

            // System.setProperty("javax.xml.stream.XMLInputFactory", value) ;

            // Stax Parsing.
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(xmlFile);

            parseDocument(reader);

            reader.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            if (ex instanceof SAXParseException)
            {
                System.err.println("" + ((SAXParseException) ex).getLineNumber());
            }
        }
    }

    /**
     * @param reader {@link XMLStreamReader}
     * @throws XMLStreamException Falls was schief geht.
     */
    private static void parseDocument(final XMLStreamReader reader) throws XMLStreamException
    {
        System.out.println("Version: " + reader.getVersion());
        System.out.println("Is Standalone: " + reader.isStandalone());
        System.out.println("Standalone Set: " + reader.standaloneSet());
        System.out.println("Encoding: " + reader.getEncoding());
        System.out.println("CharacterEncodingScheme: " + reader.getCharacterEncodingScheme());

        System.out.println();
        parseRestOfDocument(reader);
    }

    /**
     * @param reader {@link XMLStreamReader}
     * @throws XMLStreamException Falls was schief geht.
     */
    private static void parseRestOfDocument(final XMLStreamReader reader) throws XMLStreamException
    {
        while (reader.hasNext())
        {
            int type = reader.next();

            switch (type)
            {
                case XMLStreamConstants.START_ELEMENT:

                    System.out.println("NamespaceURI: " + reader.getNamespaceURI());
                    System.out.println("START_ELEMENT: " + reader.getLocalName());
                    System.out.println("Prefix: " + reader.getPrefix());
                    System.out.println("AttributeCount: " + reader.getAttributeCount());

                    for (int i = 0; i < reader.getAttributeCount(); i++)
                    {
                        System.out.printf("%d: AttributeLocalName=%s, AttributeValue=%s, AttributePrefix=%s\n", Integer.valueOf(i),
                                reader.getAttributeLocalName(i), reader.getAttributeValue(i), reader.getAttributePrefix(i));
                    }

                    break;

                case XMLStreamConstants.END_ELEMENT:
                    System.out.println("END_ELEMENT");
                    break;

                case XMLStreamConstants.CHARACTERS:
                    System.out.println("CHARACTERS=" + (reader.isWhiteSpace() ? "" : reader.getText()));
                    break;

                case XMLStreamConstants.SPACE:
                    System.out.println("SPACE");
                    break;

                case XMLStreamConstants.COMMENT:
                    System.out.println("COMMENT=" + reader.getText());
                    break;

                case XMLStreamConstants.END_DOCUMENT:
                    System.out.println("END_DOCUMENT");
                    break;

                default:
                    throw new IllegalStateException("unsupported stax type:" + type);
            }
        }
    }
}
