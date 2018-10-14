package de.freese.sonstiges.xml.jaxb.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.freese.sonstiges.xml.jaxb.OpeningDateAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "club")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder(
    {
        "opening", "employees", "guests", "dj"
    })
public class Club
{

    /**
     *
     */
    @XmlElementWrapper(name = "djs")
    private List<DJ> dj = new ArrayList<>();

    /**
     *
     */
    @XmlAttribute
    private int employees = 0;

    /**
     *
     */
    @XmlElementWrapper(name = "guests")
    private final Map<Integer, Integer> guests = new HashMap<>();

    /**
     *
     */
    @XmlJavaTypeAdapter(OpeningDateAdapter.class)
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date opening = new Date();

    /**
     * Erstellt ein neues {@link Club} Object.
     */
    public Club()
    {
        super();
    }

    /**
     * @param dj {@link DJ}
     */
    public void addDJ(final DJ dj)
    {
        this.dj.add(dj);
    }

    /**
     * @return {@link List}
     */
    public List<DJ> getDJs()
    {
        return this.dj;
    }

    /**
     * @return int
     */
    public int getEmployees()
    {
        return this.employees;
    }

    /**
     * @return {@link Map}<Integer,Integer>
     */
    public Map<Integer, Integer> getGuests()
    {
        return this.guests;
    }

    /**
     * @return {@link Date}
     */
    public Date getOpening()
    {
        return this.opening;
    }

    /**
     * @param employees int
     */
    public void setEmployees(final int employees)
    {
        this.employees = employees;
    }
}
