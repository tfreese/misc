package builder;

/**
 * @author Thomas Freese
 */
public class InformationRequiredException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7553680038676070428L;

	/**
     * 
     */
	private static final String MESSAGE =
			"Appointment cannot be created because further information is required";

	/**
     * 
     */
	public static final int START_DATE_REQUIRED = 1;

	/**
     * 
     */
	public static final int END_DATE_REQUIRED = 2;

	/**
     * 
     */
	public static final int DESCRIPTION_REQUIRED = 4;

	/**
     * 
     */
	public static final int ATTENDEE_REQUIRED = 8;

	/**
     * 
     */
	public static final int LOCATION_REQUIRED = 16;

	/**
     * 
     */
	private int informationRequired;

	/**
	 * Creates a new {@link InformationRequiredException} object.
	 * 
	 * @param itemsRequired int
	 */
	public InformationRequiredException(final int itemsRequired)
	{
		super(MESSAGE);

		this.informationRequired = itemsRequired;
	}

	/**
	 * @return int
	 */
	public int getInformationRequired()
	{
		return this.informationRequired;
	}
}
