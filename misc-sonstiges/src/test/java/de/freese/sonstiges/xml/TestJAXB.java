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

import de.freese.sonstiges.xml.jaxb.model.Club;
import de.freese.sonstiges.xml.jaxb.model.ClubFactory;
import de.freese.sonstiges.xml.jaxb.model.DJ;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestJAXB
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
    static void beforeAll() throws Exception
    {
        TestJAXB.jaxbContext = JAXBContext.newInstance(Club.class, DJ.class);

        // siehe de/freese/sonstiges/xml/jaxb/model/jaxb.index
        // TestJAXB.jaxbContext = JAXBContext.newInstance("de.freese.sonstiges.xml.jaxb.model");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test010ToXML() throws Exception
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
    void test020FromXML() throws Exception
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
