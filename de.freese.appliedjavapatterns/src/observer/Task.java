package observer;

/**
 * @author Thomas Freese
 */
public class Task
{
	/**
     * 
     */
	private String name = "";

	/**
     * 
     */
	private String notes = "";

	/**
     * 
     */
	private double timeRequired;

	/**
	 * Creates a new {@link Task} object.
	 */
	public Task()
	{
		super();
	}

	/**
	 * Creates a new {@link Task} object.
	 * 
	 * @param newName String
	 * @param newNotes String
	 * @param newTimeRequired double
	 */
	public Task(final String newName, final String newNotes, final double newTimeRequired)
	{
		super();

		this.name = newName;
		this.notes = newNotes;
		this.timeRequired = newTimeRequired;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return String
	 */
	public String getNotes()
	{
		return this.notes;
	}

	/**
	 * @return double
	 */
	public double getTimeRequired()
	{
		return this.timeRequired;
	}

	/**
	 * @param newName String
	 */
	public void setName(final String newName)
	{
		this.name = newName;
	}

	/**
	 * @param newNotes String
	 */
	public void setNotes(final String newNotes)
	{
		this.notes = newNotes;
	}

	/**
	 * @param newTimeRequired double
	 */
	public void setTimeRequired(final double newTimeRequired)
	{
		this.timeRequired = newTimeRequired;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.name + " " + this.notes;
	}
}
