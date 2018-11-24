// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.util.function.Function;

/**
 * Diese {@link Function} ersetzt die Deutschen Umlaute.<br>
 * <ul>
 * <li>'ß' -> 'ss'
 * <li>'ae' -> 'ä'
 * <li>'oe' -> 'ö'
 * <li>'ue' -> 'ü', wird nicht ersetzt, wenn darauf ein Vokal oder 'q' folgt
 * </ul>
 *
 * @author Thomas Freese
 */
public class FunctionNormalizeGerman implements Function<String, String>
{
    /**
     *
     */
    public static final Function<String, String> INSTANCE = new FunctionNormalizeGerman();

    /**
     * Erzeugt eine neue Instanz von {@link FunctionNormalizeGerman}
     */
    public FunctionNormalizeGerman()
    {
        super();
    }

    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    public String apply(final String text)
    {
        String t = text.replaceAll("ß", "ss");
        t = t.replaceAll("ae", "ä");
        t = t.replaceAll("oe", "ö");

        int index = t.indexOf("ue");

        if ((index > 0) && ((index + 2) < t.length()))
        {
            char c = t.charAt(index + 2);

            if ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u') || (c == 'q'))
            {
                // NOOP
            }
            else
            {
                t = t.replaceAll("ue", "ü");
            }
        }

        return t;
    }
}
