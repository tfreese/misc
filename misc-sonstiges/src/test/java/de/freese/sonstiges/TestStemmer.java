/**
 * Created: 20.01.2011
 */
package de.freese.sonstiges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import java.util.List;
import java.util.stream.Stream;
import org.apache.lucene.analysis.de.GermanLightStemmer;
import org.apache.lucene.analysis.de.GermanMinimalStemmer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.Arguments;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.German2Stemmer;
import org.tartarus.snowball.ext.GermanStemmer;

/**
 * TestKlasse für Stemmerfilter.
 *
 * @author Thomas Freese
 */
class TestStemmer
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
        String stem(String charSequence);
    }

    /**
     *
     */
    // @formatter:off
    private static final List<Arguments> STEMMER = List.of(
            Arguments.of("Tartarus German",  wrap(new GermanStemmer()))
            , Arguments.of("Tartarus German2", wrap(new German2Stemmer()))
            //, Arguments.of("Lucene German Minimal", wrap(new GermanMinimalStemmer()))
            , Arguments.of("Lucene German Light", wrap(new GermanLightStemmer()))
            );
    // @formatter:on

    /**
     * @param stemmerImpl Object
     * @return {@link Stemmer}
     */
    private static Stemmer wrap(final Object stemmerImpl)
    {
        if (stemmerImpl instanceof SnowballProgram)
        {
            return (value) -> {
                ((SnowballProgram) stemmerImpl).setCurrent(value);
                ((SnowballProgram) stemmerImpl).stem();

                return ((SnowballProgram) stemmerImpl).getCurrent();
            };
        }
        else if (stemmerImpl instanceof GermanMinimalStemmer)
        {
            return (value) -> {
                char[] ca = value.toCharArray();
                int lenght = ((GermanMinimalStemmer) stemmerImpl).stem(ca, ca.length);

                return new String(ca, 0, lenght);
            };
        }
        else if (stemmerImpl instanceof GermanLightStemmer)
        {
            return (value) -> {
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
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testBaeume(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("bäume");
        assertEquals("baum", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testBaum(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("baum");
        assertEquals("baum", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testBewaldet(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("bewaldet");
        assertEquals("bewaldet", stem);

        stem = stemmer.stem("bewaldet");
        assertEquals("bewaldet", stem);
    }

    /**
     * @return {@link Stream}
     */
    @TestFactory
    Stream<DynamicNode> testFactory()
    {
        // @formatter:off
        return STEMMER.stream()
                .map(arg -> dynamicContainer((String) arg.get()[0],
                                Stream.of(dynamicTest("testBaeume", () -> testBaeume((Stemmer) arg.get()[1])),
                                        dynamicTest("testBaum", () -> testBaum((Stemmer) arg.get()[1])),
                                        dynamicTest("testBewaldet", () -> testBewaldet((Stemmer) arg.get()[1])),
                                        dynamicTest("testHuehner", () -> testHuehner((Stemmer) arg.get()[1])),
                                        dynamicTest("testHuhn", () -> testHuhn((Stemmer) arg.get()[1])),
                                        dynamicTest("testTaegig", () -> testTaegig((Stemmer) arg.get()[1])),
                                        dynamicTest("testTage", () -> testTage((Stemmer) arg.get()[1])),
                                        dynamicTest("testTagung", () -> testTagung((Stemmer) arg.get()[1])),
                                        dynamicTest("testWaelder", () -> testWaelder((Stemmer) arg.get()[1])),
                                        dynamicTest("testWald", () -> testWald((Stemmer) arg.get()[1])),
                                        dynamicTest("testWeiße", () -> testWeiße((Stemmer) arg.get()[1]))
                                )
                            )
                    )
                ;
        // @formatter:on
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testHuehner(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("hühner");
        assertEquals("huhn", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testHuhn(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("huhn");
        assertEquals("huhn", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testTaegig(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("tägig");
        assertEquals("tagig", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testTage(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("tage");
        assertEquals("tag", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testTagung(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("tagung");
        assertEquals("tagung", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testWaelder(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("wälder");
        assertEquals("wald", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testWald(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("wald");
        assertEquals("wald", stem);
    }

    /**
     * @param stemmer {@link Stemmer}
     * @throws Exception Falls was schief geht.
     */
    void testWeiße(final Stemmer stemmer) throws Exception
    {
        String stem = stemmer.stem("weisse");
        assertEquals("weiss", stem);
    }
}
