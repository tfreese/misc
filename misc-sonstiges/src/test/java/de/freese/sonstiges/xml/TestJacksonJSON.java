package de.freese.sonstiges.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import de.freese.sonstiges.xml.jaxb.model.Club;
import de.freese.sonstiges.xml.jaxb.model.ClubFactory;

/**
 * http:// www.baeldung.com/jackson-annotations
 *
 * @author Thomas Freese
 */
//@Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJacksonJSON
{
    /**
    *
    */
    private static byte[] bytes = null;

    /**
    *
    */
    private static ObjectMapper jsonMapper = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        jsonMapper = new ObjectMapper();

        // AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        // AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
        //
        // // Annotation-Mix: Verwende primär JaxB-Annotations und sekundär Jackson-Annotations
        // AnnotationIntrospector introspector = new AnnotationIntrospectorPair(jaxbIntrospector, jacksonIntrospector);

        // jsonMapper.setAnnotationIntrospector(introspector);
        // jsonMapper.getDeserializationConfig().with(introspector);
        // jsonMapper.getSerializationConfig().with(introspector);

        // Name des Root-Objektes mit anzeigen.
        jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        // Globales PrettyPrinting; oder einzeln über jsonMapper.writerWithDefaultPrettyPrinter() nutzbar.
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // jsonMapper.setVisibility(jsonMapper.getVisibilityChecker().with(Visibility.NONE));
        jsonMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
        jsonMapper.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        jsonMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);
    }

    /**
     * Erstellt ein neues {@link TestJacksonJSON} Object.
     */
    public TestJacksonJSON()
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
    public void test010ToJSON() throws Exception
    {
        jsonMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());

        Club club = ClubFactory.createClub();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream os = baos)
        {
            // jsonMapper.writerWithDefaultPrettyPrinter().writeValue(baos, club);
            jsonMapper.writer().writeValue(os, club);
        }

        TestJacksonJSON.bytes = baos.toByteArray();
        Assert.assertNotNull(TestJacksonJSON.bytes);

        System.out.println(new String(TestJacksonJSON.bytes, StandardCharsets.UTF_8));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test011FromJSON() throws Exception
    {
        System.out.println(new String(TestJacksonJSON.bytes, StandardCharsets.UTF_8));

        try (InputStream is = new ByteArrayInputStream(TestJacksonJSON.bytes))
        {
            Club club = jsonMapper.readValue(is, Club.class);
            Assert.assertNotNull(club);
            // ClubFactory.toString(club);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020ToXML() throws Exception
    {
        jsonMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

        Club club = ClubFactory.createClub();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStream os = baos)
        {
            jsonMapper.writer().writeValue(os, club);
        }

        TestJacksonJSON.bytes = baos.toByteArray();
        Assert.assertNotNull(TestJacksonJSON.bytes);

        System.out.println(new String(TestJacksonJSON.bytes, StandardCharsets.UTF_8));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test021FromXML() throws Exception
    {
        System.out.println(new String(TestJacksonJSON.bytes, StandardCharsets.UTF_8));

        try (InputStream is = new ByteArrayInputStream(TestJacksonJSON.bytes))
        {
            Club club = jsonMapper.readValue(is, Club.class);
            Assert.assertNotNull(club);
            // ClubFactory.toString(club);
        }
    }
}
