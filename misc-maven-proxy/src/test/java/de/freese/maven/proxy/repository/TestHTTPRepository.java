/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import java.net.URI;
import org.junit.jupiter.api.Test;

/**
 * Testklasse für das {@link HTTPRepository}.
 *
 * @author Thomas Freese
 */
public class TestHTTPRepository
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Empty
    }

    /**
     * Erstellt ein neues {@link TestHTTPRepository} Object.
     */
    public TestHTTPRepository()
    {
        super();
    }

    /**
     * @param uri {@link URI}
     * @param path String
     * @throws Exception Falls was schief geht.
     */
    private void test(final URI uri, final String path) throws Exception
    {
        // IRepository repository = new HTTPRepository();
        // repository.setActive(true);
        // repository.setUri(repository);
        // repository.setCharset(Charset.forName("ISO-8859-1"));
        //
        // repository.init();
        //
        // byte[] content = repository.getContent(path);
        // Assert.assertNotNull("content is null", content);
        // Assert.assertTrue("content is empty", content.length > 0);
        //
        // BufferedReader reader =
        // new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
        // String line = reader.readLine();
        //
        // while (line != null)
        // {
        // System.out.println(line);
        // line = reader.readLine();
        // }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testArtifactory() throws Exception
    {
        URI uri = new URI("http://localhost:8089/artifactory/remote-repos");
        String path = "/javax/servlet/servlet-api/2.5/maven-metadata.xml";

        test(uri, path);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testCentral() throws Exception
    {
        URI uri = new URI("http://repo1.maven.org/maven2");
        String path = "/javax/servlet/servlet-api/2.5/maven-metadata.xml";

        test(uri, path);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testFail() throws Exception
    {
        try
        {
            URI uri = new URI("http://localhost:8089/artifactory/remote-repos");
            String path = "blabla";

            test(uri, path);

            // Assert.fail();
        }
        catch (Exception ex)
        {
            // Ignore;
            // throw ex;
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testFreeseWeb() throws Exception
    {
        URI uri = new URI("http://freese-home.de/maven/repository-thirdparty");
        String path = "/com/jgoodies/binding/maven-metadata.xml";

        test(uri, path);
    }
}
