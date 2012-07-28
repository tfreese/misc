package de.freese.sonstiges.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import de.freese.sonstiges.jaxb.model.Club;
import de.freese.sonstiges.jaxb.model.ClubFactory;
import de.freese.sonstiges.jaxb.model.DJ;

/**
 * @author Thomas Freese
 */
public class TestJAXB
{
	/**
	 * Erstellt ein neues {@link TestJAXB} Object.
	 */
	public TestJAXB()
	{
		super();

		// Links
		// http://openbook.galileocomputing.de/javainsel8/javainsel_15_008.htm#mje87729331896b2153f4d617a13dd4666
		// http://www.tutorials.de/forum/java/263489-jaxb-tutorial.html
	}

	/**
	 * @throws Exception Falls was schief geht.
	 */
	@Test
	public void toXML() throws Exception
	{
		Club club = ClubFactory.createClub();

		JAXBContext context = JAXBContext.newInstance(Club.class, DJ.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(club, baos);

		System.out.println("TestJAXB.toXML():");
		System.out.println(baos.toString());

		FileOutputStream fos = new FileOutputStream("test_jaxb.xml");
		fos.write(baos.toByteArray());

		baos.close();
		fos.close();
	}

	/**
	 * @throws Exception Falls was schief geht.
	 */
	@Test
	public void fromXML() throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(Club.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		FileInputStream fis = new FileInputStream("test_jaxb.xml");
		// FileInputStream fis = new FileInputStream("test_xstream.xml");

		Club club = (Club) unmarshaller.unmarshal(fis);

		fis.close();

		System.out.println("TestJAXB.fromXML():");
		ClubFactory.toString(club);
	}
}
