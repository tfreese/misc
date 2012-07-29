/**
 * 16.08.2006
 */
package de.freese.jpa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author Thomas Freese
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "hibernate.test")
@Entity
@Table(name = "PERSON", uniqueConstraints =
{
	@UniqueConstraint(columnNames =
	{
			"name", "vorName"
	})
})
public class Person implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 413810580854319964L;

	/**
     * 
     */
	@Column(name = "NAME", nullable = false)
	private String name = null;

	/**
     * 
     */
	@Column(name = "PERSON_PK", unique = true, nullable = false)
	// @GenericGenerator(
	// name = "seq", strategy = "seqhilo", parameters =
	// {
	// @Parameter(name = "max_lo", value = "50")
	// , @Parameter(name = "sequence", value = "PERSON_SEQ")
	// }
	//
	// )
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	@Id
	@SequenceGenerator(name = "seq", sequenceName = "OBJECT_SEQ", initialValue = 1, allocationSize = 1)
	private Long oid = null;

	/**
     * 
     */
	@Column(name = "VORNAME", nullable = false)
	private String vorName = null;

	/**
	 *
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade =
	{
			CascadeType.PERSIST, CascadeType.REMOVE
	})
	@OrderBy("street desc")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "hibernate.test")
	@Fetch(FetchMode.SELECT)
	private List<Address> addresses = new ArrayList<>();

	/**
	 * Creates a new {@link Person} object.
	 */
	public Person()
	{
		super();
	}

	/**
	 * @param address {@link Address}
	 */
	public void addAddress(final Address address)
	{
		this.addresses.add(address);
		address.setPerson(this);
	}

	/**
	 * @return List<Address>
	 */

	public List<Address> getAddresses()
	{
		return this.addresses;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * initialValue=Startwert, allocationSize=Schritte der Nummern
	 * 
	 * @return Long
	 */
	public Long getOID()
	{
		return this.oid;
	}

	/**
	 * @return String
	 */
	public String getVorName()
	{
		return this.vorName;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getOID().hashCode();
	}

	/**
	 * @param name String
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @param oid Long
	 */
	public void setOID(final Long oid)
	{
		this.oid = oid;
	}

	/**
	 * @param vorName String
	 */
	public void setVorName(final String vorName)
	{
		this.vorName = vorName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getOID() + ": " + getName() + ", " + getVorName();
	}
}
