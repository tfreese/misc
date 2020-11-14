/**
 *
 */
package de.freese.sonstiges.imap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.sonstiges.imap.analyze.FunctionStemmer;
import de.freese.sonstiges.imap.analyze.FunctionStripNotLetter;
import de.freese.sonstiges.imap.analyze.FunctionStripStopWords;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestMail
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
    @BeforeAll
    public static void beforeAll() throws Exception
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
     *
     */
    @AfterEach
    public void afterEach()
    {
        // Empty
    }

    /**
     *
     */
    @BeforeEach
    public void beforeEach()
    {
        // Empty
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
    void test0100TFunctionStripNotLetter() throws Exception
    {
        String text = "abcdefghijklmnopqrstuvwxyz";
        assertEquals(text, FunctionStripNotLetter.INSTANCE.apply(text));

        text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertEquals(text, FunctionStripNotLetter.INSTANCE.apply(text));

        text = " aBc ";
        assertEquals(" aBc ", FunctionStripNotLetter.INSTANCE.apply(text));

        text = ",.-#+´aBc!\"§$%&/()=";
        assertEquals("      aBc          ", FunctionStripNotLetter.INSTANCE.apply(text));

        text = "0123aBc6789";
        assertEquals("    aBc    ", FunctionStripNotLetter.INSTANCE.apply(text));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test0200TFunctionStemmer() throws Exception
    {
        String ref = "wald";
        assertEquals(ref, FunctionStemmer.DE.apply(ref));
        assertEquals(ref, FunctionStemmer.DE.apply("wälder"));

        ref = "trademark";
        assertEquals(ref, FunctionStemmer.EN.apply(ref));
        assertEquals(ref, FunctionStemmer.EN.apply("trademarks"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test1000TextPlain() throws Exception
    {
        // String text = new HTML2Text().parse(TEXT_PLAIN).getText();
        String text = TEXT_PLAIN;
        PRINT_STREAM.println(text);

        prepare(text);

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test2010HTML2Text() throws Exception
    {
        String text = Jsoup.parse(TEXT_HTML1).text();
        PRINT_STREAM.println(text);

        prepare(text);

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test2020HTML2Text() throws Exception
    {
        String text = Jsoup.parse(TEXT_HTML2).text();
        PRINT_STREAM.println(text);

        prepare(text);

        assertTrue(true);
    }
}
