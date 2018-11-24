// Erzeugt: 13.07.2016
package de.freese.sonstiges.jmx;

import java.time.LocalDateTime;

/**
 * @author Thomas Freese
 */
public class DateBean implements IDateBean
{
    /**
     * Erzeugt eine neue Instanz von {@link DateBean}
     */
    public DateBean()
    {
        super();
    }

    /**
     * @see de.freese.sonstiges.jmx.IDateBean#getDate()
     */
    @Override
    public String getDate()
    {
        return "Date: " + LocalDateTime.now().toString();
    }
}
