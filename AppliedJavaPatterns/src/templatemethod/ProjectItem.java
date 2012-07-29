package templatemethod;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public abstract class ProjectItem implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -876844681939788507L;

	/**
     * 
     */
	private String description;

	/**
     * 
     */
	private String name;

	/**
     * 
     */
	private double rate;

	/**
	 * Creates a new {@link ProjectItem} object.
	 */
	public ProjectItem()
	{
		super();
	}

	/**
	 * Creates a new {@link ProjectItem} object.
	 * 
	 * @param newName String
	 * @param newDescription String
	 * @param newRate String
	 */
	public ProjectItem(final String newName, final String newDescription, final double newRate)
	{
		super();

		this.name = newName;
		this.description = newDescription;
		this.rate = newRate;
	}

	/**
	 * @return double
	 */
	public final double getCostEstimate()
	{
		return (getTimeRequired() * getRate()) + getMaterialsCost();
	}

	/**
	 * @return String
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return double
	 */
	public abstract double getMaterialsCost();

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return double
	 */
	public double getRate()
	{
		return this.rate;
	}

	/**
	 * @return double
	 */
	public abstract double getTimeRequired();

	/**
	 * @param newDescription String
	 */
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @param newName String
	 */
	public void setName(final String newName)
	{
		this.name = newName;
	}

	/**
	 * @param newRate double
	 */
	public void setRate(final double newRate)
	{
		this.rate = newRate;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName();
	}
}
