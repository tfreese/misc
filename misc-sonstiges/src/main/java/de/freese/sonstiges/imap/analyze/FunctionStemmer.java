// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.util.Locale;
import java.util.function.Function;
import org.apache.lucene.analysis.de.GermanLightStemmer;
import org.apache.lucene.analysis.en.EnglishMinimalStemmer;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.German2Stemmer;

/**
 * Diese {@link Function} führt das Stemming des Strings durch.<br>
 *
 * @author Thomas Freese
 */
public class FunctionStemmer implements Function<String, String>
{
    /**
     * Interface für verschiedene Stemmer Implementierungen.
     *
     * @author Thomas Freese
     */
    private static interface IStemmer
    {
        /**
         * @param token String
         * @return String
         */
        public String stem(String token);
    }

    /**
     * @author Thomas Freese
     */
    static class LuceneEnglishMinimalStemmer implements IStemmer
    {
        /**
         * org.apache.lucene.analysis.en.PorterStemmer
         */
        private final EnglishMinimalStemmer impl = new EnglishMinimalStemmer();

        /**
         * @see de.freese.sonstiges.imap.analyze.FunctionStemmer.IStemmer#stem(java.lang.String)
         */
        @Override
        public String stem(final String token)
        {
            char[] ca = token.toCharArray();
            int lenght = this.impl.stem(ca, ca.length);

            return new String(ca, 0, lenght);
        }
    }

    /**
     * @author Thomas Freese
     */
    static class LuceneGermanLightStemmer implements IStemmer
    {
        /**
         *
         */
        private final GermanLightStemmer impl = new GermanLightStemmer();

        /**
         * @see de.freese.sonstiges.imap.analyze.FunctionStemmer.IStemmer#stem(java.lang.String)
         */
        @Override
        public String stem(final String token)
        {
            char[] ca = token.toCharArray();
            int lenght = this.impl.stem(ca, ca.length);

            return new String(ca, 0, lenght);
        }
    }

    /**
     * @author Thomas Freese
     */
    static class SnowballEnglishStemmer implements IStemmer
    {
        /**
         *
         */
        private final SnowballProgram impl = new EnglishStemmer();

        /**
         * @see de.freese.sonstiges.imap.analyze.FunctionStemmer.IStemmer#stem(java.lang.String)
         */
        @Override
        public String stem(final String token)
        {
            this.impl.setCurrent(token);
            this.impl.stem();

            return this.impl.getCurrent();
        }
    }

    /**
     * @author Thomas Freese
     */
    static class SnowballGerman2Stemmer implements IStemmer
    {
        /**
         *
         */
        private final SnowballProgram impl = new German2Stemmer();

        /**
         * @see de.freese.sonstiges.imap.analyze.FunctionStemmer.IStemmer#stem(java.lang.String)
         */
        @Override
        public String stem(final String token)
        {
            this.impl.setCurrent(token);
            this.impl.stem();

            return this.impl.getCurrent();
        }
    }

    /**
     * Deutscher Stemmer
     */
    public static final Function<String, String> DE = new FunctionStemmer(Locale.GERMAN);

    /**
     * Englischer Stemmer
     */
    public static final Function<String, String> EN = new FunctionStemmer(Locale.ENGLISH);

    /**
     * Liefert die Stemmer-{@link Function} des entsprechenden Locales.
     *
     * @param locale {@link Locale}
     * @return {@link Function}
     */
    public static Function<String, String> get(final Locale locale)
    {
        if (Locale.GERMAN.equals(locale))
        {
            return DE;
        }
        else if (Locale.ENGLISH.equals(locale))
        {
            return EN;
        }
        else
        {
            throw new IllegalArgumentException("not supported locale: " + locale);
        }
    }

    /**
     *
     */
    private final IStemmer stemmer;

    /**
     * Erzeugt eine neue Instanz von {@link FunctionStemmer}
     *
     * @param locale {@link Locale}
     */
    public FunctionStemmer(final Locale locale)
    {
        super();

        if (Locale.GERMAN.equals(locale))
        {
            this.stemmer = new SnowballGerman2Stemmer();
            // this.stemmer = new LuceneGermanLightStemmer();
        }
        else if (Locale.ENGLISH.equals(locale))
        {
            this.stemmer = new SnowballEnglishStemmer();
            // this.stemmer = new LuceneEnglishMinimalStemmer();
        }
        else
        {
            throw new IllegalArgumentException("not supported locale: " + locale);
        }
    }

    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    public String apply(final String text)
    {
        return this.stemmer.stem(text);
    }
}
