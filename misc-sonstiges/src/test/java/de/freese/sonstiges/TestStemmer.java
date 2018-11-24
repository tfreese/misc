/**
 * Created: 20.01.2011
 */
package de.freese.sonstiges;

import java.util.Arrays;
import org.apache.lucene.analysis.de.GermanLightStemmer;
import org.apache.lucene.analysis.de.GermanMinimalStemmer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.German2Stemmer;
import org.tartarus.snowball.ext.GermanStemmer;

/**
 * TestKlasse für Stemmerfilter.
 *
 * @author Thomas Freese
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestStemmer
{
    /**
     * Interface für verschiedene Stemmer Implementierungen.
     *
     * @author Thomas Freese
     */
    @FunctionalInterface
    private static interface Stemmer
    {
        /**
         * @param charSequence String
         * @return String
         */
        public String stem(String charSequence);
    }

    /**
     * @return {@link Iterable}
     * @throws Exception Falls was schief geht.
     */
    @Parameters(name = "Stemmer: {0}")
    public static Iterable<Object[]> stemmer() throws Exception
    {
        return Arrays.asList(new Object[][]
        {
                {
                        "Tartarus German", new GermanStemmer()
                },
                {
                        "Tartarus German2", new German2Stemmer()
                },
                // {
                // "Lucene German Minimal", new GermanMinimalStemmer()
                // },
                {
                        "Lucene German Light", new GermanLightStemmer()
                }
        });
    }

    /**
     *
     */
    private final Stemmer stemmer;

    /**
     * Erstellt ein neues {@link TestStemmer} Object.
     *
     * @param name String
     * @param stemmerImpl Object
     */
    public TestStemmer(final String name, final Object stemmerImpl)
    {
        super();

        if (stemmerImpl instanceof SnowballProgram)
        {
            this.stemmer = (value) -> {
                ((SnowballProgram) stemmerImpl).setCurrent(value);
                ((SnowballProgram) stemmerImpl).stem();

                return ((SnowballProgram) stemmerImpl).getCurrent();
            };
        }
        else if (stemmerImpl instanceof GermanMinimalStemmer)
        {
            this.stemmer = (value) -> {
                char[] ca = value.toCharArray();
                int lenght = ((GermanMinimalStemmer) stemmerImpl).stem(ca, ca.length);

                return new String(ca, 0, lenght);
            };
        }
        else if (stemmerImpl instanceof GermanLightStemmer)
        {
            this.stemmer = (value) -> {
                char[] ca = value.toCharArray();
                int lenght = ((GermanLightStemmer) stemmerImpl).stem(ca, ca.length);

                return new String(ca, 0, lenght);
            };
        }
        else
        {
            throw new IllegalArgumentException("stemmerImpl");
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testBaeume() throws Exception
    {
        String stem = this.stemmer.stem("bäume");
        Assert.assertEquals("baum", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testBaum() throws Exception
    {
        String stem = this.stemmer.stem("baum");
        Assert.assertEquals("baum", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testBewaldet() throws Exception
    {
        String stem = this.stemmer.stem("bewaldet");
        Assert.assertEquals("bewaldet", stem);

        stem = this.stemmer.stem("bewaldet");
        Assert.assertEquals("bewaldet", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testHuehner() throws Exception
    {
        String stem = this.stemmer.stem("hühner");
        Assert.assertEquals("huhn", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testHuhn() throws Exception
    {
        String stem = this.stemmer.stem("huhn");
        Assert.assertEquals("huhn", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testTaegig() throws Exception
    {
        String stem = this.stemmer.stem("tägig");
        Assert.assertEquals("tagig", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testTage() throws Exception
    {
        String stem = this.stemmer.stem("tage");
        Assert.assertEquals("tag", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testTagung() throws Exception
    {
        String stem = this.stemmer.stem("tagung");
        Assert.assertEquals("tagung", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testWaelder() throws Exception
    {
        String stem = this.stemmer.stem("wälder");
        Assert.assertEquals("wald", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testWald() throws Exception
    {
        String stem = this.stemmer.stem("wald");
        Assert.assertEquals("wald", stem);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testWeiße() throws Exception
    {
        String stem = this.stemmer.stem("weisse");
        Assert.assertEquals("weiss", stem);
    }
}
