package de.freese.sonstiges.xml.jaxb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "dj")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
        "firstName", "lastName"
})
public class DJ
{
    /**
     *
     */
    private String firstName;

    /**
     *
     */
    private String lastName;

    /**
     * Erstellt ein neues {@link DJ} Object.
     */
    public DJ()
    {
        super();
    }

    /**
     * @return String
     */
    public String getFirstName()
    {
        return this.firstName;
    }

    /**
     * @return String
     */
    public String getLastName()
    {
        return this.lastName;
    }

    /**
     * @param name String
     */
    public void setFirstName(final String name)
    {
        this.firstName = name;
    }

    /**
     * @param lastName String
     */
    public void setLastName(final String lastName)
    {
        this.lastName = lastName;
    }
}
