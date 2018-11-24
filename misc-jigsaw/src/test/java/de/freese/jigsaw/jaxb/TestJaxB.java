/**
 * Created: 03.10.2018
 */

package de.freese.jigsaw.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.jigsaw.jaxb.Club;
import de.freese.jigsaw.jaxb.ClubFactory;
import de.freese.jigsaw.jaxb.DJ;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJaxB
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
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        TestJaxB.jaxbContext = JAXBContext.newInstance(Club.class, DJ.class);
    }

    /**
     * Erstellt ein neues {@link TestJaxB} Object.
     */
    public TestJaxB()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test1ToXML() throws Exception
    {
        Club club = ClubFactory.createClub();

        Marshaller m = TestJaxB.jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream os = baos)
        {
            m.marshal(club, baos);
        }

        TestJaxB.bytes = baos.toByteArray();
        Assert.assertNotNull(TestJaxB.bytes);

        System.out.println(new String(TestJaxB.bytes, StandardCharsets.UTF_8));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test2FromXML() throws Exception
    {
        // System.out.println(new String(TestJaxB.bytes, StandardCharsets.UTF_8));

        Unmarshaller unmarshaller = TestJaxB.jaxbContext.createUnmarshaller();

        try (InputStream fis = new ByteArrayInputStream(TestJaxB.bytes))
        {
            Club club = (Club) unmarshaller.unmarshal(fis);
            Assert.assertNotNull(club);
            // ClubFactory.toString(club);
        }
    }
}
