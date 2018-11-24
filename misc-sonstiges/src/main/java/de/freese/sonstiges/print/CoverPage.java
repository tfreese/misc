/*
 * Created on 29.06.2003 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.freese.sonstiges.print;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Klasse fuer das Deckblatt - die einfachste Printable-Klasse
 *
 * @author Thomas Freese
 */
class CoverPage implements Printable
{
    /**
     * Erstellt ein neues {@link CoverPage} Object.
     */
    public CoverPage()
    {
        super();
    }

    /**
     * Ausgabe auf dem Drucker machen Die iPageIndex-Abfrage entfaellt, da im "book" fest eine Seite eingestellt ist.
     *
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    @Override
    public int print(final Graphics g, final PageFormat pageFormat, final int iPageIndex) throws PrinterException
    {
        // Schriftfarbe einstellen
        g.setColor(Color.black);

        // Text mit Schriftgroesse 30 ausgeben
        g.setFont(g.getFont().deriveFont(128f));
        g.drawString("JAVA!", 100, 300);

        return Printable.PAGE_EXISTS;
    }
}
