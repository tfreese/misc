// Created: 22.09.2016
package de.freese.sonstiges.imap.analyze;

import java.io.Reader;
import java.io.StringReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Extrahiert aus einem HTML-Dokument die Tags und liefert den reinen Text.
 *
 * @author Thomas Freese
 */
public class HTML2Text extends HTMLEditorKit.ParserCallback
{
    /**
     *
     */
    private StringBuilder sb = null;

    /**
     * Erstellt ein neues {@link HTML2Text} Object.
     */
    public HTML2Text()
    {
        super();
    }

    /**
     * @return String
     */
    public String getText()
    {
        return this.sb.toString();
    }

    /**
     * @see javax.swing.text.html.HTMLEditorKit.ParserCallback#handleText(char[], int)
     */
    @Override
    public void handleText(final char[] data, final int pos)
    {
        this.sb.append(data).append(" ");
    }

    /**
     * @param html String
     * @return {@link HTML2Text}
     * @throws Exception Falls was schief geht.
     */
    public HTML2Text parse(final String html) throws Exception
    {
        this.sb = new StringBuilder();

        ParserDelegator delegator = new ParserDelegator();

        // org.apache.lucene.analysis.charfilter.HTMLStripCharFilter
        try (Reader reader = new StringReader(html))
        {
            delegator.parse(reader, this, Boolean.TRUE);
        }

        return this;
    }
}