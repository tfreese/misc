package de.freese.sonstiges.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.sonstiges.xml.jaxb.model.Club;
import de.freese.sonstiges.xml.jaxb.model.ClubFactory;
import de.freese.sonstiges.xml.jaxb.model.DJ;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestJAXB
{
    /**
    *
    */
    private static byte[] bytes = null;

    /**
     *
     */
    private static JAXBContext jaxbContext = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    public static void beforeAll() throws Exception
    {
        TestJAXB.jaxbContext = JAXBContext.newInstance(Club.class, DJ.class);

        // siehe de/freese/sonstiges/xml/jaxb/model/jaxb.index
        // TestJAXB.jaxbContext = JAXBContext.newInstance("de.freese.sonstiges.xml.jaxb.model");
    }

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
    public void test1ToXML() throws Exception
    {
        Club club = ClubFactory.createClub();

        Marshaller m = TestJAXB.jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream os = baos)
        {
            m.marshal(club, baos);
        }

        TestJAXB.bytes = baos.toByteArray();
        assertNotNull(TestJAXB.bytes);

        System.out.println(new String(TestJAXB.bytes, StandardCharsets.UTF_8));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test2FromXML() throws Exception
    {
        System.out.println(new String(TestJAXB.bytes, StandardCharsets.UTF_8));

        Unmarshaller unmarshaller = TestJAXB.jaxbContext.createUnmarshaller();

        try (InputStream fis = new ByteArrayInputStream(TestJAXB.bytes))
        {
            Club club = (Club) unmarshaller.unmarshal(fis);
            assertNotNull(club);
            // ClubFactory.toString(club);
        }
    }
}
