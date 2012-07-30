package visitor;

import java.util.List;

/**
 * @author Thomas Freese
 */
public class Deliverable implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = -919195435362864309L;

	/**
     * 
     */
	private String description;

	/**
     * 
     */
	private double materialsCost;

	/**
     * 
     */
	private String name;

	/**
     * 
     */
	private Contact owner;

	/**
     * 
     */
	private double productionCost;

	/**
	 * Creates a new {@link Deliverable} object.
	 */
	public Deliverable()
	{
		super();
	}

	/**
	 * Creates a new {@link Deliverable} object.
	 * 
	 * @param newName String
	 * @param newDescription String
	 * @param newOwner {@link Contact}
	 * @param newMaterialsCost double
	 * @param newProductionCost double
	 */
	public Deliverable(final String newName, final String newDescription, final Contact newOwner,
			final double newMaterialsCost, final double newProductionCost)
	{
		super();

		this.name = newName;
		this.description = newDescription;
		this.owner = newOwner;
		this.materialsCost = newMaterialsCost;
		this.productionCost = newProductionCost;
	}

	/**
	 * @see visitor.ProjectItem#accept(visitor.ProjectVisitor)
	 */
	@Override
	public void accept(final ProjectVisitor v)
	{
		v.visitDeliverable(this);
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
	public double getMaterialsCost()
	{
		return this.materialsCost;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return {@link Contact}
	 */
	public Contact getOwner()
	{
		return this.owner;
	}

	/**
	 * @return double
	 */
	public double getProductionCost()
	{
		return this.productionCost;
	}

	/**
	 * @see visitor.ProjectItem#getProjectItems()
	 */
	@Override
	public List<ProjectItem> getProjectItems()
	{
		return null;
	}

	/**
	 * @param newDescription String
	 */
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @param newCost double
	 */
	public void setMaterialsCost(final double newCost)
	{
		this.materialsCost = newCost;
	}

	/**
	 * @param newName String
	 */
	public void setName(final String newName)
	{
		this.name = newName;
	}

	/**
	 * @param newOwner {@link Contact}
	 */
	public void setOwner(final Contact newOwner)
	{
		this.owner = newOwner;
	}

	/**
	 * @param newCost double
	 */
	public void setProductionCost(final double newCost)
	{
		this.productionCost = newCost;
	}
}
