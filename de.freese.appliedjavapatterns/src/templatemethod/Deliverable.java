package templatemethod;

/**
 * @author Thomas Freese
 */
public class Deliverable extends ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = -8222696631515466451L;

	/**
	 * 
	 */
	private double materialsCost;

	/**
	 * 
	 */
	private double productionTime;

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
	 * @param newMaterialsCost double
	 * @param newProductionTime double
	 * @param newRate double
	 */
	public Deliverable(final String newName, final String newDescription,
			final double newMaterialsCost, final double newProductionTime, final double newRate)
	{
		super(newName, newDescription, newRate);

		this.materialsCost = newMaterialsCost;
		this.productionTime = newProductionTime;
	}

	/**
	 * @see templatemethod.ProjectItem#getMaterialsCost()
	 */
	@Override
	public double getMaterialsCost()
	{
		return this.materialsCost;
	}

	/**
	 * @see templatemethod.ProjectItem#getTimeRequired()
	 */
	@Override
	public double getTimeRequired()
	{
		return this.productionTime;
	}

	/**
	 * @param newCost double
	 */
	public void setMaterialsCost(final double newCost)
	{
		this.materialsCost = newCost;
	}

	/**
	 * @param newTime double
	 */
	public void setProductionTime(final double newTime)
	{
		this.productionTime = newTime;
	}
}
