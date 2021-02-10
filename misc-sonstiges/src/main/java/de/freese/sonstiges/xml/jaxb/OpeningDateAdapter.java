// Created: 21.01.2010
package de.freese.sonstiges.xml.jaxb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB Adapter für ein {@link Date} Objekt.
 *
 * @author Thomas Freese
 */
public class OpeningDateAdapter extends XmlAdapter<String, Date>
{
    /**
     *
     */
    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(final Date date) throws Exception
    {
        return FORMATTER.format(date);
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Date unmarshal(final String date) throws Exception
    {
        return FORMATTER.parse(date);
    }
}
