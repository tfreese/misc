/**
 *
 */
package de.freese.sonstiges.imap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.sonstiges.imap.analyze.FunctionStemmer;
import de.freese.sonstiges.imap.analyze.FunctionStripNotLetter;
import de.freese.sonstiges.imap.analyze.FunctionStripStopWords;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMail
{
    /**
     * @author Thomas Freese
     */
    private static class NullOutputStream extends OutputStream
    {
        /**
         * @see java.io.OutputStream#write(byte[])
         */
        @Override
        public void write(final byte[] b) throws IOException
        {
            // to /dev/null
        }

        /**
         * @see java.io.OutputStream#write(byte[], int, int)
         */
        @Override
        public void write(final byte[] b, final int off, final int len)
        {
            // to /dev/null
        }

        /**
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(final int b)
        {
            // to /dev/null
        }
    }

    /**
    *
    */
    private static PrintStream PRINT_STREAM = System.out;

    /**
    *
    */
    private static String TEXT_HTML1 = null;

    /**
    *
    */
    private static String TEXT_HTML2 = null;

    /**
    *
    */
    private static String TEXT_PLAIN = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        if (!Boolean.parseBoolean(System.getProperty("run_in_ide", "false")))
        {
            PRINT_STREAM = new PrintStream(new NullOutputStream(), false);
        }

        Charset charset = StandardCharsets.UTF_8;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("mail1.txt"), charset)))
        {
            TEXT_PLAIN = bufferedReader.lines().collect(Collectors.joining(" "));
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("mail1.html"), charset)))
        {
            TEXT_HTML1 = bufferedReader.lines().collect(Collectors.joining(" "));
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("mail2.html"), charset)))
        {
            TEXT_HTML2 = bufferedReader.lines().collect(Collectors.joining(" "));
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link TestMail}
     */
    public TestMail()
    {
        super();
    }

    /**
     *
     */
    @After
    public void afterMethod()
    {
    }

    /**
     *
     */
    @Before
    public void beforeMethod()
    {
    }

    /**
     * Verarbeitet den Text für die Verwendung als Spam-Filter.
     *
     * @param text {@link String}
     */
    private void prepare(final String text)
    {
        PRINT_STREAM.println();
        PRINT_STREAM.println("========================================================================================================");

        // @formatter:off
        List<String> token = Stream.of(text)
            .map(t -> t.split(" "))
            .flatMap(Arrays::stream)
             // peek(System.out::println)
            .collect(Collectors.toList());
        // @formatter:on

        Locale locale = FunctionStripStopWords.guessLocale(token);

        token = ReadImapMails.PRE_FILTER.apply(token);
        token.stream().forEach(PRINT_STREAM::println);

        PRINT_STREAM.println();
        PRINT_STREAM.println("Stemmer --------------------");
        Map<String, Integer> wordCount = ReadImapMails.STEMMER_FILTER.apply(token, locale);
        wordCount.forEach((word, count) -> PRINT_STREAM.printf("%s - %d%n", word, count));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test0100TFunctionStripNotLetter() throws Exception
    {
        String text = "abcdefghijklmnopqrstuvwxyz";
        Assert.assertEquals(text, FunctionStripNotLetter.INSTANCE.apply(text));

        text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Assert.assertEquals(text, FunctionStripNotLetter.INSTANCE.apply(text));

        text = " aBc ";
        Assert.assertEquals(" aBc ", FunctionStripNotLetter.INSTANCE.apply(text));

        text = ",.-#+´aBc!\"§$%&/()=";
        Assert.assertEquals("      aBc          ", FunctionStripNotLetter.INSTANCE.apply(text));

        text = "0123aBc6789";
        Assert.assertEquals("    aBc    ", FunctionStripNotLetter.INSTANCE.apply(text));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test0200TFunctionStemmer() throws Exception
    {
        String ref = "wald";
        Assert.assertEquals(ref, FunctionStemmer.DE.apply(ref));
        Assert.assertEquals(ref, FunctionStemmer.DE.apply("wälder"));

        ref = "trademark";
        Assert.assertEquals(ref, FunctionStemmer.EN.apply(ref));
        Assert.assertEquals(ref, FunctionStemmer.EN.apply("trademarks"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test1000TextPlain() throws Exception
    {
        // String text = new HTML2Text().parse(TEXT_PLAIN).getText();
        String text = TEXT_PLAIN;
        PRINT_STREAM.println(text);

        prepare(text);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test2010HTML2Text() throws Exception
    {
        String text = Jsoup.parse(TEXT_HTML1).text();
        PRINT_STREAM.println(text);

        prepare(text);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test2020HTML2Text() throws Exception
    {
        String text = Jsoup.parse(TEXT_HTML2).text();
        PRINT_STREAM.println(text);

        prepare(text);
    }
}
